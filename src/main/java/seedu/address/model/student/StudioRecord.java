package seedu.address.model.student;

public class StudioRecord {

    private final boolean isPresent;
    private final int participation;

    private final static String PRESENT_MARKER = "A: ";
    private final static String PARTICIPATION_MARKER = "P: ";

    public StudioRecord(boolean isPresent, int participation) {
        this.isPresent = isPresent;
        this.participation = participation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudioRecord otherStudioRecord)) {
            return false;
        }

        return isPresent == otherStudioRecord.isPresent && participation == otherStudioRecord.participation;
    }

    @Override
    public String toString() {
        return PRESENT_MARKER + isPresent + " | " + PARTICIPATION_MARKER + participation;
    }
}
