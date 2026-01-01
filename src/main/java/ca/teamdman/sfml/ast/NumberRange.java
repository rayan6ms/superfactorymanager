package ca.teamdman.sfml.ast;

import java.util.List;

public record NumberRange(
        NumberExpression start,

        NumberExpression end
) implements SfmlAstNode {
    public static final NumberRange MAX_RANGE = new NumberRange(
            NumberExpression.fromLiteral(Long.MIN_VALUE),
            NumberExpression.fromLiteral(Long.MAX_VALUE)
    );

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
    public List<? extends SfmlAstNode> getChildNodes() {

        return List.of(start, end);
    }

}
