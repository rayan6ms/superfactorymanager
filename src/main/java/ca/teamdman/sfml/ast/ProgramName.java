package ca.teamdman.sfml.ast;

import java.util.List;

public record ProgramName(StringHolder value) implements ASTNode {
    @Override
    public List<StringHolder> getChildNodes() {

        return List.of(value);
    }

}
