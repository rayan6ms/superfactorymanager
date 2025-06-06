package ca.teamdman.sfm.mixins;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameTestInfo.class, priority = 0)
public class GameTestInfoMixin {
    @Redirect(
            method = "prepareTestStructure",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/gametest/framework/StructureUtils;encaseStructure(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/server/level/ServerLevel;Z)V")
    )
    public void skipEncaseStructure(
            AABB pBounds,
            ServerLevel pLevel,
            boolean pPlaceBarriers
    ) {
        GameTestInfo self = (GameTestInfo) (Object) this;

        if (self.getStructureName().startsWith("sfm:")) {
            return; // Skip encasing for SFM structures
        }

        StructureUtils.encaseStructure(pBounds, pLevel, pPlaceBarriers); // Call original for others
    }
}
