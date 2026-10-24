package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDIO_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BOB;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.MissionId;
import seedu.address.testutil.StudentBuilder;

/** Tests student identity, value equality, and preservation of fields when copying activity records. */
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
                .withTags(VALID_TAG_HUSBAND).withStudioGroup(VALID_STUDIO_GROUP_BOB)
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

        // different tags -> returns false
        editedAlice = new StudentBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

    }

    @Test
    public void equals_sameStudioGroupValue_returnsTrue() {
        Student aliceCopy = new StudentBuilder(ALICE).withStudioGroup(ALICE.getStudioGroup().toString()).build();

        assertNotSame(ALICE.getStudioGroup(), aliceCopy.getStudioGroup());
        assertTrue(ALICE.equals(aliceCopy));
        assertTrue(aliceCopy.equals(ALICE));
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());
    }

    @Test
    public void equals_differentStudioGroup_returnsFalse() {
        Student editedAlice = new StudentBuilder(ALICE).withStudioGroup(VALID_STUDIO_GROUP_BOB).build();

        assertFalse(ALICE.equals(editedAlice));
        assertFalse(editedAlice.equals(ALICE));
    }

    @Test
    public void constructor_defaultSubmissions_createsEmptySubmissions() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup());

        assertTrue(student.getMissionSubmissions().getSubmittedMissionIds().isEmpty());
    }

    @Test
    public void constructor_nullMissionSubmissions_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                        ALICE.getTags(), ALICE.getStudioGroup(), null, ALICE.getRegisteredStudioRecords(),
                        ALICE.getRegisteredMasteryChecks(), ALICE.getRegisteredNotes()));
    }

    @Test
    public void equals_differentMissionSubmissions_returnsFalse() {
        Student original = new StudentBuilder(ALICE).withMissionSubmissions().build();
        Student updated = new StudentBuilder(original).withMissionSubmissions("M03").build();

        assertFalse(original.equals(updated));
        assertTrue(original.isSameStudent(updated));
        assertTrue(original.getMissionSubmissions().getSubmittedMissionIds().isEmpty());
        assertTrue(updated.getMissionSubmissions().hasSubmission(new seedu.address.model.mission.MissionId("M03")));
    }

    @Test
    public void studentBuilder_copyStudent_preservesMissionSubmissions() {
        Student original = new StudentBuilder(ALICE).withMissionSubmissions("M03", "M05").build();

        Student copy = new StudentBuilder(original).build();

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    public void withMissionSubmissions_validSubmissions_replacesSubmissionsAndPreservesOtherFields() {
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        check.markComplete();
        Student original = new StudentBuilder(ALICE).withMissionSubmissions("M03")
                .withStudioRecords(new StudentBuilder.StudioRecordSet(3, new StudioRecord(20)))
                .withMasteryChecks(Map.of(1, check)).build();
        Student originalSnapshot = new StudentBuilder(original).build();
        MissionSubmissions replacements = new MissionSubmissions(Set.of(new MissionId("M05")));

        Student updated = original.withMissionSubmissions(replacements);

        assertNotSame(original, updated);
        assertEquals(replacements, updated.getMissionSubmissions());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(original.getStudioGroup(), updated.getStudioGroup());
        assertEquals(original.getRegisteredStudioRecords(), updated.getRegisteredStudioRecords());
        assertEquals(original.getRegisteredMasteryChecks(), updated.getRegisteredMasteryChecks());
        assertEquals(originalSnapshot, original);
        assertEquals(Set.of(new MissionId("M03")), original.getMissionSubmissions().getSubmittedMissionIds());
        assertTrue(check.isComplete());
    }

    @Test
    public void withMissionSubmissions_nullSubmissions_throwsNullPointerException() {
        Student student = new StudentBuilder().build();

        assertThrows(NullPointerException.class, () -> student.withMissionSubmissions(null));
    }

    @Test
    public void withMissionSubmissions_emptySubmissions_clearsOnlySubmissions() {
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        Student original = new StudentBuilder(ALICE).withMissionSubmissions("M03")
                .withStudioRecords(new StudentBuilder.StudioRecordSet(3, new StudioRecord(20)))
                .withMasteryChecks(Map.of(1, check)).build();
        Student expected = new StudentBuilder(original).withMissionSubmissions().build();

        Student updated = original.withMissionSubmissions(new MissionSubmissions());

        assertNotSame(original, updated);
        assertEquals(expected, updated);
        assertEquals(Set.of(new MissionId("M03")), original.getMissionSubmissions().getSubmittedMissionIds());
    }

    @Test
    public void withRegisteredMasteryChecks_validRegistrations_replacesChecksAndPreservesOtherFields() {
        MasteryCheck originalCheck = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        originalCheck.markComplete();
        Student original = new StudentBuilder(ALICE).withMissionSubmissions("M03")
                .withStudioRecords(new StudentBuilder.StudioRecordSet(3, new StudioRecord(20)))
                .withMasteryChecks(Map.of(1, originalCheck)).build();
        Student originalSnapshot = new StudentBuilder(original).build();
        MasteryCheck replacementCheck = new MasteryCheck(LocalDateTime.of(2026, 8, 19, 15, 0));
        RegisteredMasteryChecks replacements = new RegisteredMasteryChecks(Map.of(2, replacementCheck));

        Student updated = original.withRegisteredMasteryChecks(replacements);

        assertNotSame(original, updated);
        assertEquals(replacements, updated.getRegisteredMasteryChecks());
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(original.getStudioGroup(), updated.getStudioGroup());
        assertEquals(original.getMissionSubmissions(), updated.getMissionSubmissions());
        assertEquals(original.getRegisteredStudioRecords(), updated.getRegisteredStudioRecords());
        assertEquals(originalSnapshot, original);
        assertEquals(Map.of(1, originalCheck), original.getRegisteredMasteryChecks().getMasteryCheckAppointments());
        assertTrue(originalCheck.isComplete());
        assertFalse(replacementCheck.isComplete());
    }

    @Test
    public void withRegisteredMasteryChecks_nullRegistrations_throwsNullPointerException() {
        Student student = new StudentBuilder().build();

        assertThrows(NullPointerException.class, () -> student.withRegisteredMasteryChecks(null));
    }

    @Test
    public void withRegisteredMasteryChecks_emptyRegistrations_clearsOnlyMasteryChecks() {
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        check.markComplete();
        Student original = new StudentBuilder(ALICE).withMissionSubmissions("M03")
                .withStudioRecords(new StudentBuilder.StudioRecordSet(3, new StudioRecord(20)))
                .withMasteryChecks(Map.of(1, check)).build();
        Student expected = new StudentBuilder(original).withMasteryChecks(Map.of()).build();

        Student updated = original.withRegisteredMasteryChecks(new RegisteredMasteryChecks());

        assertNotSame(original, updated);
        assertEquals(expected, updated);
        assertEquals(Map.of(1, check), original.getRegisteredMasteryChecks().getMasteryCheckAppointments());
        assertTrue(check.isComplete());
    }

    @Test
    public void withRegisteredStudioRecords_validRecords_replacesRecordsAndPreservesOtherFields() {
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        check.markComplete();
        StudioRecord originalRecord = new StudioRecord(20);
        Student original = new StudentBuilder(ALICE)
                .withMissionSubmissions("M03")
                .withStudioRecords(new StudentBuilder.StudioRecordSet(3, originalRecord))
                .withMasteryChecks(Map.of(1, check))
                .build();
        RegisteredStudioRecords updatedRecords = new RegisteredStudioRecords(Map.of(4, new StudioRecord(40)));

        Student updated = original.withRegisteredStudioRecords(updatedRecords);

        assertNotSame(original, updated);
        assertEquals(original.getName(), updated.getName());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(original.getStudioGroup(), updated.getStudioGroup());
        assertEquals(original.getMissionSubmissions(), updated.getMissionSubmissions());
        assertEquals(original.getRegisteredMasteryChecks(), updated.getRegisteredMasteryChecks());
        assertTrue(check.isComplete());
        assertEquals(updatedRecords, updated.getRegisteredStudioRecords());
        assertEquals(originalRecord, original.getRegisteredStudioRecords().getRecordsByWeek().get(3));
    }

    @Test
    public void withRegisteredStudioRecords_nullRecords_throwsNullPointerException() {
        Student student = new StudentBuilder().build();

        assertThrows(NullPointerException.class, () -> student.withRegisteredStudioRecords(null));
    }

    @Test
    public void constructor_defaultMasteryChecks_createsEmptyRegistrations() {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup());
        assertTrue(student.getRegisteredMasteryChecks().getMasteryCheckAppointments().isEmpty());
    }

    @Test
    public void constructor_nullMasteryChecks_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup(), ALICE.getMissionSubmissions(),
                ALICE.getRegisteredStudioRecords(), null, ALICE.getRegisteredNotes()));
    }

    @Test
    public void equals_differentMasteryChecks_preservesStudentIdentity() {
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        Student registered = new StudentBuilder(ALICE).withMasteryChecks(Map.of(1, check)).build();
        Student differentNumber = new StudentBuilder(ALICE).withMasteryChecks(Map.of(2, check)).build();
        assertFalse(ALICE.equals(registered));
        assertFalse(registered.equals(differentNumber));
        assertTrue(ALICE.isSameStudent(registered));
        assertEquals(Map.of(1, check), registered.getRegisteredMasteryChecks().getMasteryCheckAppointments());
        assertTrue(ALICE.getRegisteredMasteryChecks().getMasteryCheckAppointments().isEmpty());
    }

    @Test
    public void studentBuilder_copyStudent_preservesMasteryChecksAndHashCode() {
        LocalDateTime time = LocalDateTime.of(2026, 8, 18, 14, 30);
        Student original = new StudentBuilder(ALICE).withMasteryChecks(Map.of(1, new MasteryCheck(time))).build();
        Student copy = new StudentBuilder(original).build();
        Student equalValues = new StudentBuilder(ALICE).withMasteryChecks(Map.of(1, new MasteryCheck(time))).build();
        assertEquals(original, copy);
        assertEquals(original, equalValues);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.hashCode(), equalValues.hashCode());
    }

    @Test
    public void toStringMethod() {
        Student student = new StudentBuilder(ALICE).withMissionSubmissions("M03").build();
        String expected = Student.class.getCanonicalName()
                + "{name=" + student.getName() + ", phone=" + student.getPhone()
                + ", email=" + student.getEmail() + ", tags=" + student.getTags()
                + ", studioGroup=" + student.getStudioGroup()
                + ", missionSubmissions=" + student.getMissionSubmissions()
                + ", registeredStudioRecords=" + student.getRegisteredStudioRecords()
                + ", registeredMasteryChecks=" + student.getRegisteredMasteryChecks()
                + ", registeredNotes=" + student.getRegisteredNotes() + "}";
        assertEquals(expected, student.toString());
    }
}
