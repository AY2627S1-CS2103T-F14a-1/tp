package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests submission listing and preservation of the student filter and records.
 */
public class ListMissionsCommandTest {

    private static final MissionId MISSION_ID = new MissionId("M01");
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-21T12:00:00Z"), ZoneOffset.UTC);

    private Model model;
    private Student john;
    private Student betsy;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addMission(MISSION_ID, "Rune Reading", LocalDate.of(2026, 10, 20));
        john = new StudentBuilder().withName("John Doe").build();
        betsy = new StudentBuilder().withName("Betsy Crowe")
                .withStudioGroup("1A")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.addStudent(betsy);
    }

    @Test
    public void execute_submittedStudent_preservesDisplayedIndexAndRecords() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, CLOCK), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Overdue\n"
                        + "2. Betsy Crowe | Studio group: 1A | Submitted",
                expectedModel);
    }

    @Test
    public void execute_filteredSubmittedStudent_usesFilteredIndex() {
        model.updateFilteredStudentList(student -> student.equals(betsy));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(betsy));

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, CLOCK), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. Betsy Crowe | Studio group: 1A | Submitted",
                expectedModel);
    }

    @Test
    public void execute_submittedStudentsInDifferentGroups_showsEachStudentsGroup() {
        Student brian = new StudentBuilder().withName("Brian Lim")
                .withStudioGroup("1B")
                .withMissionSubmissions("M01").build();
        model.addStudent(brian);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, CLOCK), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Overdue\n"
                        + "2. Betsy Crowe | Studio group: 1A | Submitted\n"
                        + "3. Brian Lim | Studio group: 1B | Submitted",
                expectedModel);
    }

    @Test
    public void execute_hiddenSubmittedStudent_listsOnlyDisplayedStudents() {
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, CLOCK), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Overdue",
                expectedModel);
    }

    @Test
    public void execute_emptyFilteredList_reportsNoStudents() {
        model.updateFilteredStudentList(student -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> false);

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, CLOCK), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "No students in the current student list.",
                expectedModel);
    }

    @Test
    public void execute_unknownMission_preservesState() {
        model.updateFilteredStudentList(student -> student.equals(betsy));

        assertCommandFailure(new ListMissionsCommand(new MissionId("M99")),
                model, "Mission M99 does not exist.");
    }

    @Test
    public void constructorAndExecute_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionsCommand(null));
        assertThrows(NullPointerException.class, () -> new ListMissionsCommand(MISSION_ID, CLOCK).execute(null));
    }

    @Test
    public void execute_deadlineToday_reportsMissingSubmissionAsNotSubmitted() {
        Clock todayClock = Clock.fixed(
                Instant.parse("2026-10-20T12:00:00Z"), ZoneOffset.UTC);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, todayClock), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Not submitted\n"
                        + "2. Betsy Crowe | Studio group: 1A | Submitted",
                expectedModel);
    }

    @Test
    public void execute_futureDeadline_reportsMissingSubmissionAsNotSubmitted() {
        Clock earlierClock = Clock.fixed(
                Instant.parse("2026-10-19T12:00:00Z"), ZoneOffset.UTC);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID, earlierClock), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Not submitted\n"
                        + "2. Betsy Crowe | Studio group: 1A | Submitted",
                expectedModel);
    }

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionsCommand(MISSION_ID, null));
    }

    @Test
    public void equals_sameAndDifferentMissionIds_comparesMissionIds() {
        ListMissionsCommand command = new ListMissionsCommand(MISSION_ID, CLOCK);
        ListMissionsCommand equalCommand = new ListMissionsCommand(new MissionId("M01"));

        assertEquals(command, command);
        assertEquals(command, equalCommand);
        assertEquals(command.hashCode(), equalCommand.hashCode());
        assertNotEquals(command, new ListMissionsCommand(new MissionId("M02")));
        assertNotEquals(command, null);
        assertNotEquals(command, "M01");
    }
}
