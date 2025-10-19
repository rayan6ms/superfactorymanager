package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.*;
import ca.teamdman.sfm.common.util.SFMEnvironmentUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;

public class SFMItems {
    public static final SFMDeferredRegister<Item> REGISTERER = SFMDeferredRegister.createForExistingRegistry(
            SFMWellKnownRegistries.ITEMS,
            SFM.MOD_ID
    );

    public static final SFMRegistryObject<BlockItem> MANAGER_ITEM = register("manager", SFMBlocks.MANAGER_BLOCK);

    public static final SFMRegistryObject<BlockItem> TUNNELLED_MANAGER_ITEM = register(
            "tunnelled_manager",
            SFMBlocks.TUNNELLED_MANAGER_BLOCK
    );

    public static final SFMRegistryObject<BlockItem> CABLE_ITEM = register("cable", SFMBlocks.CABLE_BLOCK);

    public static final SFMRegistryObject<BlockItem> FANCY_CABLE_ITEM = register(
            "fancy_cable",
            SFMBlocks.FANCY_CABLE_BLOCK
    );

    public static final SFMRegistryObject<PrintingPressBlockItem> PRINTING_PRESS_ITEM = REGISTERER.register(
            "printing_press",
            PrintingPressBlockItem::new
    );

    public static final SFMRegistryObject<BlockItem> WATER_TANK_ITEM = register(
            "water_tank",
            SFMBlocks.WATER_TANK_BLOCK
    );

    public static final SFMRegistryObject<DiskItem> DISK_ITEM = REGISTERER.register("disk", DiskItem::new);

    public static final SFMRegistryObject<LabelGunItem> LABEL_GUN_ITEM = REGISTERER.register(
            "labelgun",
            () -> new LabelGunItem(new Item.Properties().stacksTo(1))
    );

    public static final SFMRegistryObject<NetworkToolItem> NETWORK_TOOL_ITEM = REGISTERER.register(
            "network_tool",
            NetworkToolItem::new
    );

    public static final SFMRegistryObject<FormItem> FORM_ITEM = REGISTERER.register("form", FormItem::new);

    public static final SFMRegistryObject<ExperienceShardItem> EXPERIENCE_SHARD_ITEM = REGISTERER.register(
            "xp_shard",
            ExperienceShardItem::new
    );

    public static final SFMRegistryObject<ExperienceGoopItem> EXPERIENCE_GOOP_ITEM = REGISTERER.register(
            "xp_goop",
            ExperienceGoopItem::new
    );

    public static SFMRegistryObject<BlockItem> BUFFER_ITEM = null;

    static {
        if (SFMEnvironmentUtils.isInIDE()) {
            BUFFER_ITEM = register("buffer", SFMBlocks.BUFFER_BLOCK);
        }
    }

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    private static SFMRegistryObject<BlockItem> register(
            String name,
            SFMRegistryObject<? extends Block> block
    ) {
        return REGISTERER.register(
                name,
                () -> new BlockItem(block.get(), new Item.Properties())
        );
    }
}
