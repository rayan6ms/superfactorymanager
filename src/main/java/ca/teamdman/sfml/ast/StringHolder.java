package ca.teamdman.sfml.ast;

import java.util.List;

public record StringHolder(String value) implements ASTNode {
    @Override
    public List<? extends ASTNode> getChildNodes() {

        return List.of();
    }

}
