package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfml.program_builder.ProgramBuilder;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface BoolExpr extends Predicate<ProgramContext>, ASTNode, ToStringPretty {
    default void collectPositions(
            ProgramContext context,
            Consumer<BlockPos> posConsumer
    ) {

    }

    static BoolExpr from(String line) {
        // This is where you’d parse lines like:
        //   “a BOTTOM SIDE HAS EQ 1000 fe::”
        // Or something like: “b BOTTOM SIDE HAS EQ 0 fe::”
        Mutable<BoolExpr> rtn = new MutableObject<>();
        String programString = "EVERY 20 TICKS DO IF " + line + " THEN END END";
        ProgramBuilder
                .build(programString)
                .caseSuccess((program, metadata) -> {
                    BoolExpr condition = (
                            (IfStatement) program
                                    .triggers()
                                    .get(0)
                                    .getBlock()
                                    .getStatements()
                                    .get(0)
                    ).condition();
                    rtn.setValue(condition);
                })
                .caseFailure(result -> {
                    StringBuilder msg = new StringBuilder("Failed to compile program: ").append(programString);
                    msg.append('\n');
                    result.metadata().errors().forEach(e -> msg.append(e.toString()).append('\n'));
                    throw new IllegalStateException(msg.toString());
                });
        return rtn.getValue();
    }

}
