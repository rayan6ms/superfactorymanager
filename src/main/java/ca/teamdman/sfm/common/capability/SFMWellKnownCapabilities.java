package ca.teamdman.sfm.common.capability;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

/// In between Forge for Minecraft 1.19.2 and NeoForge for Minecraft 1.20.3,
/// the {@code ForgeCapabilities} class is changed to {@code BuiltInCapabilities}
/// and later again to {@code Capabilities.ItemHandler.BLOCK}
public class SFMWellKnownCapabilities {
    public static final SFMBlockCapabilityKind<IEnergyStorage> ENERGY
            = new SFMBlockCapabilityKind<>(ForgeCapabilities.ENERGY);
    public static final SFMBlockCapabilityKind<IFluidHandler> FLUID_HANDLER
            = new SFMBlockCapabilityKind<>(ForgeCapabilities.FLUID_HANDLER);
    public static final SFMBlockCapabilityKind<IItemHandler> ITEM_HANDLER
            = new SFMBlockCapabilityKind<>(ForgeCapabilities.ITEM_HANDLER);
}
