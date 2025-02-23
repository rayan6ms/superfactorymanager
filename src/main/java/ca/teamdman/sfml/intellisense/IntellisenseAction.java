package ca.teamdman.sfml.intellisense;

public interface IntellisenseAction {
    IntellisenseContext perform(IntellisenseContext context);
    String getDisplayText();
}
