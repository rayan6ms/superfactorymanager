package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;
import java.util.Objects;

public record Interval(
        Number interval,

        DurationUnit intervalUnit,

        IntervalAlignment alignment,

        Number offset,

        DurationUnit offsetUnit
) implements ASTNode {
    public boolean shouldTick(ProgramContext context) {

        final ManagerBlockEntity manager = context.manager();
        if (manager == null) return false;
        return switch (alignment) {
            case LOCAL -> manager.getTick() % intervalTicks() == offsetTicks();
            case GLOBAL -> Objects.requireNonNull(manager.getLevel()).getGameTime() % intervalTicks() == offsetTicks();
        };
    }

    public int intervalTicks() {

        return intervalUnit.toTicks(interval);
    }

    public int offsetTicks() {

        return offsetUnit.toTicks(offset);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(interval).append(" ").append(intervalUnit);
        if (alignment == IntervalAlignment.GLOBAL) {
            sb.append(" GLOBAL");
        }
        if (offset.value() != 0) {
            sb.append(" OFFSET BY ").append(offset);
            if (offsetUnit != intervalUnit) {
                sb.append(" ").append(offsetUnit);
            }
        }
        return sb.toString();
    }

    @Override
    public List<? extends ASTNode> getChildNodes() {

        return List.of(interval, intervalUnit, alignment, offset, offsetUnit);
    }

    public enum IntervalAlignment implements ASTNode{
        LOCAL,
        GLOBAL;

        @Override
        public List<? extends ASTNode> getChildNodes() {

            return List.of();
        }
    }

}
