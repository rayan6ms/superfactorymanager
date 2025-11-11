package ca.teamdman.sfm.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Rotation;

import java.util.Locale;

public abstract class SFMGameTestDefinition {
    public abstract String template();
    public String templateModId() {
        return "sfm";
    }

    public abstract void run(SFMGameTestHelper helper);

    public String batchName() {
        return "defaultBatch";
    }

    public String testName() {
        return toSnakeCase(getClass().getSimpleName().replaceAll("GameTest$", ""));
    }

    public int maxTicks() {
        return 100;
    }

    public int setupTicks() {
        return 0;
    }

    public boolean required() {
        return true;
    }

    public TestFunction intoTestFunction() {
        return new TestFunction(
                this.batchName(),
                this.testName(),
                this.templateModId() + ":" + this.template(),
                Rotation.NONE,
                this.maxTicks(),
                this.setupTicks(),
                this.required(),
                false,
                1,
                1,
                false,
                (GameTestHelper helper) -> this.run(new SFMGameTestHelper(helper))
        );
    }

    private String toSnakeCase(String input) {
        return
                input.replaceAll("([a-zA-Z])(\\d+)", "$1_$2")
                        .replaceAll("(\\d+)([a-zA-Z])", "$1_$2")
                        .replaceAll("([a-z])([A-Z])", "$1_$2")
                        .toLowerCase(Locale.ROOT);
    }
}
