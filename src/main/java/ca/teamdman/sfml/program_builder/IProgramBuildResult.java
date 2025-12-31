package ca.teamdman.sfml.program_builder;

import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IProgramBuildResult<PROGRAM, METADATA extends IProgramMetadata<?, ?, ?>, SELF extends IProgramBuildResult<PROGRAM, METADATA, SELF>> {

    PROGRAM maybeProgram();

    METADATA metadata();


    default boolean isBuildSuccessful() {

        return maybeProgram() != null && metadata().errors().isEmpty();
    }

    default SELF caseSuccess(
            BiConsumer<PROGRAM, METADATA> callback
    ) {

        if (isBuildSuccessful()) {
            callback.accept(this.maybeProgram(), this.metadata());
        }
        //noinspection unchecked
        return (SELF) this;
    }

    default SELF caseFailure(
            BiConsumer<PROGRAM, METADATA> callback
    ) {

        if (!isBuildSuccessful()) {
            callback.accept(this.maybeProgram(), this.metadata());
        }
        //noinspection unchecked
        return (SELF) this;
    }

    default SELF caseFailure(
            Consumer<METADATA> callback
    ) {

        if (!isBuildSuccessful()) {
            callback.accept(this.metadata());
        }
        //noinspection unchecked
        return (SELF) this;
    }
    default SELF caseFailure(
            Runnable callback
    ) {

        if (!isBuildSuccessful()) {
            callback.run();
        }
        //noinspection unchecked
        return (SELF) this;
    }

    default @Nullable Token getTokenAtCursorPosition(int cursorPos) {

        for (Token token : metadata().tokens().getTokens()) {
            if (token.getStartIndex() <= cursorPos && token.getStopIndex() + 1 >= cursorPos) {
                return token;
            }
        }
        return null;
    }

    /**
     * @param cursorPos The cursor position
     * @return The sequence of non-whitespace characters to the left of the cursor
     */
    default String getWordAtCursorPosition(int cursorPos) {

        StringBuilder word = new StringBuilder();
        for (int i = cursorPos - 1; i >= 0; i--) {
            char c = this.metadata().programString().charAt(i);
            if (Character.isWhitespace(c)) {
                break;
            }
            word.insert(0, c);
        }
        return word.toString();
    }

}
