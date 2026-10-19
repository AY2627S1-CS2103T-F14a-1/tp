package seedu.address.ui.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import guitests.guihandles.StudentCardHandle;
import seedu.address.model.student.Student;

/**
 * A set of assertion methods useful for writing GUI tests.
 */
public class GuiTestAssert {

    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedStudent}.
     */
    public static void assertCardDisplaysStudent(Student expectedStudent, StudentCardHandle actualCard) {
        assertEquals(expectedStudent.getName().fullName, actualCard.getName());
    }
}
