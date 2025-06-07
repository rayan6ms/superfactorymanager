package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.util.Stored;
import net.minecraft.core.BlockPos;

public class LeftRightManagerTest extends SFMTestBuilder {
    public LeftRightManagerTest(SFMGameTestHelper helper) {
        super(helper);
    }

    @Override
    protected void setupStructure(@Stored BlockPos offset) {
        setupChests(offset);
        setupManager(offset);
    }

    protected void setupChests(@Stored BlockPos offset) {
        addChest("left", new BlockPos(2, 2, 0).offset(offset));
        addChest("right", new BlockPos(0, 2, 0).offset(offset));
    }
}

