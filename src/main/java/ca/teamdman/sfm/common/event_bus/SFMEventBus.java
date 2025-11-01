package ca.teamdman.sfm.common.event_bus;

import ca.teamdman.sfm.SFM;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.NeoForge;

/// Used to reduce {@link ca.teamdman.sfm.common.util.MCVersionDependentBehaviour}.
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SFMEventBus {

    public static IEventBus getEventBus(Bus target) {

        if (target == Target.MOD) {
            var modContainer = (FMLModContainer) ModList.get().getModContainerById(SFM.MOD_ID).get();
            IEventBus eventBus = modContainer.getEventBus();
            assert eventBus != null;
            return eventBus;
        } else if (target == Target.GAME) {
            return NeoForge.EVENT_BUS;
        } else {
            throw new IllegalArgumentException("Invalid target: " + target);
        }
    }

    public static class Target {

        public static final Bus MOD = Bus.MOD;

        public static final Bus GAME = Bus.GAME;

    }

}
