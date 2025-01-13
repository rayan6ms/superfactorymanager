package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.common.localization.LocalizationEntry;
import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.Nullable;

public class ButtonBuilder {
    private @Nullable Component text = null;
    private int x = 0;
    private int y = 0;
    private int width = 150;
    private int height = 20;
    private @Nullable Button.OnPress onPress = null;
    private @MCVersionDependentBehaviour @Nullable Button.OnTooltip tooltip = null;

    public ButtonBuilder setText(LocalizationEntry text) {
        return setText(text.getComponent());
    }

    public ButtonBuilder setText(Component text) {
        this.text = text;
        return this;
    }

    public ButtonBuilder setSize(
            int x,
            int y
    ) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ButtonBuilder setPosition(
            int width,
            int height
    ) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ButtonBuilder setOnPress(Button.OnPress onPress) {
        this.onPress = onPress;
        return this;
    }

    public ButtonBuilder setTooltip(
            Screen screen,
            Font font,
            LocalizationEntry tooltip
    ) {
        return this.setTooltip(screen, font, tooltip.getComponent());
    }

    @MCVersionDependentBehaviour
    public ButtonBuilder setTooltip(
            Screen screen,
            Font font,
            Component tooltip
    ) {
        this.tooltip = (btn, pose, mx, my) -> screen.renderTooltip(
                pose,
                font.split(tooltip, Math.max(screen.width / 2 - 43, 170)
                ),
                mx,
                my
        );
        return this;
    }

    public Button build() {
        if (text == null) {
            throw new IllegalArgumentException("Text must be set");
        }
        if (onPress == null) {
            throw new IllegalArgumentException("OnPress must be set");
        }
        if (tooltip != null) {
            return new ExtendedButtonWithTooltip(
                    x,
                    y,
                    width,
                    height,
                    text,
                    onPress,
                    tooltip
            );
        } else {
            return new ExtendedButton(
                    x,
                    y,
                    width,
                    height,
                    text,
                    onPress
            );
        }
    }
}
