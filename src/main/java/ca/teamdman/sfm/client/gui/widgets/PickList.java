package ca.teamdman.sfm.client.gui.widgets;

import ca.teamdman.sfm.client.gui.screen.SFMScreenUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.simmetrics.StringDistance;
import org.simmetrics.builders.StringDistanceBuilder;
import org.simmetrics.metrics.BlockDistance;
import org.simmetrics.simplifiers.Simplifiers;

import java.util.Comparator;
import java.util.List;

import static org.simmetrics.tokenizers.Tokenizers.qGramWithPadding;

public class PickList extends AbstractScrollWidget {
    protected final Font font;
    protected List<Component> choices;
    protected Component query = Component.empty();

    public PickList(
            Font font,
            int pX,
            int pY,
            int pWidth,
            int pHeight,
            Component title,
            List<Component> choices
    ) {
        super(pX, pY, pWidth, pHeight, title);
        this.font = font;
        this.choices = choices;
    }

    public List<Component> getChoices() {
        return choices;
    }

    public void setChoices(List<Component> choices) {
        this.choices = choices;
    }

    public @Nullable Component getSelected() {
        if (choices.isEmpty()) return null;
        return getChoices().get(0);
    }

    public void setQuery(Component query) {
        this.query = query;
        sortChoices();
    }

    public void setXY(
            int x,
            int y
    ) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void updateNarration(NarrationElementOutput narration) {
        narration.add(NarratedElementType.TITLE, getMessage());
    }

    @Override
    public void render(
            PoseStack pPoseStack,
            int pMouseX,
            int pMouseY,
            float pPartialTick
    ) {
        if (choices.isEmpty()) return;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void sortChoices() {
        StringDistance distance = StringDistanceBuilder
                .with(new BlockDistance<>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(qGramWithPadding(2))
                .build();
        choices.sort(Comparator.comparing(s -> distance.distance(s.getString(), query.getString())));
    }

    @Override
    protected int getInnerHeight() {
        return font.lineHeight * choices.size();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.choices.size() > this.getDisplayableLineCount();
    }


    private double getDisplayableLineCount() {
        return (double) (this.height - this.totalInnerPadding()) / (double) font.lineHeight;
    }


    @Override
    protected double scrollRate() {
        return font.lineHeight / 2.0d;
    }

    @Override
    protected void renderContents(
            PoseStack poseStack,
            int mx,
            int my,
            float partialTick
    ) {
        Matrix4f matrix4f = poseStack.last().pose();
        var buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        int lineX = SFMScreenUtils.getX(this) + this.innerPadding();
        int lineY = SFMScreenUtils.getY(this) + this.innerPadding();
        for (Component choice : choices) {
            SFMScreenUtils.drawInBatch(
                    choice,
                    this.font,
                    lineX,
                    lineY,
                    true,
                    false,
                    matrix4f,
                    buffer
            );
            lineY += font.lineHeight;
        }
        buffer.endBatch();
    }
}
