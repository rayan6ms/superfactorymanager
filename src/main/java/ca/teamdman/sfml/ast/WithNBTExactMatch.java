package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

import java.util.Objects;

public record WithNBTExactMatch(CompoundTag tag) implements ASTNode, WithClause, ToStringPretty {
    @Override
    public <STACK> boolean matchesStack(
            ResourceType<STACK, ?, ?> resourceType,
            STACK stack
    ) {
        return Objects.equals(resourceType.getNBTForStack(stack), tag());
    }

    @Override
    public String toString() {
        return "NBT " + NbtUtils.prettyPrint(tag());
    }
}
