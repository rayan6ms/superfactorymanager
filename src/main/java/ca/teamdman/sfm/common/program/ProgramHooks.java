package ca.teamdman.sfm.common.program;

public interface ProgramHooks {
    ProgramHooks EMPTY = new ProgramHooks() {};
    default void onOutputStatementTick() {}
}
