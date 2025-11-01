package ca.teamdman.sfm.common.event_bus;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;

/// Used to reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}.
public class SFMEventBus {

    public static IEventBus getEventBus(Bus target) {

        if (target == Target.MOD) {
            return FMLJavaModLoadingContext.get().getModEventBus();
        } else if (target == Target.GAME) {
            return NeoForge.EVENT_BUS;
        } else {
            throw new IllegalArgumentException("Invalid target: " + target);
        }
    }

    public static class Target {

        public static final Bus MOD = Bus.MOD;

        public static final Bus GAME = Bus.FORGE;

    }

}
