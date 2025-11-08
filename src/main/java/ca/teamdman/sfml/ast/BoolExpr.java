package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;
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
        String program = "EVERY 20 TICKS DO IF " + line + " THEN END END";
        Program.compile(
                program,
                success -> {
                    BoolExpr condition = (
                            (IfStatement) success
                                    .triggers()
                                    .get(0)
                                    .getBlock()
                                    .getStatements()
                                    .get(0)
                    ).condition();
                    rtn.setValue(condition);
                },
                failure -> {
                    StringBuilder msg = new StringBuilder("Failed to compile program: ").append(program);
                    msg.append('\n');
                    failure.forEach(e -> msg.append(e.toString()).append('\n'));
                    throw new IllegalStateException(msg.toString());
                }
        );
        return rtn.getValue();
    }

}
