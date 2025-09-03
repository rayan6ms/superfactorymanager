package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.common.util.SFMResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SFMBlockTags {
    public static final TagKey<Block> ANVIL_DISENCHANTING = BlockTags.create(
            SFMResourceLocation.fromSFMPath("anvil_disenchanting"));
    public static final TagKey<Block> ANVIL_PRINTING_PRESS_FORMING = BlockTags.create(
            SFMResourceLocation.fromSFMPath("anvil_printing_press_forming"));

    @SuppressWarnings("deprecation")
    public static boolean blockHasTag(Block block, TagKey<Block> tag) {
        return block.builtInRegistryHolder().tags().anyMatch(tag::equals);
    }
}
