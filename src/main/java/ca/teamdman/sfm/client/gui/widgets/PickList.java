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

public class PickList<T extends PickListItem> extends AbstractScrollWidget {
    protected final Font font;
    protected List<T> items;
    protected Component query = Component.empty();

    public PickList(
            Font font,
            int pX,
            int pY,
            int pWidth,
            int pHeight,
            Component title,
            List<T> items
    ) {
        super(pX, pY, pWidth, pHeight, title);
        this.font = font;
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public @Nullable T getSelected() {
        if (items.isEmpty()) return null;
        return getItems().get(0);
    }

    public void setQuery(Component query) {
        this.query = query;
        sortItems();
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
        if (items.isEmpty()) return;
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    private void sortItems() {
        StringDistance distance = StringDistanceBuilder
                .with(new BlockDistance<>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(qGramWithPadding(2))
                .build();
        items.sort(Comparator.comparing(item -> distance.distance(
                item.getComponent().getString(),
                query.getString()
        )));
    }

    @Override
    protected int getInnerHeight() {
        return font.lineHeight * items.size();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.items.size() > this.getDisplayableLineCount();
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
        for (PickListItem item : items) {
            SFMScreenUtils.drawInBatch(
                    item.getComponent(),
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
