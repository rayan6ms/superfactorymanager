package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.gui.widget.smarttext.SmartTextLanguage;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.text2speech.Narrator;
import net.minecraft.client.gui.screens.Screen;

public class SmartTextScreen extends Screen {
    public SmartTextScreen(
            SmartTextLanguage language,
            String initialContent
    ) {
        super(LocalizationKeys.EXAMPLES_GUI_TITLE.getComponent(language.name()));
    }

    @Override
    public boolean keyPressed(
            int pKeyCode,
            int pScanCode,
            int pModifiers
    ) {


//        SFMWindowsNarrator narrator = (SFMWindowsNarrator) SFMNarratorDiscovery.getNarrator();
//        narrator.clear();
//        narrator.setRate(7);


        Narrator narrator = Narrator.getNarrator();
        narrator.clear();
        narrator.say(String.valueOf((char) pKeyCode), true);
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void render(
            PoseStack pPoseStack,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        float scale = 2.0f;
        pPoseStack.pushPose();
        pPoseStack.scale(scale, scale, scale);
        SFMFontUtils.draw(
                pPoseStack,
                this.font,
                "AHOY THERE!",
                (int) ((this.width / 2 - this.font.width("AHOY THERE!") / 2) * (1. / scale)),
                (int) ((this.height / 2 - this.font.lineHeight / 2) * (1. / scale)),
                0xffffff,
                true
        );
        pPoseStack.popPose();
    }
}
