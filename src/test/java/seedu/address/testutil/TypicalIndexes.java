package seedu.address.testutil;

import seedu.address.commons.core.index.Index;

/**
 * A utility class containing a list of {@code Index} objects to be used in tests.
 */
public class TypicalIndexes {
    public static final Index INDEX_INVALID_STUDENT = Index.fromOneBased(999);
    public static final Index INDEX_FIRST_STUDENT = Index.fromOneBased(1);
    public static final Index INDEX_SECOND_STUDENT = Index.fromOneBased(2);
    public static final Index INDEX_THIRD_STUDENT = Index.fromOneBased(3);

    public static final Index INDEX_FIRST_NOTE = Index.fromOneBased(1);
    public static final Index INDEX_SECOND_NOTE = Index.fromOneBased(2);
    public static final Index INDEX_THIRD_NOTE = Index.fromOneBased(3);
    public static final Index INDEX_INVALID_UPPER_NOTE = Index.fromOneBased(11);
}
