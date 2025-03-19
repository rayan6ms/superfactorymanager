package ca.teamdman.sfm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;


/*
2024-11-12
- SFM currently uses COMMON when it seems like it should be SERVER
- SERVER configs are automatically sent to clients
- Search discord for "send config" and "ConfigTracker" to find discussions

- SFM currently sends a packet and receives a packet to display the server config, this should be replaced with showing the config synced from the server using built-in behaviour
- SFM wants to send the updated config TOML but the handler is stubbed. Config needs to be updated from toml, saved, and resent to clients.

MehVahdJukaar — 03/19/2021 7:25 PM
https://discord.com/channels/313125603924639766/725850371834118214/822611868275310592
so I've managed to sync the common config file by sending to the client its data and then
using CONFIG.setConfig(TomlFormat.instance().createParser().parse(data)) like it's done in
ConfigTracker class. However I would like to be able to load the original client side config
file (still common) back up in case I want to edit it. How can I do that?

sleepy sci, on graveyard duty — 03/19/2021 7:42 PM
https://discord.com/channels/313125603924639766/725850371834118214/822615931510718514
the common config is meant for config settings which do not impact any game logic, but would be useful to store/have on both sides (and which can be separate)
server config is for server-controlled values
client config is for client only player-controlled values
common is anything else

sleepy sci, on graveyard duty — 03/19/2021 7:42 PM
https://discord.com/channels/313125603924639766/725850371834118214/822616037417549835
data defined by the server that affects client-side ...
then it should be server config

 */
public class SFMConfig {
    public static final ForgeConfigSpec SERVER_SPEC;
    public static final SFMServerConfig SERVER;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final SFMClientConfig CLIENT;

    public static final ForgeConfigSpec CLIENT_PROGRAM_EDITOR_SPEC;
    public static final SFMClientProgramEditorConfig CLIENT_PROGRAM_EDITOR;

    static {
        {
            var pair =
                    new ForgeConfigSpec.Builder().configure(SFMServerConfig::new);
            SERVER_SPEC = pair.getRight();
            SERVER = pair.getLeft();
        }
        {
            var pair =
                    new ForgeConfigSpec.Builder().configure(SFMClientConfig::new);
            CLIENT_SPEC = pair.getRight();
            CLIENT = pair.getLeft();
        }
        {
            var pair =
                    new ForgeConfigSpec.Builder().configure(SFMClientProgramEditorConfig::new);
            CLIENT_PROGRAM_EDITOR_SPEC = pair.getRight();
            CLIENT_PROGRAM_EDITOR = pair.getLeft();
        }
    }

    /**
     * Get a config value in a way that doesn't fail when running tests
     */
    public static <T> T getOrDefault(ForgeConfigSpec.ConfigValue<T> configValue) {
        try {
            return configValue.get();
        } catch (Exception e) {
            return configValue.getDefault();
        }
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, SFMConfig.SERVER_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, SFMConfig.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, SFMConfig.CLIENT_PROGRAM_EDITOR_SPEC, "sfm-client-program-editor.toml");
    }
}
