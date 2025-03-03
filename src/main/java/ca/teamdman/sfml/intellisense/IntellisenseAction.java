package ca.teamdman.sfml.intellisense;

public interface IntellisenseAction {
    IntellisenseContext perform(IntellisenseContext context);
    String getDisplayText();
    /*
    - If suggesting NAME token and NAME already present, jump cursor to inside of existing string token following existing NAME token
    - If suggesting NAME token and NAME already present but STRING following name missing, insert string and place cursor inside
     */
}
