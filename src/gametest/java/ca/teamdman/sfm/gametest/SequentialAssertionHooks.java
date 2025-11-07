package ca.teamdman.sfm.gametest;

import ca.teamdman.sfm.common.program.IProgramHooks;

import java.time.Duration;
import java.util.List;

public final class SequentialAssertionHooks implements IProgramHooks {
    private final List<Runnable> assertions;

    private int index = 0;

    public SequentialAssertionHooks(
            List<Runnable> assertions
    ) {

        this.assertions = assertions;
    }

    @Override
    public void onProgramDidSomething(Duration elapsed) {
        if (index < assertions.size()) {
            assertions.get(index).run();
            index++;
        }
    }


    @Override
    public String toString() {

        return "SequentialAssertionHooks[" +
               "assertionCount=" + assertions.size()
               + ", index=" + index
               + ']';
    }

}
