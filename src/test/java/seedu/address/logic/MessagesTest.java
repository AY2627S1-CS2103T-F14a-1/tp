package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalStudents.ALICE;

import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test
    public void formatStudentDetails_validStudent_returnsFormattedDetails() {
        String expected = String.format(
                "Name: %s%n"
                        + "Phone: %s%n"
                        + "Email: %s%n"
                        + "Address: %s%n"
                        + "Studio Group: %s",
                ALICE.getName(),
                ALICE.getPhone(),
                ALICE.getEmail(),
                ALICE.getAddress(),
                ALICE.getStudioGroup());

        assertEquals(expected, Messages.formatStudentDetails(ALICE));
    }
}
