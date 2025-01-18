package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class ExtendedButtonWithTooltip extends ExtendedButton {
    @MCVersionDependentBehaviour
    public ExtendedButtonWithTooltip(
            int xPos,
            int yPos,
            int width,
            int height,
            Component displayString,
            OnPress handler,
            Tooltip tooltip
    ) {
        super(xPos, yPos, width, height, displayString, handler);
        setTooltip(tooltip);
    }
}
