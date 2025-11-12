package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.util.NotStored;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.program_builder.ProgramBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SFMGameTestMethodHelpers {

    public static void assertTrue(
            boolean condition,
            String message
    ) {

        if (!condition) {
            @SuppressWarnings("UnnecessaryLocalVariable")
            var toThrow = new GameTestAssertException(message);
            // Uncomment below for detailed location information
            // Note that the tests fail every tick using this until they succeed, so you will see logs that make things look like tests are failing if this is uncommented
//            SFM.LOGGER.error("Assertion failed: {}", message, toThrow);
            throw toThrow;
        }
    }

    public static Program compile(String code) {

        AtomicReference<Program> rtn = new AtomicReference<>();

        new ProgramBuilder(code)
                .useCache(false)
                .build()
                .caseSuccess((program, metadata) -> rtn.set(program))
                .caseFailure(result -> {
                    throw new GameTestAssertException("Failed to compile program: " + result.metadata().errors()
                            .stream()
                            .map(Object::toString)
                            .reduce("", (a, b) -> a + "\n" + b));
                });
        return rtn.get();
    }

    public static void assertManagerRunning(ManagerBlockEntity manager) {

        SFMGameTestMethodHelpers.assertTrue(manager.getDisk() != null, "No disk in manager");
        SFMGameTestMethodHelpers.assertTrue(
                manager.getState() == ManagerBlockEntity.State.RUNNING,
                "Program did not start running " + DiskItem.getErrors(manager.getDisk())
        );
    }

    public static IItemHandler getItemHandler(
            GameTestHelper helper,
            @NotStored BlockPos pos
    ) {
        BlockEntity blockEntity = helper
                .getBlockEntity(pos);
        SFMGameTestMethodHelpers.assertTrue(blockEntity != null, "No block entity found at " + pos);
        Optional<IItemHandler> found = blockEntity
                .getCapability(Capabilities.ITEM_HANDLER)
                .resolve();
        SFMGameTestMethodHelpers.assertTrue(found.isPresent(), "No item handler found at " + pos);
        return found.get();
    }

}
