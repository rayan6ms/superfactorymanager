package ca.teamdman.sfm.common.capability;


import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class SFMWellKnownCapabilities {
    public static final SFMBlockCapabilityWrapper<IEnergyStorage> ENERGY
            = new SFMBlockCapabilityWrapper<>(Capabilities.EnergyStorage.BLOCK);
    public static final SFMBlockCapabilityWrapper<IFluidHandler> FLUID_HANDLER
            = new SFMBlockCapabilityWrapper<>(Capabilities.FluidHandler.BLOCK);
    public static final SFMBlockCapabilityWrapper<IItemHandler> ITEM_HANDLER
            = new SFMBlockCapabilityWrapper<>(Capabilities.ItemHandler.BLOCK);
}
