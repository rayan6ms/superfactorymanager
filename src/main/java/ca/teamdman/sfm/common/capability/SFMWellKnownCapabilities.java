package ca.teamdman.sfm.common.capability;


import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class SFMWellKnownCapabilities {
    public static final SFMBlockCapabilityWrapper<IEnergyStorage> ENERGY
            = new SFMBlockCapabilityWrapper<>(Capabilities.ENERGY);
    public static final SFMBlockCapabilityWrapper<IFluidHandler> FLUID_HANDLER
            = new SFMBlockCapabilityWrapper<>(Capabilities.FLUID_HANDLER);
    public static final SFMBlockCapabilityWrapper<IItemHandler> ITEM_HANDLER
            = new SFMBlockCapabilityWrapper<>(Capabilities.ITEM_HANDLER);
}
