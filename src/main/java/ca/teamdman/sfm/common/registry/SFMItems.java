package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, SFM.MOD_ID);
    public static final Supplier<BlockItem> MANAGER_ITEM = register("manager", SFMBlocks.MANAGER_BLOCK);
//    public static final Supplier<BlockItem> TUNNELLED_MANAGER_ITEM = register(
//            "tunnelled_manager",
//            SFMBlocks.TUNNELLED_MANAGER_BLOCK
//    );
    public static final Supplier<BlockItem> CABLE_ITEM = register("cable", SFMBlocks.CABLE_BLOCK);
    public static final Supplier<BlockItem> FANCY_CABLE_ITEM = register(
            "fancy_cable",
            SFMBlocks.FANCY_CABLE_BLOCK
    );
    public static final Supplier<PrintingPressBlockItem> PRINTING_PRESS_ITEM = ITEMS.register(
            "printing_press",
            PrintingPressBlockItem::new
    );
    //    public static final  Supplier<Item>   BATTERY_ITEM    = register("battery", SFMBlocks.BATTERY_BLOCK);
    public static final Supplier<BlockItem> WATER_TANK_ITEM = register("water_tank", SFMBlocks.WATER_TANK_BLOCK);
    public static final Supplier<DiskItem> DISK_ITEM = ITEMS.register("disk", DiskItem::new);
    public static final Supplier<LabelGunItem> LABEL_GUN_ITEM = ITEMS.register(
            "labelgun", // TODO: rename on a major version update to label_gun
            LabelGunItem::new
    );
    public static final Supplier<NetworkToolItem> NETWORK_TOOL_ITEM = ITEMS.register(
            "network_tool",
            NetworkToolItem::new
    );


    public static final Supplier<FormItem> FORM_ITEM = ITEMS.register("form", FormItem::new);
    public static final Supplier<ExperienceShardItem> EXPERIENCE_SHARD_ITEM = ITEMS.register(
            "xp_shard",
            ExperienceShardItem::new
    );
    public static final Supplier<ExperienceGoopItem> EXPERIENCE_GOOP_ITEM = ITEMS.register(
            "xp_goop",
            ExperienceGoopItem::new
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private static Supplier<BlockItem> register(
            String name,
            Supplier<? extends Block> block
    ) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void populateMainCreativeTab(
            @SuppressWarnings("unused") CreativeModeTab.ItemDisplayParameters params,
            CreativeModeTab.Output output
    ) {
        output.acceptAll(SFMItems.ITEMS
                                 .getEntries()
                                 .stream()
                                 .map(Supplier::get)
                                 .map(ItemStack::new)
                                 .toList());
    }
}
