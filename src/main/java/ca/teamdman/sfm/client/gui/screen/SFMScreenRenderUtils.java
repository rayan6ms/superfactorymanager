package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

public class SFMScreenRenderUtils {
    @MCVersionDependentBehaviour
    public static int getX(AbstractWidget widget) {
        return widget.x;
    }
    @MCVersionDependentBehaviour
    public static int getY(AbstractWidget widget) {
        return widget.y;
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
                transparent,
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
                transparent,
                0,
                LightTexture.FULL_BRIGHT
        );
    }

    @MCVersionDependentBehaviour
    public static void enableKeyRepeating() {
        Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
    }

    /**
     * Applies a colour inversion for a region to impart a highlight effect.
     * <p/>
     * See also: {@link net.minecraft.client.gui.components.MultiLineEditBox#renderHighlight(PoseStack, int, int, int, int)}
     */
    @SuppressWarnings("JavadocReference")
    @MCVersionDependentBehaviour
    public static void renderHighlight(
            PoseStack poseStack,
            int startX,
            int startY,
            int endX,
            int endY
    ) {
        Matrix4f matrix4f = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        bufferbuilder.vertex(matrix4f, (float) startX, (float) endY, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f, (float) endX, (float) endY, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f, (float) endX, (float) startY, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f, (float) startX, (float) startY, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }
}
