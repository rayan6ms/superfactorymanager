package ca.teamdman.sfm.gametest.compat.multiple;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.gametest.SFMGameTestBase;
import com.buuz135.industrial.block.resourceproduction.tile.WashingFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.client.screen.addon.FacingHandlerScreenAddon;
import com.hrznstudio.titanium.component.fluid.MultiTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tile.TileEntityFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;

import java.lang.reflect.Field;
import java.util.Objects;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@GameTestHolder(SFM.MOD_ID)
public class SFMIndustrialForegoingMekanismGameTests extends SFMGameTestBase {
    @SuppressWarnings("DanglingJavadoc")
    @GameTest(template = "3x4x3", timeoutTicks = 200)
    public static void meat_fluid(GameTestHelper helper) {
        BlockPos managerPos = new BlockPos(1, 2, 0);
        BlockPos leftTankPos = new BlockPos(2, 2, 0);
        BlockPos rightTankPos = new BlockPos(0, 2, 0);
        BlockPos washingFactoryPos = new BlockPos(2, 2, 1);

        // set up the tanks
        helper.setBlock(leftTankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(rightTankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        TileEntityFluidTank rightTank = (TileEntityFluidTank) helper.getBlockEntity(rightTankPos);
        assert rightTank != null;
        IExtendedFluidTank fluidTank = rightTank.getFluidTank(0, Direction.DOWN);
        assert fluidTank != null;

        // set up the washing factory
        helper.setBlock(washingFactoryPos, ModuleResourceProduction.WASHING_FACTORY.getLeft().get());
        WashingFactoryTile washingFactory = (WashingFactoryTile) helper.getBlockEntity(washingFactoryPos);
        assert washingFactory != null;

        // configure it to push front
        /**
         * {@link FacingHandlerScreenAddon#mouseClicked}
         * {@link ActiveTile#handleButtonMessage(int, Player, CompoundTag)}
         * {@link MultiTankComponent#handleFacingChange(String, FacingUtil.Sideness, int)}
         */
        CompoundTag click = new CompoundTag();
        click.putString("Name", "output");
        click.putString("Facing", FacingUtil.Sideness.FRONT.name());
//        click.putInt("Next", IFacingComponent.FaceMode.PUSH.getIndex());
        try {
            Field multiTankComponentField = ActiveTile.class.getDeclaredField("multiTankComponent");
            multiTankComponentField.setAccessible(true);
            MultiTankComponent<?> multiTankComponent = (MultiTankComponent<?>) multiTankComponentField.get(
                    washingFactory);
            SidedFluidTankComponent<?> outputTank = (SidedFluidTankComponent<?>) multiTankComponent
                    .getTanks()
                    .stream()
                    .filter(tank -> tank.getName().equals("output"))
                    .findFirst()
                    .get();
            outputTank.getFacingModes().put(FacingUtil.Sideness.BOTTOM, IFacingComponent.FaceMode.NONE);
            IFacingComponent.FaceMode[] validFacingModes = outputTank.getValidFacingModes();
            for (int i = 0; i < validFacingModes.length; i++) {
                if (validFacingModes[i] == IFacingComponent.FaceMode.PUSH) {
                    click.putInt("Next", i);
                    break;
                }
            }
            if (!click.contains("Next")) {
                throw new RuntimeException("No valid facing mode found");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        //noinspection DataFlowIssue
        washingFactory.handleButtonMessage(-1, null, click);

        // add some power
        washingFactory
                .getCapability(ForgeCapabilities.ENERGY)
                .resolve()
                .get().receiveEnergy(Integer.MAX_VALUE, false);

        // add some meat
        washingFactory.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .resolve().get().fill(
                        new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), Integer.MAX_VALUE),
                        IFluidHandler.FluidAction.EXECUTE
                );

        // add some iron
        washingFactory
                .getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP)
                .resolve()
                .get()
                .insertItem(0, new ItemStack(Items.RAW_IRON, 64), false);

        // place the manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));
        assert manager != null;
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "bruh"
                                   EVERY 20 TICKS DO
                                    INPUT fluid:: FROM a BOTTOM SIDE
                                    OUTPUT fluid:: TO b TOP SIDE
                                   END
                                   """.stripTrailing().stripIndent());
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(leftTankPos))
                .add("b", helper.absolutePos(rightTankPos))
                .save(Objects.requireNonNull(manager.getDisk()));

        helper.succeedWhen(() -> {
            if (fluidTank.getFluidAmount() < 100) {
                helper.fail("Fluid tank did not receive fluid");
            }
        });
    }

    @GameTest(template = "3x4x3", timeoutTicks = 200)
    public static void meat_fluid_direct(GameTestHelper helper) {
        BlockPos managerPos = new BlockPos(1, 2, 1);
        BlockPos rightTankPos = new BlockPos(0, 2, 1);
        BlockPos washingFactoryPos = new BlockPos(2, 2, 1);

        // set up the tanks
        helper.setBlock(rightTankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        TileEntityFluidTank rightTank = (TileEntityFluidTank) helper.getBlockEntity(rightTankPos);
        assert rightTank != null;
        IExtendedFluidTank fluidTank = rightTank.getFluidTank(0, Direction.DOWN);
        assert fluidTank != null;

        // set up the washing factory
        helper.setBlock(washingFactoryPos, ModuleResourceProduction.WASHING_FACTORY.getLeft().get());
        WashingFactoryTile washingFactory = (WashingFactoryTile) helper.getBlockEntity(washingFactoryPos);
        assert washingFactory != null;

        // add some power
        washingFactory
                .getCapability(ForgeCapabilities.ENERGY)
                .resolve()
                .get().receiveEnergy(Integer.MAX_VALUE, false);

        // add some meat
        washingFactory.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .resolve().get().fill(
                        new FluidStack(ModuleCore.MEAT.getSourceFluid().get(), Integer.MAX_VALUE),
                        IFluidHandler.FluidAction.EXECUTE
                );

        // add some iron
        washingFactory
                .getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP)
                .resolve()
                .get()
                .insertItem(0, new ItemStack(Items.RAW_IRON, 64), false);

        // place the manager
        helper.setBlock(managerPos, SFMBlocks.MANAGER_BLOCK.get());
        ManagerBlockEntity manager = ((ManagerBlockEntity) helper.getBlockEntity(managerPos));
        assert manager != null;
        manager.setItem(0, new ItemStack(SFMItems.DISK_ITEM.get()));
        manager.setProgram("""
                                   NAME "bruh"
                                   EVERY 20 TICKS DO
                                    INPUT fluid:: FROM a BOTTOM SIDE
                                    OUTPUT fluid:: TO b TOP SIDE
                                   END
                                   """.stripTrailing().stripIndent());
        LabelPositionHolder.empty()
                .add("a", helper.absolutePos(washingFactoryPos))
                .add("b", helper.absolutePos(rightTankPos))
                .save(Objects.requireNonNull(manager.getDisk()));

        helper.succeedWhen(() -> {
            if (fluidTank.getFluidAmount() < 100) {
                helper.fail("Fluid tank did not receive fluid");
            }
        });
    }
}
