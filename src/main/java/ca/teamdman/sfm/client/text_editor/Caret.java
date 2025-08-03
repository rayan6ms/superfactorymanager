package ca.teamdman.sfm.client.text_editor;

public record Caret(int lineIndex, int gapIndex) {
    /**
     * Compares two {@code caret} values numerically.
     * @return the value {@code 0} if {@code this == other};
     *         a value less than {@code 0} if {@code other comes after this}; and
     *         a value greater than {@code 0} if {@code this comes after other}
     */
    public int compareTo(Caret other) {
        if (lineIndex != other.lineIndex) {
            return Integer.compare(lineIndex, other.lineIndex);
        }
        return Integer.compare(gapIndex, other.gapIndex);
    }
}
