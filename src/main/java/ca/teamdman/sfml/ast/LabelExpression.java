package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.label.LabelPositionHolder;
import net.minecraft.core.BlockPos;

import java.util.Set;

public interface LabelExpression extends ASTNode {
    Set<BlockPos> getPositions(LabelPositionHolder labelPositionHolder);
}
