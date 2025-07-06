package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.util.SFMDirections;
import ca.teamdman.sfm.common.util.SFMStreamUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ca.teamdman.sfm.common.util.SFMStreamUtils.get3DNeighbours;
import static ca.teamdman.sfm.common.util.SFMStreamUtils.get3DNeighboursIncludingKittyCorner;

public record ServerboundLabelGunUsePacket(
        InteractionHand hand,
        BlockPos pos,
        boolean isContiguousModifierActive,
        boolean isPickBlockModifierActive,
        boolean isClearModifierActive,
        boolean isPullModifierActive,
        boolean isTargetManagerModifierActive
) implements SFMPacket {
    public static class Daddy implements SFMPacketDaddy<ServerboundLabelGunUsePacket> {
        @Override
        public PacketDirection getPacketDirection() {
            return PacketDirection.SERVERBOUND;
        }

        @Override
        public void encode(
                ServerboundLabelGunUsePacket msg,
                FriendlyByteBuf buf
        ) {
            buf.writeEnum(msg.hand);
            buf.writeBlockPos(msg.pos);
            buf.writeBoolean(msg.isContiguousModifierActive);
            buf.writeBoolean(msg.isPickBlockModifierActive);
            buf.writeBoolean(msg.isClearModifierActive);
            buf.writeBoolean(msg.isPullModifierActive);
            buf.writeBoolean(msg.isTargetManagerModifierActive);
        }

        @Override
        public ServerboundLabelGunUsePacket decode(FriendlyByteBuf buf) {
            return new ServerboundLabelGunUsePacket(
                    buf.readEnum(InteractionHand.class),
                    buf.readBlockPos(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean()
            );
        }

        @SuppressWarnings("ConstantValue")
        @Override
        public void handle(
                ServerboundLabelGunUsePacket msg,
                SFMPacketHandlingContext context
        ) {
            var sender = context.sender();
            if (sender == null) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            var level = sender.getLevel();
            if (!(stack.getItem() instanceof LabelGunItem)) {
                return;
            }

            var gunLabels = LabelPositionHolder.from(stack).toOwned();
            var pos = msg.pos;

            // If not targeting manager modifier, do normal push/pull
            if (!msg.isTargetManagerModifierActive && level.getBlockEntity(pos) instanceof ManagerBlockEntity manager) {
                var disk = manager.getDisk();
                if (disk != null) {
                    if (msg.isPullModifierActive) {
                        // start with labels from disk
                        var newLabels = LabelPositionHolder.from(disk).toOwned();
                        // ensure script-referenced labels are included
                        manager.getReferencedLabels().forEach(newLabels::addReferencedLabel);
                        // save to gun
                        newLabels.save(stack);
                        // give feedback to player
                        sender.sendSystemMessage(LocalizationKeys.LABEL_GUN_CHAT_PULLED.getComponent());
                    } else {
                        // save gun labels to disk
                        gunLabels.save(disk);
                        // rebuild program
                        manager.rebuildProgramAndUpdateDisk();
                        // mark manager dirty
                        manager.setChanged();
                        // give feedback to player
                        sender.sendSystemMessage(LocalizationKeys.LABEL_GUN_CHAT_PUSHED.getComponent());
                    }
                }
                return;
            }

            // target is not a manager, we will perform label toggle
            var activeLabel = LabelGunItem.getActiveLabel(stack);

            // get the block type of the target position
            Block targetBlock = level.getBlockState(pos).getBlock();

            // get positions of all connected blocks of the same type if contiguous modifier is active
            List<BlockPos> targets;
            if (msg.isContiguousModifierActive) {
                // find all cable positions so that we only include inventories adjacent to a cable
                Set<BlockPos> cablePositions = get3DNeighbours(pos)
                        .map(suspected_cable_pos -> CableNetworkManager.getOrRegisterNetworkFromCablePosition(
                                level,
                                suspected_cable_pos
                        ))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .flatMap(CableNetwork::getCablePositions)
                        .collect(Collectors.toSet());

                Set<BlockPos> warnBecauseNoCableNeighbour = new HashSet<>();
                Predicate<BlockPos> isAdjacentToCable = p -> {
                    boolean isAdjacent = get3DNeighbours(p).anyMatch(cablePositions::contains);
                    if (!isAdjacent) {
                        warnBecauseNoCableNeighbour.add(p);
                    }
                    return isAdjacent;
                };
                targets = SFMStreamUtils.<BlockPos, BlockPos>getRecursiveStream(
                                (current, nextQueue, results) -> {
                                    results.accept(current);
                                    get3DNeighboursIncludingKittyCorner(current)
                                            .filter(p -> level.getBlockState(p).getBlock() == targetBlock)
                                            .filter(isAdjacentToCable)
                                            .forEach(nextQueue);
                                }, pos
                        )
                        .toList();

                // Notify user if any blocks were skipped because they aren't touching cables
                // TODO: highlight skipped blocks in the world
                if (!warnBecauseNoCableNeighbour.isEmpty()) {
                    sender.sendSystemMessage(LocalizationKeys.LABEL_GUN_CHAT_SKIPPED_BLOCKS.getComponent(
                            warnBecauseNoCableNeighbour.size()));
                }
            } else {
                targets = List.of(pos);
            }

            if (msg.isClearModifierActive) {
                // we are removing labels
                if (msg.isPickBlockModifierActive) {

                    targets.forEach(p -> gunLabels.remove(activeLabel, p));
                } else {
                    targets.forEach(gunLabels::removeAll);
                }
            } else {
                if (msg.isPickBlockModifierActive) {
                    // pick the next label in the list to become active
                    Set<String> allLabels = new HashSet<>();
                    targets.forEach(p -> allLabels.addAll(gunLabels.getLabels(p)));

                    if (!allLabels.isEmpty()) {
                        var labelsList = new ArrayList<>(allLabels);
                        labelsList.sort(Comparator.naturalOrder());
                        var index = (labelsList.indexOf(activeLabel) + 1) % labelsList.size();
                        var nextLabel = labelsList.get(index);
                        LabelGunItem.setActiveLabel(stack, nextLabel);
                    }
                } else {
                    // if any missing label, make all blocks have label, otherwise remove label from all those blocks
                    if (!activeLabel.isEmpty()) {
                        var existing = new HashSet<>(gunLabels.getPositions(activeLabel));
                        boolean anyMissing = targets.stream().anyMatch(p -> !existing.contains(p));

                        // apply or strip label from all positions
                        if (anyMissing) {
                            gunLabels.addAll(activeLabel, targets);
                        } else {
                            targets.forEach(p -> gunLabels.remove(activeLabel, p));
                        }
                    }
                }
            }

            // write changes to label gun stack
            gunLabels.save(stack);
        }

        @Override
        public Class<ServerboundLabelGunUsePacket> getPacketClass() {
            return ServerboundLabelGunUsePacket.class;
        }
    }
}
