package ca.teamdman.sfm.common.resourcetype.exclude;

import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.common.capabilities.Capability;
import net.neoforged.common.capabilities.CapabilityManager;
import net.neoforged.common.capabilities.CapabilityToken;
import net.neoforged.registries.IForgeRegistry;

import java.util.stream.Stream;

public class MekanismEnergyResourceType extends ScalarResourceType<FloatingLong, IStrictEnergyHandler> {
    public static final Capability<IStrictEnergyHandler> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public MekanismEnergyResourceType() {
        super(CAP, new ResourceLocation("mekanism", "energy"), FloatingLong.class);
    }

    @Override
    public long getAmount(FloatingLong amount) {
        return amount.longValue();
    }

    @Override
    public long getAmountDifference(
            FloatingLong stack1,
            FloatingLong stack2
    ) {
        return stack1.subtract(stack2).longValue();
    }

    @Override
    public FloatingLong getStackInSlot(
            IStrictEnergyHandler storage,
            int slot
    ) {
        return storage.getEnergy(slot);
    }

    @Override
    public FloatingLong extract(
            IStrictEnergyHandler storage,
            int slot,
            long amount,
            boolean simulate
    ) {
        return storage.extractEnergy(FloatingLong.create(amount), simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(IStrictEnergyHandler handler) {
        return 1;
    }

    @Override
    public long getMaxStackSize(FloatingLong stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSizeForSlot(
            IStrictEnergyHandler storage,
            int slot
    ) {
        return storage.getMaxEnergy(slot).longValue();
    }

    @Override
    public FloatingLong insert(
            IStrictEnergyHandler storage,
            int slot,
            FloatingLong amount,
            boolean simulate
    ) {
        // note that mekanism returns the remainder, while forge IEnergyStorage returns the accepted amount
        //noinspection UnnecessaryLocalVariable
        FloatingLong remainder = storage.insertEnergy(amount, simulate ? Action.SIMULATE : Action.EXECUTE);
        return remainder;
    }

    @Override
    public boolean isEmpty(FloatingLong stack) {
        return stack.isZero();
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IStrictEnergyHandler;
    }

    @Override
    public Stream<ResourceLocation> getTagsForStack(FloatingLong floatingLong) {
        return Stream.empty();
    }

    @Override
    public FloatingLong getEmptyStack() {
        return FloatingLong.ZERO;
    }

    @Override
    public FloatingLong copy(FloatingLong stack) {
        return stack;
    }

    @Override
    protected FloatingLong setCount(
            FloatingLong stack,
            long amount
    ) {
        return FloatingLong.create(amount);
    }

    @Override
    public FloatingLong withCount(
            FloatingLong floatingLong,
            long count
    ) {
        return FloatingLong.create(count);
    }
}
