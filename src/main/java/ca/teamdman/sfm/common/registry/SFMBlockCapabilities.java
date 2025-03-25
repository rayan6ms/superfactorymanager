package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockcapabilityprovider.CauldronBlockCapabilityProvider;
import ca.teamdman.sfm.common.util.SFMDirections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SFMBlockCapabilities {

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
//        event.registerBlockEntity(
//                Capabilities.EnergyStorage.BLOCK,
//                SFMBlockEntities.BATTERY_BLOCK_ENTITY.get(),
//                (blockEntity, direction) -> blockEntity.CONTAINER
//        );
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
//        SFMResourceTypes.getCapabilities().forEach(cap -> {
//            event.registerBlock(
//                    cap,
//                    createProvider(cap),
//                    SFMBlocks.TUNNELLED_MANAGER_BLOCK.get()
//            );
//        });
    }

    private static <T> IBlockCapabilityProvider<T, @Nullable Direction> createProvider(BlockCapability<?, @Nullable Direction> cap) {
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

    public static boolean hasAnyCapabilityAnyDirection(ILevelExtension level, BlockPos pos) {
        return SFMResourceTypes.getCapabilities().anyMatch(cap -> {
            for (Direction direction : SFMDirections.DIRECTIONS_WITH_NULL) {
                if (level.getCapability(cap, pos, direction) != null) {
                    return true;
                }
            }
            return false;
        });
    }

    public static boolean hasAnyCapability(ILevelExtension level, BlockPos pos, @Nullable Direction direction) {
        return SFMResourceTypes.getCapabilities().anyMatch(cap -> level.getCapability(cap, pos, direction) != null);
    }
}
