package ca.teamdman.sfm.common.event_bus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/// Used to reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}.
public class SFMEventBus {

    public static IEventBus getEventBus(Bus target) {

        if (target == Target.MOD) {
            return FMLJavaModLoadingContext.get().getModEventBus();
        } else if (target == Target.GAME) {
            return MinecraftForge.EVENT_BUS;
        } else {
            throw new IllegalArgumentException("Invalid target: " + target);
        }
    }

    public static class Target {

        public static final Bus MOD = Bus.MOD;

        public static final Bus GAME = Bus.FORGE;

    }
}
