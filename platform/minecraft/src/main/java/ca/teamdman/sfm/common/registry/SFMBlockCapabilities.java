package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.capability.BufferBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.CauldronBlockCapabilityProvider;
import ca.teamdman.sfm.common.capability.SFMBlockCapabilityKind;
import ca.teamdman.sfm.common.capability.SFMWellKnownCapabilities;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
@MCVersionDependentBehaviour // 1.20.3+
public class SFMBlockCapabilities {

    @SuppressWarnings("Convert2Lambda")
    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.INVENTORY
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                SFMBlockEntities.WATER_TANK_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.TANK
        );

        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                new IBlockCapabilityProvider<>() {
                    @Override
                    public @Nullable IItemHandler getCapability(
                            Level level,
                            BlockPos pos,
                            BlockState state,
                            @Nullable BlockEntity blockEntity,
                            Direction context
                    ) {

                        if (blockEntity instanceof BarrelBlockEntity bbe) {
                            return new InvWrapper(bbe);
                        }
                        return null;
                    }
                },
                SFMBlocks.TEST_BARREL_BLOCK.get()
        );

        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                new IBlockCapabilityProvider<>() {
                    @Override
                    public @Nullable IItemHandler getCapability(
                            Level level,
                            BlockPos pos,
                            BlockState state,
                            @Nullable BlockEntity blockEntity,
                            Direction context
                    ) {

                        if (blockEntity instanceof BarrelBlockEntity bbe) {
                            return new InvWrapper(bbe);
                        }
                        return null;
                    }
                },
                SFMBlocks.TEST_BARREL_BLOCK.get()
        );

        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                SFMBlockEntities.TEST_BARREL_TANK_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> blockEntity.getTank()
        );

        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                new CauldronBlockCapabilityProvider(),
                Blocks.CAULDRON,
                Blocks.LAVA_CAULDRON,
                Blocks.WATER_CAULDRON
        );

        event.registerBlockEntity(
                SFMWellKnownCapabilities.ITEM_HANDLER.capabilityKind(),
                SFMBlockEntities.MANAGER_BLOCK_ENTITY.get(),
                (manager, direction) -> manager.invWrapper
        );

        SFMResourceTypes.registry().values().forEach(resourceType -> {
            registerCapabilitiesForResourceType(event, resourceType);
        });

    }

    private static <STACK, ITEM, CAP> void registerCapabilitiesForResourceType(
            RegisterCapabilitiesEvent event,
            ResourceType<STACK, ITEM, CAP> resourceType
    ) {

        BlockCapability<CAP, @Nullable Direction> resourceTypeBlockCapability = resourceType
                .capabilityKind()
                .capabilityKind();

        IBlockCapabilityProvider<CAP, @Nullable Direction> capabilityProxy = createCapabilityProxy(
                resourceTypeBlockCapability);

        for (SFMRegistryObject<Block, ?> tunnelledBlock : List.of(
                SFMBlocks.TUNNELLED_MANAGER_BLOCK,
                SFMBlocks.TUNNELLED_CABLE_BLOCK,
                SFMBlocks.TUNNELLED_CABLE_FACADE_BLOCK,
                SFMBlocks.TUNNELLED_FANCY_CABLE_BLOCK,
                SFMBlocks.TUNNELLED_FANCY_CABLE_FACADE_BLOCK
        )) {

            event.registerBlock(
                    resourceTypeBlockCapability,
                    capabilityProxy,
                    tunnelledBlock.get()
            );
        }

        BufferBlockCapabilityProvider bufferCapabilityProvider = new BufferBlockCapabilityProvider();
        @SuppressWarnings({"unchecked", "rawtypes"})
        IBlockCapabilityProvider<?, @Nullable Direction> providerForKind = bufferCapabilityProvider
                .specialize((SFMBlockCapabilityKind) resourceType.capabilityKind());

        event.registerBlockEntity(
                resourceTypeBlockCapability,
                SFMBlockEntities.BUFFER_BLOCK_ENTITY.get(),
                (blockEntity, direction) -> {
                    //noinspection unchecked
                    return (CAP) providerForKind.getCapability(
                            blockEntity.getLevel(),
                            blockEntity.getBlockPos(),
                            blockEntity.getBlockState(),
                            blockEntity,
                            direction
                    );
                }
        );
    }

    @SuppressWarnings("Convert2Lambda")
    private static <T> IBlockCapabilityProvider<T, @Nullable Direction> createCapabilityProxy(BlockCapability<?, @Nullable Direction> cap) {

        return new IBlockCapabilityProvider<>() {
            @Override
            public @Nullable T getCapability(
                    Level level,
                    BlockPos blockPos,
                    BlockState blockState,
                    @Nullable BlockEntity blockEntity,
                    @Nullable Direction side
            ) {

                if (side == null) return null;
                var offset = blockPos.relative(side.getOpposite());
                //noinspection unchecked
                return (T) level.getCapability(cap, offset, side);
            }
        };
    }

}
