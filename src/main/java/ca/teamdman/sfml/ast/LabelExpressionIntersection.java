package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.label.LabelPositionHolder;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public record LabelExpressionIntersection(
        LabelExpression left,
        LabelExpression right
) implements LabelExpression {
    @Override
    public Set<BlockPos> evaluate(LabelPositionHolder labelPositionHolder) {
        Set<BlockPos> leftPositions = left().evaluate(labelPositionHolder);
        Set<BlockPos> rightPositions = right().evaluate(labelPositionHolder);
        leftPositions.retainAll(rightPositions);
        return leftPositions;
    }

    @Override
    public void visitLabels(Consumer<Label> consumer) {
        left.visitLabels(consumer);
        right.visitLabels(consumer);
    }

    @Override
    public List<LabelExpression> getChildNodes() {

        return List.of(left, right);
    }

}
