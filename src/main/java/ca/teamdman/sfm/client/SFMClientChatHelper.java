package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class SFMClientChatHelper {
    @MCVersionDependentBehaviour
    public static void sendChatMessage(Component message) {
        Minecraft.getInstance().getChatListener().handleSystemMessage(message, false);
    }
}
