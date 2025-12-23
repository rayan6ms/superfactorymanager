package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.ProgramContext;
import net.minecraft.world.level.Level;

import java.util.List;

public record BoolRedstone(ComparisonOperator operator, Number number) implements BoolExpr {
    @Override
    public boolean test(ProgramContext programContext) {

        ManagerBlockEntity manager = programContext.manager();
        Level level = manager.getLevel();
        assert level != null;
        long lhs = level.getBestNeighborSignal(manager.getBlockPos());
        long rhs = number.value();
        return operator.test(lhs, rhs);
    }

    @Override
    public String toString() {
        return "REDSTONE " + operator + " " + number;
    }

    @Override
    public List<? extends ASTNode> getChildNodes() {

        return List.of(operator, number);
    }

}
