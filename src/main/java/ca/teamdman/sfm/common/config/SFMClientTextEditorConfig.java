package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.client.text_editor.SFMTextEditorIntellisenseLevel;
import net.neoforged.neoforge.common.ModConfigSpec;

public class SFMClientTextEditorConfig {
    public final ModConfigSpec.BooleanValue showLineNumbers;
    public final ModConfigSpec.EnumValue<SFMTextEditorIntellisenseLevel> intellisenseLevel;
    public final ModConfigSpec.ConfigValue<String> preferredEditor;

    SFMClientTextEditorConfig(ModConfigSpec.Builder builder) {
        showLineNumbers = builder.define("showLineNumbers", true);
        intellisenseLevel = builder.defineEnum("intellisenseLevel", SFMTextEditorIntellisenseLevel.OFF);
        preferredEditor = builder.define("preferredEditor", "sfm:v1");
    }
}