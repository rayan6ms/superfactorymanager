package ca.teamdman.sfm.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;

public abstract class SFMGameTestDefinition {
    public abstract String template();

    public abstract void testMethod(SFMGameTestHelper gameTestHelper);

    public String batchName() {
        return "defaultBatch";
    }

    public String testName() {
        return toSnakeCase(getClass().getSimpleName());
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
                batchName(),
                testName(),
                template(),
                maxTicks(),
                setupTicks(),
                required(),
                (GameTestHelper helper) -> this.testMethod(new SFMGameTestHelper(helper))
        );
    }

    private String toSnakeCase(String input) {
        return
                input.replaceAll("([a-zA-Z])(\\d+)", "$1_$2")
                        .replaceAll("(\\d+)([a-zA-Z])", "$1_$2")
                        .replaceAll("([a-z])([A-Z])", "$1_$2")
                        .toLowerCase();
    }
}
