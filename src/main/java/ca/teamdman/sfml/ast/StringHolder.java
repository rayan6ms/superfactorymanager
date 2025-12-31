package ca.teamdman.sfml.ast;

import java.util.List;

public record StringHolder(String value) implements SfmlAstNode {
    @Override
    public List<? extends SfmlAstNode> getChildNodes() {

        return List.of();
    }

}
