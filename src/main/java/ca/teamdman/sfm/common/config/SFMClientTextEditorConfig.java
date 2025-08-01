package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMTextEditors;
import ca.teamdman.sfm.common.text_editor.SFMTextEditorIntellisenseLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public class SFMClientTextEditorConfig {

    @SuppressWarnings("DataFlowIssue")
    public static final Supplier<ResourceLocation> PREFERRED_EDITOR_DEFAULT = () -> SFMTextEditors.V1
            .getKey()
            .location();

    public static final Supplier<ResourceLocation> PREFERRED_EDITOR_ASK = () -> new ResourceLocation(
            SFM.MOD_ID,
            "ask"
    );

    public final ForgeConfigSpec.BooleanValue showLineNumbers;
    public final ForgeConfigSpec.EnumValue<SFMTextEditorIntellisenseLevel> intellisenseLevel;
    public final ForgeConfigSpec.ConfigValue<String> preferredEditor;

    SFMClientTextEditorConfig(ForgeConfigSpec.Builder builder) {
        showLineNumbers = builder.define("showLineNumbers", true);
        intellisenseLevel = builder.defineEnum("intellisenseLevel", SFMTextEditorIntellisenseLevel.OFF);
        preferredEditor = builder.define("preferredEditor", PREFERRED_EDITOR_DEFAULT.get().toString());
    }

}