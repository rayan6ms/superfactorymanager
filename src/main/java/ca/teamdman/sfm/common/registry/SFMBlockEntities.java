package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;

@SuppressWarnings("DataFlowIssue")
public final class SFMBlockEntities {
    private static final SFMDeferredRegister<BlockEntityType<?>> REGISTERER
            = SFMDeferredRegister.createForExistingRegistry(SFMWellKnownRegistries.BLOCK_ENTITY_TYPES, SFM.MOD_ID);

    public static final SFMRegistryObject<BlockEntityType<TestBarrelBlockEntity>> TEST_BARREL_BLOCK_ENTITY
            = REGISTERER.register(
            "test_barrel",
            () -> BlockEntityType.Builder
                    .of(TestBarrelBlockEntity::new, SFMBlocks.TEST_BARREL_BLOCK.get())
                    .build(null)
    );

    public static void register(IEventBus bus) {
        REGISTERER.register(bus);
    }

    public static final SFMRegistryObject<BlockEntityType<ManagerBlockEntity>> MANAGER_BLOCK_ENTITY
            = REGISTERER.register(
            "manager",
            () -> BlockEntityType.Builder
                    .of(ManagerBlockEntity::new, SFMBlocks.MANAGER_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<BufferBlockEntity>> BUFFER_BLOCK_ENTITY
            = REGISTERER.register(
            "buffer",
            () -> BlockEntityType.Builder
                    .of(BufferBlockEntity::new, SFMBlocks.BUFFER_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<TunnelledManagerBlockEntity>> TUNNELLED_MANAGER_BLOCK_ENTITY
            = REGISTERER.register(
            "tunnelled_manager",
            () -> BlockEntityType.Builder
                    .of(TunnelledManagerBlockEntity::new, SFMBlocks.TUNNELLED_MANAGER_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<CableFacadeBlockEntity>> CABLE_FACADE_BLOCK_ENTITY
            = REGISTERER.register(
            "cable_facade",
            () -> BlockEntityType.Builder
                    .of(CableFacadeBlockEntity::new, SFMBlocks.CABLE_FACADE_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<FancyCableFacadeBlockEntity>> FANCY_CABLE_FACADE_BLOCK_ENTITY
            = REGISTERER.register(
            "fancy_cable_facade",
            () -> BlockEntityType.Builder
                    .of(FancyCableFacadeBlockEntity::new, SFMBlocks.FANCY_CABLE_FACADE_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<PrintingPressBlockEntity>> PRINTING_PRESS_BLOCK_ENTITY
            = REGISTERER.register(
            "printing_press",
            () -> BlockEntityType.Builder
                    .of(PrintingPressBlockEntity::new, SFMBlocks.PRINTING_PRESS_BLOCK.get())
                    .build(null)
    );

    public static final SFMRegistryObject<BlockEntityType<WaterTankBlockEntity>> WATER_TANK_BLOCK_ENTITY
            = REGISTERER.register(
            "water_tank",
            () -> BlockEntityType.Builder
                    .of(WaterTankBlockEntity::new, SFMBlocks.WATER_TANK_BLOCK.get())
                    .build(null)
    );


    public static final SFMRegistryObject<BlockEntityType<TestBarrelTankBlockEntity>> TEST_BARREL_TANK_BLOCK_ENTITY
            = REGISTERER.register(
            "test_barrel_tank",
            () -> BlockEntityType.Builder
                    .of(TestBarrelTankBlockEntity::new, SFMBlocks.TEST_BARREL_BLOCK.get())
                    .build(null)
    );
}
