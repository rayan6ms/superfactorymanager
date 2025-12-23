package ca.teamdman.sfml;

import ca.teamdman.sfml.ast.ASTNode;
import ca.teamdman.sfml.ast.Program;
import ca.teamdman.sfml.program_builder.ProgramBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

public class SFMLChildrenTests {
    /// The {@link ASTNode#getChildNodes()} return value must include all fields.
    public void assertChildrenDiscoverable(ASTNode node) {

        Class<? extends ASTNode> nodeClass = node.getClass();
        Field[] fields = nodeClass.getDeclaredFields();
        List<? extends ASTNode> exposedChildren = node.getChildNodes();
        // there must not exist a field that is ? extends ASTNode that is not present in exposedChildren
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (ASTNode.class.isAssignableFrom(fieldType)) {
                boolean found = false;
                for (ASTNode exposedChild : exposedChildren) {
                    try {
                        if (exposedChild.equals(field.get(node))) {
                            found = true;
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!found) {
                    throw new RuntimeException("Field " + field + " not found in exposed children of " + node);
                }
            }
        }
    }

    public void assertDescendantsDiscoverable(ASTNode node) {
        node.getDescendantNodes().forEach(this::assertChildrenDiscoverable);
    }

    @Test
    public void programDiscoverable() {
        String programString = """
                NAME "bruh"
                EVERY 20 TICKS DO
                    INPUT FROM a
                    OUTPUT TO b
                END
                """.stripIndent().stripIndent();
        Program program = new ProgramBuilder(programString).build().maybeProgram();
        Assertions.assertNotNull(program);
        assertDescendantsDiscoverable(program);
    }
}
