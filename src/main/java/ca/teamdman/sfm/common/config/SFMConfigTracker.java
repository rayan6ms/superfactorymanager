package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.SFM;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class SFMConfigTracker {
    private static final HashMap<IConfigSpec, Path> configPaths = new HashMap<>();

    public static @Nullable Path getPathForConfig(IConfigSpec spec) {
        return configPaths.get(spec);
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    private static Set<ModConfig> getModConfigs(ModConfig.Type modConfigType) {
        ConfigTracker configTracker = ConfigTracker.INSTANCE;
//        return configTracker.configSets().get(ModConfig.Type.SERVER);
        try {
            Field configSetsField = configTracker.getClass().getDeclaredField("configSets");
            configSetsField.setAccessible(true);
            HashMap<ModConfig.Type, Set<ModConfig>> configSets = (HashMap<ModConfig.Type, Set<ModConfig>>) configSetsField.get(configTracker);
            return configSets.get(modConfigType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    static @Nullable ModConfig getServerModConfig() {
        Set<ModConfig> modConfigs = getModConfigs(ModConfig.Type.SERVER);
        for (ModConfig modConfig : modConfigs) {
            // .equals() doesn't work here
            if (modConfig.getSpec() == SFMConfig.SERVER_SPEC) {
                return modConfig;
            }
        }
        return null;
    }

    static @Nullable ModConfig getClientModConfig() {
        Set<ModConfig> modConfigs = getModConfigs(ModConfig.Type.CLIENT);
        for (ModConfig modConfig : modConfigs) {
            // .equals() doesn't work here
            if (modConfig.getSpec() == SFMConfig.CLIENT_SPEC) {
                return modConfig;
            }
        }
        return null;
    }

    @EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModConfigEventListeners {
        /**
         * Tracks when configs are loaded
         * <p>
         * See {@link ConfigTracker#openConfig(ModConfig, Path)}
         */
        @SuppressWarnings("JavadocReference")
        @SubscribeEvent
        public static void onConfigLoaded(ModConfigEvent.Loading event) {
            handleConfigEvent(event);
        }

        @SubscribeEvent
        public static void onConfigReloaded(ModConfigEvent.Reloading event) {
            handleConfigEvent(event);
        }

        private static void handleConfigEvent(ModConfigEvent event) {
            ModConfig modConfig = event.getConfig();
            if (modConfig.getModId().equals(SFM.MOD_ID)) {
                IConfigSpec spec = modConfig.getSpec();
                FileConfig fileConfig = getFileConfig(spec);
                if (fileConfig != null) {
                    configPaths.put(spec, fileConfig.getNioPath());
                }
            }
        }

        private static @Nullable FileConfig getFileConfig(IConfigSpec configSpec) {
            Config config = getChildConfig(configSpec);
            if (config instanceof FileConfig fileConfig) {
                return fileConfig;
            }
            return null;
        }

        private static @Nullable Config getChildConfig(IConfigSpec configSpec) {
            if (configSpec instanceof ModConfigSpec forgeConfigSpec) {
                try {
                    Field childConfigField = forgeConfigSpec.getClass().getDeclaredField("childConfig");
                    childConfigField.setAccessible(true);
                    return (Config) childConfigField.get(forgeConfigSpec);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    SFM.LOGGER.error("Failed to extract childConfig field", e);
                    return null;
                }
            }
            return null;
        }
    }

    @EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    public static class GameConfigEventListeners {
        /**
         * Tracks when configs are unloaded
         * <p>
         * See {@link ConfigTracker#unloadConfigs(ModConfig.Type, Path)}
         * which is called by {@link ServerLifecycleHooks#handleServerStopped(MinecraftServer)}
         */
        @SubscribeEvent
        public static void onServerStopped(ServerStoppedEvent event) {
            configPaths.entrySet().removeIf(entry -> entry.getKey() == SFMConfig.SERVER_SPEC);
        }
    }
}
