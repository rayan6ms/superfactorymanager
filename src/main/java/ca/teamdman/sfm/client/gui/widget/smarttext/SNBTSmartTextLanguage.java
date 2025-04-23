package ca.teamdman.sfm.client.gui.widget.smarttext;

import ca.teamdman.sfm.common.localization.LocalizationKeys;
import net.minecraft.network.chat.Component;

public class SNBTSmartTextLanguage implements SmartTextLanguage{
    @Override
    public Component name() {
        return LocalizationKeys.LanguageNames.SNBT.getComponent();
    }
}
