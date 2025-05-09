package ca.teamdman.sfm.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SFMClientProgramEditorConfig {
    public final ModConfigSpec.BooleanValue showLineNumbers;
    public final ModConfigSpec.EnumValue<IntellisenseLevel> intellisenseLevel;

    SFMClientProgramEditorConfig(ModConfigSpec.Builder builder) {
        showLineNumbers = builder.define("showLineNumbers", true);
        intellisenseLevel = builder.defineEnum("intellisenseLevel", IntellisenseLevel.OFF);
    }

    public enum IntellisenseLevel {
        OFF,
        BASIC,
        ADVANCED,
        ;

        public boolean isResourceIntellisenseEnabled() {
            return this == ADVANCED;
        }

        public boolean isDisabled() {
            return this == OFF;
        }
    }
}