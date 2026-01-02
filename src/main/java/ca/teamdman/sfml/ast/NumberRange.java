package ca.teamdman.sfml.ast;

import java.util.ArrayList;
import java.util.Arrays;
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

        if (start.value() == end.value()) return start.toString();
        return start + "-" + end;
    }

    @Override
    public List<? extends SfmlAstNode> getChildNodes() {

        return List.of(start, end);
    }

    public static NumberRange[] compactRanges(NumberRange[] input) {
        if (input.length == 0) {
            return input;
        }

        // Sort ranges by start value
        NumberRange[] sorted = input.clone();
        Arrays.sort(sorted, (a, b) -> Long.compare(a.start().value(), b.start().value()));

        List<NumberRange> result = new ArrayList<>();
        NumberRange current = sorted[0];

        for (int i = 1; i < sorted.length; i++) {
            NumberRange next = sorted[i];
            // Check if current and next overlap or are adjacent (end + 1 >= start)
            if (current.end().value() + 1 >= next.start().value()) {
                // Merge: keep the earlier start and the later end
                long newEnd = Math.max(current.end().value(), next.end().value());
                current = new NumberRange(current.start(), NumberExpression.fromLiteral(newEnd));
            } else {
                result.add(current);
                current = next;
            }
        }
        result.add(current);

        return result.toArray(new NumberRange[0]);
    }

}
