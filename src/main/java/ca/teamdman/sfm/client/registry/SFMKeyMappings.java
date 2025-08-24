package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;


@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = SFM.MOD_ID, value = Dist.CLIENT)

public class SFMKeyMappings {
    public static final Lazy<KeyMapping> MORE_INFO_TOOLTIP_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.MORE_HOVER_INFO_KEY.key().get(),
            KeyConflictContext.UNIVERSAL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> CYCLE_LABEL_VIEW_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.CYCLE_LABEL_VIEW_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));
    public static final Lazy<KeyMapping> TOGGLE_NETWORK_TOOL_OVERLAY_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.TOGGLE_NETWORK_TOOL_OVERLAY.key().get(),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> CONTAINER_INSPECTOR_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.CONTAINER_INSPECTOR_TOGGLE_KEY.key().get(),
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> ITEM_INSPECTOR_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.ITEM_INSPECTOR_TOGGLE_KEY.key().get(),
            KeyConflictContext.GUI,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
//            GLFW.GLFW_KEY_GRAVE_ACCENT,
            InputConstants.UNKNOWN.getValue(),
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_PICK_BLOCK_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_PICK_BLOCK_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_CONTIGUOUS_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_CONTIGUOUS_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_CLEAR_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_CLEAR_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_SCROLL_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_SCROLL_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_NEXT_LABEL_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_NEXT_LABEL_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_PREVIOUS_LABEL_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_PREVIOUS_LABEL_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_PULL_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_PULL_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> LABEL_GUN_TARGET_MANAGER_MODIFIER_KEY = Lazy.of(() -> new KeyMapping(
            LocalizationKeys.LABEL_GUN_TARGET_MANAGER_MODIFIER_KEY.key().get(),
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_ALT,
            LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));


    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(MORE_INFO_TOOLTIP_KEY.get());
        event.register(CONTAINER_INSPECTOR_KEY.get());
        event.register(ITEM_INSPECTOR_KEY.get());
        event.register(CYCLE_LABEL_VIEW_KEY.get());
        event.register(LABEL_GUN_PICK_BLOCK_MODIFIER_KEY.get());
        event.register(LABEL_GUN_CONTIGUOUS_MODIFIER_KEY.get());
        event.register(LABEL_GUN_CLEAR_MODIFIER_KEY.get());
        event.register(LABEL_GUN_PULL_MODIFIER_KEY.get()); // Register new key
        event.register(LABEL_GUN_NEXT_LABEL_KEY.get());
        event.register(LABEL_GUN_PREVIOUS_LABEL_KEY.get());
        event.register(LABEL_GUN_TARGET_MANAGER_MODIFIER_KEY.get());
    }

    public static boolean isKeyDownInScreenOrWorld(Lazy<KeyMapping> key) {
        if (key.get().getKey().equals(InputConstants.UNKNOWN)) {
            return false;
        }
        // special effort is needed to ensure this works properly when the manager screen is open
        // https://github.com/mekanism/Mekanism/blob/f92b48a49e0766cd3aa78e95c9c4a47ba90402f5/src/main/java/mekanism/client/key/MekKeyHandler.java
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(
                handle,
                key.get().getKey().getValue()
        );
    }
    public static boolean isKeyDownInWorld(Lazy<KeyMapping> key) {
        if (key.get().getKey().equals(InputConstants.UNKNOWN)) {
            return false;
        }
        return key.get().isDown();
    }
}
