package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.item.DebugStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value=Dist.CLIENT)
public class OnHitBlockChanged {
    private static BlockPos lastPos = BlockPos.ZERO;
    private static int blocksBrokenSinceRelease = 0;

    @SubscribeEvent
    public static void onBlockChanged(RenderHighlightEvent.Block event) {
        BlockPos hitPos = event.getTarget().getBlockPos();
        if (hitPos.equals(lastPos)) {
            return;
        }
        lastPos = hitPos.immutable();
        BlockState hitState = Minecraft.getInstance().level.getBlockState(hitPos);
        SFM.LOGGER.info("Looking at {} {}", hitPos, hitState);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (blocksBrokenSinceRelease <= 0) return;
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        if (!Minecraft.getInstance().options.keyAttack.isDown()) {
            SFM.LOGGER.info("Resetting block break counter");
            blocksBrokenSinceRelease = 0;
        }
    }

    @SubscribeEvent
    public static void onBreakBlockProgress(PlayerInteractEvent.BreakSpeed event) {
        if (event.getEntity().getMainHandItem().getItem() instanceof DebugStickItem) {
            if (blocksBrokenSinceRelease >= 1) {
                event.setNewSpeed(0.0f);
            } else {
                event.setNewSpeed(Float.MAX_VALUE);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        blocksBrokenSinceRelease++;
        SFM.LOGGER.info("Blocks broken since release: {}", blocksBrokenSinceRelease);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().level.isClientSide) return;
        if (event.getItemStack().getItem() instanceof DebugStickItem) {
            if (!SFMKeyMappings.isKeyDown(SFMKeyMappings.ITEM_INSPECTOR_KEY)) {
                event.setCanceled(true);
            }
        }
    }
}
