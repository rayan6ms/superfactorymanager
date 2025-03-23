package ca.teamdman.sfm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SFMClientProgramEditorConfig {
    public final ForgeConfigSpec.BooleanValue showLineNumbers;
    public final ForgeConfigSpec.EnumValue<IntellisenseLevel> intellisenseLevel;

    SFMClientProgramEditorConfig(ForgeConfigSpec.Builder builder) {
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