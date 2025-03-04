package ca.teamdman.sfml.program_builder;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfml.ast.Program;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("UnusedReturnValue")
public record ProgramBuildResult(
        @Nullable Program program,
        ProgramMetadata metadata
) {
    public boolean isBuildSuccessful() {
        return program != null && metadata.errors().isEmpty();
    }

    public ProgramBuildResult caseSuccess(BiConsumer<Program, ProgramMetadata> callback) {
        if (isBuildSuccessful()) {
            callback.accept(this.program(), this.metadata());
        }
        return this;
    }

    public ProgramBuildResult caseFailure(Consumer<ProgramBuildResult> callback) {
        if (!isBuildSuccessful()) {
            callback.accept(this);
        }
        return this;
    }

    public int getTokenIndexAtCursorPosition(int cursorPos) {
        ArrayList<Token> found = new ArrayList<>();
        for (Token token : metadata().tokens().getTokens()) {
            if (token.getStartIndex() <= cursorPos && token.getStopIndex() >= cursorPos) {
                found.add(token);
            }
        }

        if (found.isEmpty()) {
            return -1;
        }
        if (found.size() != 1 && !FMLEnvironment.production) {
            SFM.LOGGER.warn("Found interesting token count: {}", found.size());
        }
        return found.get(found.size() - 1).getTokenIndex();
    }
}
