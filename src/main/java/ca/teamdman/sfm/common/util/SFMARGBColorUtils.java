package ca.teamdman.sfm.common.util;

import net.minecraft.util.ARGB;

public class SFMARGBColorUtils {
    @MCVersionDependentBehaviour
    public static int color(int a, int r, int g, int b) {
        return ARGB.color(a,r,g,b);
    }
    @MCVersionDependentBehaviour
    public static int red(int color) {
        return ARGB.red(color);
    }
    @MCVersionDependentBehaviour
    public static int green(int color) {
        return ARGB.green(color);
    }
    @MCVersionDependentBehaviour
    public static int blue(int color) {
        return ARGB.blue(color);
    }
    @MCVersionDependentBehaviour
    public static int alpha(int color) {
        return ARGB.alpha(color);
    }

}
