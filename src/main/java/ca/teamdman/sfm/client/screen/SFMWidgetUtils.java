package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.common.util.MCVersionDependentBehaviour;
import net.minecraft.client.gui.components.AbstractWidget;

public class SFMWidgetUtils {
    /// The field is private in 1.19.4 when it is not in 1.19.2
    @MCVersionDependentBehaviour
    public static int getX(AbstractWidget widget) {
        return widget.getX();
    }

    /// The field is private in 1.19.4 when it is not in 1.19.2
    @MCVersionDependentBehaviour
    public static int getY(AbstractWidget widget) {
        return widget.getY();
    }

}
