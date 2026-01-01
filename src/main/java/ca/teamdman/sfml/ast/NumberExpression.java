package ca.teamdman.sfml.ast;

import java.util.List;

/**
 * A number expression that holds both the eagerly evaluated numeric value
 * and the original expression AST for pretty printing purposes.
 *
 * @param number     The eagerly evaluated numeric value
 * @param expression The original expression AST node
 */
public record NumberExpression(
        Number number,
        INumberExpression expression
) implements SfmlAstNode, ToStringPretty {
    
    /**
     * Get the evaluated long value
     */
    public long value() {
        return number.value();
    }

    /**
     * Add two number expressions together, creating a new NumberExpression with the combined value.
     * The resulting expression type is NumberAddition to preserve the operation structure.
     */
    public NumberExpression add(NumberExpression other) { // TODO v5: rename to concatAddition and add an add idk
        NumberAddition expr = new NumberAddition(this, other);
        Number resultValue = new Number(this.value() + other.value());
        return new NumberExpression(resultValue, expr);
    }

    /**
     * Create a NumberExpression from a literal value
     */
    public static NumberExpression fromLiteral(long value) {
        NumberLiteral literal = new NumberLiteral(value);
        return new NumberExpression(new Number(value), literal);
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    public List<? extends SfmlAstNode> getChildNodes() {
        return List.of(expression);
    }
}
