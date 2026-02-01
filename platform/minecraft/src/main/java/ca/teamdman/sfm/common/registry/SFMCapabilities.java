package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.common.capability.IRedstoneSignalStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.capabilities.RegisterCapabilitiesEvent;

public class SFMCapabilities {
    @SFMSubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IRedstoneSignalStorage.class);
    }
}
