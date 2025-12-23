package ca.teamdman.sfml.ast;

import java.util.List;

public record NumberRange(
        Number start,

        Number end
) implements ASTNode {
    public static final NumberRange MAX_RANGE = new NumberRange(new Number(Long.MIN_VALUE), new Number(Long.MAX_VALUE));

    /**
     * Inclusive
     */
    public boolean contains(int value) {

        return value >= start.value() && value <= end.value();
    }

    @Override
    public String toString() {

        if (start == end) return start.toString();
        return start + "-" + end;
    }

    @Override
    public List<Number> getChildNodes() {

        return List.of(start, end);
    }

}
