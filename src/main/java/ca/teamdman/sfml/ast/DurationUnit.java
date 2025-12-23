package ca.teamdman.sfml.ast;

import java.util.List;

public enum DurationUnit implements ASTNode {
    TICKS,
    SECONDS;

    public int toTicks(Number number) {

        return (int) switch (this) {
            case TICKS -> number.value();
            case SECONDS -> number.value() * 20;
        };
    }

    @Override
    public List<? extends ASTNode> getChildNodes() {

        return List.of();
    }
}
