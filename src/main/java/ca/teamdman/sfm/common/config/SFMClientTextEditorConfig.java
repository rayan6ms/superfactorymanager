package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.client.registry.SFMTextEditors;
import ca.teamdman.sfm.client.text_editor.SFMTextEditorIntellisenseLevel;
import net.minecraftforge.common.ForgeConfigSpec;

public class SFMClientTextEditorConfig {
    public final ForgeConfigSpec.BooleanValue showLineNumbers;
    public final ForgeConfigSpec.EnumValue<SFMTextEditorIntellisenseLevel> intellisenseLevel;
    public final ForgeConfigSpec.ConfigValue<String> preferredEditor;

    SFMClientTextEditorConfig(ForgeConfigSpec.Builder builder) {
        showLineNumbers = builder.define("showLineNumbers", true);
        intellisenseLevel = builder.defineEnum("intellisenseLevel", SFMTextEditorIntellisenseLevel.OFF);
        assert SFMTextEditors.V1.getKey() != null;
        preferredEditor = builder.define("preferredEditor", SFMTextEditors.V1.getKey().location().toString());
    }

}