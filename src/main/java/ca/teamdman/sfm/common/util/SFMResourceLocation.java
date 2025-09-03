package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.SFM;
import net.minecraft.resources.ResourceLocation;

public class SFMResourceLocation {
    public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
    public static ResourceLocation fromSFMPath(String path) {
        return fromNamespaceAndPath(SFM.MOD_ID, path);
    }
    public static ResourceLocation fromMinecraftPath(String path) {
        return fromNamespaceAndPath("minecraft", path);
    }
    public static ResourceLocation parse(String expanded) {
        return ResourceLocation.parse(expanded);
    }
}
