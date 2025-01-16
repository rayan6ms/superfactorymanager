package ca.teamdman.sfm.gametest.compat.multiple;

import ca.teamdman.sfm.SFM;
import com.buuz135.industrial.block.resourceproduction.tile.WashingFactoryTile;
import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.module.ModuleResourceProduction;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.fluid.MultiTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import mekanism.common.registries.MekanismBlocks;
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

@SuppressWarnings("OptionalGetWithoutIsPresent")
@GameTestHolder(SFM.MOD_ID)
public class SFMIndustrialForegoingMekanismGameTests {
    @SuppressWarnings("DanglingJavadoc")
    @GameTest(template = "3x4x3")
    public static void meat_fluid(GameTestHelper helper) {
        BlockPos managerPos = new BlockPos(1, 2, 0);
        BlockPos leftTankPos = new BlockPos(2, 2, 0);
        BlockPos rightTankPos = new BlockPos(0, 2, 0);
        BlockPos washingFactoryPos = new BlockPos(2, 2, 1);

        // set up the tanks
        helper.setBlock(leftTankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());
        helper.setBlock(rightTankPos, MekanismBlocks.BASIC_FLUID_TANK.getBlock());

        // set up the washing factory
        helper.setBlock(washingFactoryPos, ModuleResourceProduction.WASHING_FACTORY.getLeft().get());
        WashingFactoryTile washingFactory = (WashingFactoryTile) helper.getBlockEntity(washingFactoryPos);
        assert washingFactory != null;

        // configure it to push front
        /**
         * {@link com.hrznstudio.titanium.client.screen.addon.FacingHandlerScreenAddon#mouseClicked}
         * {@link com.hrznstudio.titanium.block.tile.ActiveTile#handleButtonMessage(int, Player, CompoundTag)}
         * {@link MultiTankComponent#handleFacingChange(String, FacingUtil.Sideness, int)}
         */
        CompoundTag click = new CompoundTag();
        click.putString("Name", "output");
        click.putString("Facing", FacingUtil.Sideness.FRONT.name());
//        click.putInt("Next", IFacingComponent.FaceMode.PUSH.getIndex());
        try {
            Field multiTankComponentField = ActiveTile.class.getDeclaredField("multiTankComponent");
            multiTankComponentField.setAccessible(true);
            MultiTankComponent<?> multiTankComponent = (MultiTankComponent<?>) multiTankComponentField.get(washingFactory);
            SidedFluidTankComponent<?> outputTank = (SidedFluidTankComponent<?>) multiTankComponent
                    .getTanks()
                    .stream()
                    .filter(tank -> tank.getName().equals("output"))
                    .findFirst()
                    .get();
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

        helper.succeed();
    }
}
