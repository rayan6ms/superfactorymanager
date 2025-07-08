package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.label.LabelPositionHolder;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.program.CapabilityConsumer;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.util.Stored;
import ca.teamdman.sfml.ast.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    public final BlockCapability<CAP, @Nullable Direction> CAPABILITY_KIND;

    public ResourceType(BlockCapability<CAP, @Nullable Direction> CAPABILITY_KIND) {
        this.CAPABILITY_KIND = CAPABILITY_KIND;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceType<?, ?, ?> that)) return false;
        return Objects.equals(CAPABILITY_KIND, that.CAPABILITY_KIND);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(CAPABILITY_KIND);
    }

    public abstract long getAmount(STACK stack);

    /**
     * Some resource types may exceed MAX_LONG, this method should be used to get the difference between two stacks
     */
    public long getAmountDifference(
            STACK stack1,
            STACK stack2
    ) {
        return getAmount(stack1) - getAmount(stack2);
    }

    public abstract STACK getStackInSlot(
            CAP cap,
            int slot
    );

    public abstract STACK extract(
            CAP cap,
            int slot,
            long amount,
            boolean simulate
    );

    public abstract int getSlots(CAP handler);

    public abstract long getMaxStackSize(STACK stack);

    public abstract long getMaxStackSizeForSlot(
            CAP cap,
            int slot
    );

    /**
     * @return the remainder, what was not inserted
     */
    public abstract STACK insert(
            CAP cap,
            int slot,
            STACK stack,
            boolean simulate
    );

    public abstract boolean isEmpty(STACK stack);

    @SuppressWarnings("unused")
    public abstract STACK getEmptyStack();

    public abstract boolean matchesStackType(Object o);

    public boolean matchesStack(
            ResourceIdentifier<STACK, ITEM, CAP> resourceId,
            Object stack
    ) {
        if (!matchesStackType(stack)) return false;
        @SuppressWarnings("unchecked") STACK stack_ = (STACK) stack;
        if (isEmpty(stack_)) return false;
        var stackId = getRegistryKeyForStack(stack_);
        return resourceId.matchesResourceLocation(stackId);
    }

    public abstract boolean matchesCapabilityType(Object o);

    public void forEachCapability(
            ProgramContext programContext,
            LabelAccess labelAccess,
            CapabilityConsumer<CAP> consumer
    ) {
        // Log
        programContext
                .getLogger()
                .trace(x -> x.accept(LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_BEGIN.get(
                        displayAsCode(),
                        displayAsCapabilityClass(),
                        labelAccess
                )));

        DirectionQualifier directions = labelAccess.directions();
        LabelPositionHolder labelPositionHolder = programContext.getLabelPositionHolder();
        ArrayList<Pair<Label, BlockPos>> positions = labelAccess.getLabelledPositions(labelPositionHolder);

        for (var pair : positions) {
            Label label = pair.getFirst();
            BlockPos pos = pair.getSecond();
            forEachDirectionalCapability(
                    programContext,
                    directions,
                    pos,
                    (dir, cap) -> consumer.accept(label, pos, dir, cap)
            );
        }
    }

    public void forEachDirectionalCapability(
            ProgramContext programContext,
            DirectionQualifier directions,
            @Stored BlockPos pos,
            BiConsumer<Direction, CAP> consumer
    ) {
        for (Direction dir : directions) {
            @Nullable CAP maybeCap = programContext.getNetwork()
                    .getCapability(CAPABILITY_KIND, pos, dir, programContext.getLogger());
            if (maybeCap != null) {
                programContext
                        .getLogger()
                        .debug(x -> x.accept(LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_CAP_PRESENT.get(
                                displayAsCapabilityClass(),
                                pos,
                                dir
                        )));
                consumer.accept(dir, maybeCap);
            } else {
                // Log error
                programContext
                        .getLogger()
                        .error(x -> x.accept(LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_CAP_NOT_PRESENT.get(
                                displayAsCapabilityClass(),
                                pos,
                                dir
                        )));
            }
        }
    }

    public abstract Stream<ResourceLocation> getTagsForStack(STACK stack);

    public Stream<STACK> getStacksInSlots(
            CAP cap,
            NumberRangeSet slots
    ) {
        var rtn = Stream.<STACK>builder();
        for (int slot = 0; slot < getSlots(cap); slot++) {
            if (!slots.contains(slot)) continue;
            var stack = getStackInSlot(cap, slot);
            if (!isEmpty(stack)) {
                rtn.add(stack);
            }
        }
        return rtn.build();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean registryKeyExists(ResourceLocation location);

    public abstract ResourceLocation getRegistryKeyForStack(STACK stack);

    public abstract ResourceLocation getRegistryKeyForItem(ITEM item);

    public abstract @Nullable ITEM getItemFromRegistryKey(ResourceLocation location);

    public abstract Set<ResourceLocation> getRegistryKeys();

    public abstract Collection<ITEM> getItems();

    public abstract ITEM getItem(STACK stack);

    public abstract STACK copy(STACK stack);

    @SuppressWarnings("unused")
    public STACK withCount(
            STACK stack,
            long count
    ) {
        return setCount(copy(stack), count);
    }

    public String displayAsCode() {
        ResourceLocation thisKey = SFMResourceTypes.registry().getKey(this);
        return thisKey != null ? thisKey.toString() : "null";
    }

    public String displayAsCapabilityClass() {
        return CAPABILITY_KIND.name().toString();
    }

    protected abstract STACK setCount(
            STACK stack,
            long amount
    );
}
