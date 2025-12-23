package ca.teamdman.sfml.ast;

import java.util.List;
import java.util.stream.Stream;

public interface ASTNode {
    List<? extends ASTNode> getChildNodes();

    default Stream<? extends ASTNode> getDescendantNodes() {
        Stream.Builder<ASTNode> builder = Stream.builder();
        getChildNodes().forEach(s -> {
            builder.accept(s);
            s.getDescendantNodes().forEach(builder);
        });
        return builder.build();
    }

    default <T> Stream<T> getDescendantNodes(Class<T> clazz) {
        return getDescendantNodes().filter(clazz::isInstance).map(clazz::cast);
    }

    default Stream<ResourceIdentifier<?, ?, ?>> getReferencedIOResourceIds() {
        if (this instanceof IOStatement ioStatement) {
            return ioStatement.resourceLimits().resourceLimitList().stream()
                    .flatMap(resourceLimit -> resourceLimit.resourceIds().stream());
        }
        return getDescendantNodes()
                .filter(IOStatement.class::isInstance)
                .map(IOStatement.class::cast)
                .flatMap(statement -> statement.resourceLimits().resourceLimitList().stream())
                .flatMap(resourceLimit -> resourceLimit.resourceIds().stream());
    }
}
