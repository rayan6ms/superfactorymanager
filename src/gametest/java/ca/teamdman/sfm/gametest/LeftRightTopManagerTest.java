package ca.teamdman.sfm.gametest;


import ca.teamdman.sfm.common.util.NotStored;
import net.minecraft.core.BlockPos;

@SuppressWarnings("unused")
public class LeftRightTopManagerTest extends LeftRightManagerTest {
    public LeftRightTopManagerTest(SFMGameTestHelper helper) {
        super(helper);
    }

    @Override
    protected void setupStructure(@NotStored BlockPos offset) {
        setupChests(offset.offset(0, 0, 1));
        setupManager(offset.offset(0, 0, 1));
    }

    @Override
    protected void setupChests(@NotStored BlockPos offset) {
        super.setupChests(offset);
        addChest("top", new BlockPos(1, 3, 0).offset(offset));
    }
}
