package ca.teamdman.sfm.common.capability;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public class SFMWellKnownCapabilities {
    public static final SFMBlockCapabilityWrapper<IEnergyStorage> ENERGY
            = new SFMBlockCapabilityWrapper<>(ForgeCapabilities.ENERGY);
    public static final SFMBlockCapabilityWrapper<IFluidHandler> FLUID_HANDLER
            = new SFMBlockCapabilityWrapper<>(ForgeCapabilities.FLUID_HANDLER);
    public static final SFMBlockCapabilityWrapper<IItemHandler> ITEM_HANDLER
            = new SFMBlockCapabilityWrapper<>(ForgeCapabilities.ITEM_HANDLER);
}
