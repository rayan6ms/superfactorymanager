package ca.teamdman.sfm.common.capability;

import ca.teamdman.sfm.common.registry.SFMGlobalBlockCapabilityProviders;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import org.jetbrains.annotations.Nullable;

/// In NeoForge for Minecraft before 1.20.3, capabilities are queried from {@link BlockEntity}.
/// We wrap retrieved capabilities in {@link SFMBlockCapabilityResult}.
/// Capabilities are queried using a {@link SFMBlockCapabilityKind} like {@link SFMWellKnownCapabilities#ITEM_HANDLER}.
///
/// In Minecraft 1.20.3 and later, capabilities are instead queried from {@link Level}.
/// Additionally, mods can participate in the registration of capabilities to blocks in that version.
///
/// Prior to this version, for SFM to get a {@link SFMWellKnownCapabilities#FLUID_HANDLER} for {@link CauldronBlock},
/// it has its own mechanism for indirection via {@link SFMGlobalBlockCapabilityProviders}
///
/// For per-mod compat, like to fix <a href="https://github.com/TeamDman/SuperFactoryManager/issues/322">#322</a>,
/// SFM keeps this indirection mechanism for 1.20.3 and later as well.
///
/// TODO: Fix <a href="https://github.com/TeamDman/SuperFactoryManager/issues/352">#352</a> using this.
///
/// This class is used for both global registration of capabilities and local registration.
/// For example, SFM adds {@link CauldronBlock} support for {@link net.neoforged.neoforge.fluids.capability.IFluidHandler}
/// in a global way, which lets other mods use cauldrons as fluid containers without needing to add support themselves.
///
/// However, this class is also used for local registrations, which only SFM sees from the way SFM discovers capabilities.
public interface SFMBlockCapabilityProvider {
    @Nullable IBlockCapabilityProvider<?, @Nullable Direction> createForKind(SFMBlockCapabilityKind<?> capabilityKind);

    ///  Higher priority providers are checked first. The Default priority is 0.
    default int priority() {
        return 0;
    }
}
