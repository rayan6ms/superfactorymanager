package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

public class SFMScreenRenderUtils {
    @MCVersionDependentBehaviour
    public static int getX(AbstractWidget widget) {
        return widget.getX();
    }
    @MCVersionDependentBehaviour
    public static int getY(AbstractWidget widget) {
        return widget.getY();
    }


    /**
     * Draws text to the screen
     * @return the width of the drawn text
     */
    @MCVersionDependentBehaviour
    public static int drawInBatch(
            Component text,
            Font font,
            float x,
            float y,
            boolean dropShadow,
            boolean transparent,
            Matrix4f matrix4f,
            MultiBufferSource bufferSource
    ) {
        return font.drawInBatch(
                text,
                x,
                y,
                -1,
                dropShadow,
                matrix4f,
                bufferSource,
                transparent ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                0,
                LightTexture.FULL_BRIGHT
        );
    }
    /**
     * Draws text to the screen
     * @return the width of the drawn text
     */
    @SuppressWarnings("UnusedReturnValue")
    @MCVersionDependentBehaviour
    public static int drawInBatch(
            String text,
            Font font,
            float x,
            float y,
            boolean dropShadow,
            Matrix4f matrix4f,
            MultiBufferSource bufferSource,
            boolean transparent
    ) {
        return font.drawInBatch(
                text,
                x,
                y,
                -1,
                dropShadow,
                matrix4f,
                bufferSource,
                transparent ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL,
                0,
                LightTexture.FULL_BRIGHT
        );
    }

    @MCVersionDependentBehaviour
    public static void enableKeyRepeating() {
        // 1.19.2
//        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
    }

    /**
     * Applies a colour inversion for a region to impart a highlight effect.
     * <p/>
     * See also: {@link net.minecraft.client.gui.components.MultiLineEditBox#renderHighlight(PoseStack, int, int, int, int)}
     */
    @SuppressWarnings("JavadocReference")
    @MCVersionDependentBehaviour
    public static void renderHighlight(
            GuiGraphics graphics,
            int startX,
            int startY,
            int endX,
            int endY
    ) {
        graphics.fill(RenderType.guiTextHighlight(), startX, startY, endX, endY, -16776961);
    }
}
