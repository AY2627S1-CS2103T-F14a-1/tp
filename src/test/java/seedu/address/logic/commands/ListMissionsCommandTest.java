package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.mission.MissionSubmissionStatusEnum;
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
    public void execute_submittedFilter_preservesDisplayedIndexAndRecords() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(
                        MISSION_ID, CLOCK, Optional.of(MissionSubmissionStatusEnum.SUBMITTED)), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "2. Betsy Crowe | Studio group: 1A | Submitted",
                expectedModel);
    }

    @Test
    public void execute_overdueFilter_excludesRecordedSubmissions() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(
                        MISSION_ID, CLOCK, Optional.of(MissionSubmissionStatusEnum.OVERDUE)), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "1. John Doe | Studio group: 1A | Overdue",
                expectedModel);
    }

    @Test
    public void execute_overdueFilterOnDeadline_reportsNoMatches() {
        Clock todayClock = Clock.fixed(
                Instant.parse("2026-10-20T12:00:00Z"), ZoneOffset.UTC);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(
                        MISSION_ID, todayClock, Optional.of(MissionSubmissionStatusEnum.OVERDUE)), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "No submissions match the selected status.",
                expectedModel);
    }

    @Test
    public void execute_submittedFilterWithHiddenSubmitter_preservesStudentFilter() {
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListMissionsCommand(
                        MISSION_ID, CLOCK, Optional.of(MissionSubmissionStatusEnum.SUBMITTED)), model,
                "Submission statuses for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "No submissions match the selected status.",
                expectedModel);
    }

    @Test
    public void execute_allSubmittedMissions_ordersByMissionAndPreservesIndexes() {
        model.addMission(new MissionId("M10"), "Curves", LocalDate.of(2026, 10, 22));
        model.addMission(new MissionId("M02"), "Lists", LocalDate.of(2026, 10, 21));
        model.setStudent(john, new StudentBuilder(john).withMissionSubmissions("M02", "M10").build());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED, CLOCK), model,
                "Submission statuses for all missions (current student list):\n"
                        + "2. Betsy Crowe | Studio group: 1A"
                        + " | M01 · Rune Reading (Due: 2026-10-20) | Submitted\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M02 · Lists (Due: 2026-10-21) | Submitted\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M10 · Curves (Due: 2026-10-22) | Submitted",
                expectedModel);
    }

    @Test
    public void execute_allOverdueMissions_excludesSubmittedTodayAndFuture() {
        model.addMission(new MissionId("M02"), "Today", LocalDate.of(2026, 10, 21));
        model.addMission(new MissionId("M03"), "Future", LocalDate.of(2026, 10, 22));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE, CLOCK), model,
                "Submission statuses for all missions (current student list):\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M01 · Rune Reading (Due: 2026-10-20) | Overdue",
                expectedModel);
    }

    @Test
    public void execute_allSubmittedMissionsWithHiddenSubmitter_reportsNoMatches() {
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED, CLOCK), model,
                "Submission statuses for all missions (current student list):\n"
                        + "No submissions match the selected status.",
                expectedModel);
    }

    @Test
    public void execute_allMissionsWithEmptyStudentList_reportsNoStudents() {
        model.updateFilteredStudentList(student -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> false);

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE, CLOCK), model,
                "Submission statuses for all missions (current student list):\n"
                        + "No students in the current student list.",
                expectedModel);
    }

    @Test
    public void execute_allMissionsWithEmptyCatalogue_reportsNoMissions() {
        Model emptyModel = new ModelManager();
        emptyModel.addStudent(new StudentBuilder().build());
        Model expectedModel = new ModelManager(emptyModel.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED, CLOCK), emptyModel,
                "No missions configured.",
                expectedModel);
    }

    @Test
    public void equals_allMissionAndSpecificMissionQueries_returnsFalse() {
        ListMissionsCommand all = ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED);
        ListMissionsCommand same = ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED);

        assertEquals(all, same);
        assertEquals(all.hashCode(), same.hashCode());
        assertNotEquals(all, ListMissionsCommand.withStatus(MISSION_ID, MissionSubmissionStatusEnum.SUBMITTED));
        assertNotEquals(all, ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE));
    }

    @Test
    public void withStatus_nullStatusOrClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ListMissionsCommand.withStatus(null));
        assertThrows(NullPointerException.class, () ->
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.SUBMITTED, null));
    }

    @Test
    public void execute_overdueAcrossTimeZones_usesClockLocalDate() {
        Instant instant = Instant.parse("2026-10-20T17:00:00Z");
        Clock utcClock = Clock.fixed(instant, ZoneOffset.UTC);
        Clock localClock = Clock.fixed(instant, ZoneId.of("Asia/Makassar"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE, utcClock), model,
                "Submission statuses for all missions (current student list):\n"
                        + "No submissions match the selected status.",
                expectedModel);

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE, localClock), model,
                "Submission statuses for all missions (current student list):\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M01 · Rune Reading (Due: 2026-10-20) | Overdue",
                expectedModel);
    }

    @Test
    public void execute_multipleOverduePairs_ordersByMissionThenStudent() {
        model.addMission(new MissionId("M02"), "Curves", LocalDate.of(2026, 10, 19));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                ListMissionsCommand.withStatus(MissionSubmissionStatusEnum.OVERDUE, CLOCK), model,
                "Submission statuses for all missions (current student list):\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M01 · Rune Reading (Due: 2026-10-20) | Overdue\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M02 · Curves (Due: 2026-10-19) | Overdue\n"
                        + "2. Betsy Crowe | Studio group: 1A"
                        + " | M02 · Curves (Due: 2026-10-19) | Overdue",
                expectedModel);
    }

    @Test
    public void execute_afterMarkAndUnmark_refreshesStatusesWithoutChangingIndexes() throws Exception {
        ListMissionsCommand command = ListMissionsCommand.withStatus(
                MissionSubmissionStatusEnum.OVERDUE, CLOCK);
        Index johnIndex = Index.fromOneBased(1);

        command.execute(model);
        new MarkMissionCommand(johnIndex, MISSION_ID).execute(model);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                "Submission statuses for all missions (current student list):\n"
                        + "No submissions match the selected status.",
                expectedModel);
        assertEquals("John Doe", model.getFilteredStudentList().get(0).getName().toString());

        new UnmarkMissionCommand(johnIndex, MISSION_ID).execute(model);
        Model restoredModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                "Submission statuses for all missions (current student list):\n"
                        + "1. John Doe | Studio group: 1A"
                        + " | M01 · Rune Reading (Due: 2026-10-20) | Overdue",
                restoredModel);
        assertEquals(List.of(john, betsy), model.getFilteredStudentList());
    }

    @Test
    public void equals_differentStatusFilters_returnsFalse() {
        ListMissionsCommand submitted = ListMissionsCommand.withStatus(
                MISSION_ID, MissionSubmissionStatusEnum.SUBMITTED);
        ListMissionsCommand same = ListMissionsCommand.withStatus(
                MISSION_ID, MissionSubmissionStatusEnum.SUBMITTED);
        ListMissionsCommand overdue = ListMissionsCommand.withStatus(
                MISSION_ID, MissionSubmissionStatusEnum.OVERDUE);

        assertEquals(submitted, same);
        assertEquals(submitted.hashCode(), same.hashCode());
        assertNotEquals(submitted, overdue);
        assertNotEquals(submitted, new ListMissionsCommand(MISSION_ID));
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
