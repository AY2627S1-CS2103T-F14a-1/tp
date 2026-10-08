package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDIO_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.StudentBuilder;

public class StudentTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Student student = new StudentBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> student.getTags().remove(0));
    }

    @Test
    public void isSameStudent() {
        // same object -> returns true
        assertTrue(ALICE.isSameStudent(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameStudent(null));

        // same name, all other attributes different -> returns true
        Student editedAlice = new StudentBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).withStudioGroup(VALID_STUDIO_GROUP_BOB)
                .build();
        assertTrue(ALICE.isSameStudent(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new StudentBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameStudent(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Student editedBob = new StudentBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameStudent(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new StudentBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameStudent(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Student aliceCopy = new StudentBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different student -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Student editedAlice = new StudentBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new StudentBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new StudentBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new StudentBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new StudentBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different studio group -> returns false
        editedAlice = new StudentBuilder(ALICE).withStudioGroup(VALID_STUDIO_GROUP_BOB).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void constructor_defaultSubmissions_createsEmptySubmissions() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), ALICE.getStudioGroup());

        assertTrue(student.getMissionSubmissions().getSubmittedWeeks().isEmpty());
    }

    @Test
    public void constructor_nullMissionSubmissions_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                        ALICE.getAddress(), ALICE.getTags(), ALICE.getStudioGroup(), null));
    }

    @Test
    public void equals_differentMissionSubmissions_returnsFalse() {
        Student original = new StudentBuilder(ALICE).withMissionSubmissions().build();
        Student updated = new StudentBuilder(original).withMissionSubmissions(3).build();

        assertFalse(original.equals(updated));
        assertTrue(original.isSameStudent(updated));
        assertTrue(original.getMissionSubmissions().getSubmittedWeeks().isEmpty());
        assertTrue(updated.getMissionSubmissions().hasSubmission(3));
    }

    @Test
    public void studentBuilder_copyStudent_preservesMissionSubmissions() {
        Student original = new StudentBuilder(ALICE).withMissionSubmissions(3, 5).build();

        Student copy = new StudentBuilder(original).build();

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    public void toStringMethod() {
        Student student = new StudentBuilder(ALICE).withMissionSubmissions(3).build();
        String expected = Student.class.getCanonicalName()
                + "{name=" + student.getName() + ", phone=" + student.getPhone()
                + ", email=" + student.getEmail() + ", address=" + student.getAddress() + ", tags=" + student.getTags()
                + ", studioGroup=" + student.getStudioGroup()
                + ", missionSubmissions=" + student.getMissionSubmissions() + "}";
        assertEquals(expected, student.toString());
    }
}
