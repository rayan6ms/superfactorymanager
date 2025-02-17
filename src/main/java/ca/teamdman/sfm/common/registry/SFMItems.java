package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SFM.MOD_ID);
    public static final RegistryObject<BlockItem> MANAGER_ITEM = register("manager", SFMBlocks.MANAGER_BLOCK);
//    public static final RegistryObject<BlockItem> TUNNELLED_MANAGER_ITEM = register(
//            "tunnelled_manager",
//            SFMBlocks.TUNNELLED_MANAGER_BLOCK
//    );
    public static final RegistryObject<BlockItem> CABLE_ITEM = register("cable", SFMBlocks.CABLE_BLOCK);
    public static final RegistryObject<BlockItem> FANCY_CABLE_ITEM = register("fancy_cable", SFMBlocks.FANCY_CABLE_BLOCK);
    public static final RegistryObject<PrintingPressBlockItem> PRINTING_PRESS_ITEM = ITEMS.register(
            "printing_press",
            PrintingPressBlockItem::new
    );
    //    public static final  RegistryObject<Item>   BATTERY_ITEM    = register("battery", SFMBlocks.BATTERY_BLOCK);
    public static final RegistryObject<BlockItem> WATER_TANK_ITEM = register("water_tank", SFMBlocks.WATER_TANK_BLOCK);
    public static final RegistryObject<DiskItem> DISK_ITEM = ITEMS.register("disk", DiskItem::new);
    public static final RegistryObject<LabelGunItem> LABEL_GUN_ITEM = ITEMS.register(
            "labelgun", // TODO: rename on a major version update to label_gun
            LabelGunItem::new
    );
    public static final RegistryObject<NetworkToolItem> NETWORK_TOOL_ITEM = ITEMS.register(
            "network_tool",
            NetworkToolItem::new
    );


    public static final RegistryObject<FormItem> FORM_ITEM = ITEMS.register("form", FormItem::new);
    public static final RegistryObject<ExperienceShardItem> EXPERIENCE_SHARD_ITEM = ITEMS.register(
            "xp_shard",
            ExperienceShardItem::new
    );
    public static final RegistryObject<ExperienceGoopItem> EXPERIENCE_GOOP_ITEM = ITEMS.register(
            "xp_goop",
            ExperienceGoopItem::new
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static CreativeModeTab tab;

    private static RegistryObject<BlockItem> register(
            String name,
            RegistryObject<? extends Block> block
    ) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    @SubscribeEvent
    public static void onRegister(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(new ResourceLocation(SFM.MOD_ID, "main"), builder ->
                // Set name of tab to display
                builder.title(Component.translatable("item_group." + SFM.MOD_ID + ".main"))
                        // Set icon of creative tab
                        .icon(() -> new ItemStack(SFMBlocks.MANAGER_BLOCK.get()))
                        // Add default items to tab
                        .displayItems((params, output) -> output.acceptAll(ITEMS
                                                                                   .getEntries()
                                                                                   .stream()
                                                                                   .map(RegistryObject::get)
                                                                                   .map(ItemStack::new)
                                                                                   .toList())));
    }
}
