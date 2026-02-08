package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.screen.ManagerScreen;
import ca.teamdman.sfm.client.screen.TestBarrelTankScreen;
import ca.teamdman.sfm.common.registry.registration.SFMMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SFMMenuScreens {
    @SubscribeEvent
    public static void register(RegisterMenuScreensEvent event) {
        event.register(SFMMenus.MANAGER.get(), ManagerScreen::new);
        event.register(SFMMenus.TEST_BARREL_TANK.get(), TestBarrelTankScreen::new);
    }
}
