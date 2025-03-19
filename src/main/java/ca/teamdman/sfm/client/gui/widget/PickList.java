package ca.teamdman.sfm.client.gui.widget;

import ca.teamdman.sfm.client.gui.screen.SFMScreenHelpers;
import ca.teamdman.sfm.client.gui.screen.SFMScreenUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
    protected int selectionIndex = -1;
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
        this.clampOrUnsetSelectionIndex();
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
        sortItems();
        selectionIndex = 0;
        clampOrUnsetSelectionIndex();
        scrollSelectedIntoView();
    }

    public int getItemHeight() {
        return font.lineHeight;
    }

    public @Nullable T getSelected() {
        if (items.isEmpty()) return null;
        if (selectionIndex < 0) return null;
        if (selectionIndex >= items.size()) return null;
        return getItems().get(selectionIndex);
    }

    public void setQuery(Component query) {
        this.query = query;
        sortItems();
        selectionIndex = 0;
        clampOrUnsetSelectionIndex();
        scrollSelectedIntoView();
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

    public void selectPreviousWrapping() {
        if (this.selectionIndex == -1) {
            this.selectionIndex = this.items.size() - 1;
            return;
        }
        this.selectionIndex = (this.selectionIndex - 1 + this.items.size()) % this.items.size();
        scrollSelectedIntoView();
    }

    public void selectNextWrapping() {
        if (this.selectionIndex == -1) {
            this.selectionIndex = 0;
            return;
        }
        this.selectionIndex = (this.selectionIndex + 1) % this.items.size();
        scrollSelectedIntoView();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public void clear() {
        this.items.clear();
        this.selectionIndex = -1;
    }

    private void clampOrUnsetSelectionIndex() {
        if (this.items.isEmpty()) {
            this.selectionIndex = -1;
        } else {
            this.selectionIndex = Mth.clamp(this.selectionIndex, 0, this.items.size() - 1);
        }
    }

    private void scrollSelectedIntoView() {
        if (this.isEmpty()) {
            this.setScrollAmount(0);
        } else {
            this.setScrollAmount(
                    this.selectionIndex * this.getItemHeight()
                    - this.getInnerHeight() / (double) this.getItemHeight()
            );
        }
    }

    private void sortItems() {
        StringDistance distance = StringDistanceBuilder
                .with(new BlockDistance<>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(qGramWithPadding(2))
                .build();
        String queryString = query.getString();
        if (queryString.isBlank()) {
            var preferredOrder = new String[]{
                    "TICKS",
                    "INPUT",
                    "OUTPUT",
                    "FORGET",
                    "FROM",
                    "TO"
            };
            items.sort(Comparator.comparing(item -> {
                String itemString = item.getComponent().getString();
                for (int i = 0; i < preferredOrder.length; i++) {
                    if (itemString.contains(preferredOrder[i])) {
                        return i;
                    }
                }
                return preferredOrder.length;
            }));
        } else {
            items.sort(Comparator.comparing(item -> distance.distance(
                    item.getComponent().getString(),
                    queryString
            )));
        }
    }

    @Override
    protected int getInnerHeight() {
        return getItemHeight() * items.size();
    }

    @Override
    protected boolean scrollbarVisible() {
        return this.items.size() > this.getDisplayableItemCount();
    }

    private double getDisplayableItemCount() {
        return (double) (this.height - this.totalInnerPadding()) / (double) getItemHeight();
    }


    @Override
    protected double scrollRate() {
        return this.getItemHeight() / 2.0d;
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
        Rect2i highlight = null;
        int i = 0;
        int itemHeight = getItemHeight();
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
            if (i == this.selectionIndex) {
                highlight = new Rect2i(
                        lineX,
                        lineY,
                        this.width,
                        itemHeight
                );
            }
            lineY += itemHeight;
            i++;

        }
        buffer.endBatch();

        if (highlight != null) {
            SFMScreenHelpers.renderHighlight(
                    poseStack,
                    highlight.getX(),
                    highlight.getY(),
                    highlight.getX() + highlight.getWidth(),
                    highlight.getY() + highlight.getHeight()
            );
        }
    }
}
