package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests mission status listing, filtering, and displayed student indexes.
 */
public class ViewMissionCommandTest {

    private static final MissionId MISSION_ID = new MissionId("M03");
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 26);
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-26T04:00:00Z"), ZoneId.of("Asia/Makassar"));

    private Model model;
    private Student alice;
    private Student bob;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addMission(MISSION_ID, "Rune Reading", TODAY.minusDays(1));
        alice = new StudentBuilder().withName("Alice Tan")
                .withMissionSubmissions("M03").build();
        bob = new StudentBuilder().withName("Bob Lee").build();
        model.addStudent(alice);
        model.addStudent(bob);
    }

    @Test
    public void execute_previousFilter_listsAllStudentsAndStatuses() throws Exception {
        model.updateFilteredStudentList(student -> student.equals(alice));

        CommandResult result = new ViewMissionCommand(MISSION_ID, false, CLOCK).execute(model);

        assertEquals(List.of(alice, bob), model.getFilteredStudentList());
        assertEquals(
                "M03 · Rune Reading (Due: 2026-10-25)\n"
                        + "2 student(s) listed.\n"
                        + "1. Alice Tan - Submitted\n"
                        + "2. Bob Lee - Overdue\n"
                        + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                result.getFeedbackToUser());
        assertEquals(List.of(alice, bob), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_deadlineToday_displaysNotSubmitted() throws Exception {
        Mission mission = model.findMission(MISSION_ID).orElseThrow();
        model.setMission(mission, new Mission(MISSION_ID, mission.getName(), TODAY));

        CommandResult result = new ViewMissionCommand(MISSION_ID, false, CLOCK).execute(model);

        assertEquals(
                "M03 · Rune Reading (Due: 2026-10-26)\n"
                        + "2 student(s) listed.\n"
                        + "1. Alice Tan - Submitted\n"
                        + "2. Bob Lee - Not submitted\n"
                        + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_unknownMission_preservesDisplayedListAndRecords() {
        model.updateFilteredStudentList(student -> student.equals(bob));

        CommandException exception = assertThrows(CommandException.class, () ->
                new ViewMissionCommand(new MissionId("M99"), false, CLOCK).execute(model));

        assertEquals("Mission M99 does not exist.", exception.getMessage());
        assertEquals(List.of(bob), model.getFilteredStudentList());
        assertEquals(List.of(alice, bob), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_emptyAddressBook_displaysMissionAndZeroCount() throws Exception {
        model.deleteStudent(alice);
        model.deleteStudent(bob);

        CommandResult result = new ViewMissionCommand(MISSION_ID, false, CLOCK).execute(model);

        assertEquals(
                "M03 · Rune Reading (Due: 2026-10-25)\n"
                        + "0 student(s) listed.\n"
                        + ViewMissionCommand.MESSAGE_NO_RESULTS + "\n"
                        + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredStudentList());
    }

    @Test
    public void execute_overdueFilter_searchesAllStudentsAndExcludesSubmitted() throws Exception {
        model.updateFilteredStudentList(student -> student.equals(alice));

        CommandResult result = new ViewMissionCommand(MISSION_ID, true, CLOCK).execute(model);

        assertEquals(List.of(bob), model.getFilteredStudentList());
        assertEquals(
                "M03 · Rune Reading (Due: 2026-10-25)\n"
                        + "1 student(s) listed.\n"
                        + "1. Bob Lee - Overdue\n"
                        + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                result.getFeedbackToUser());
        assertEquals(List.of(alice, bob), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_deadlineTodayOrTomorrow_returnsNoOverdueStudents() throws Exception {
        for (LocalDate deadline : List.of(TODAY, TODAY.plusDays(1))) {
            Mission mission = model.findMission(MISSION_ID).orElseThrow();
            model.setMission(mission, new Mission(MISSION_ID, mission.getName(), deadline));

            CommandResult result = new ViewMissionCommand(MISSION_ID, true, CLOCK).execute(model);

            assertEquals(List.of(), model.getFilteredStudentList());
            assertEquals(
                    "M03 · Rune Reading (Due: " + deadline + ")\n"
                            + "0 student(s) listed.\n"
                            + ViewMissionCommand.MESSAGE_NO_RESULTS + "\n"
                            + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                    result.getFeedbackToUser());
        }
    }

    @Test
    public void execute_differentLocalDates_usesClockTimeZone() throws Exception {
        Mission mission = model.findMission(MISSION_ID).orElseThrow();
        model.setMission(mission, new Mission(MISSION_ID, mission.getName(), TODAY));
        Instant instant = Instant.parse("2026-10-26T16:30:00Z");
        Clock utcClock = Clock.fixed(instant, ZoneId.of("UTC"));
        Clock localClock = Clock.fixed(instant, ZoneId.of("Asia/Makassar"));

        new ViewMissionCommand(MISSION_ID, true, utcClock).execute(model);
        assertEquals(List.of(), model.getFilteredStudentList());

        new ViewMissionCommand(MISSION_ID, true, localClock).execute(model);
        assertEquals(List.of(bob), model.getFilteredStudentList());
    }

    @Test
    public void execute_filteredIndexes_viewMarkAndUnmarkTargetDisplayedStudent() throws Exception {
        Index firstIndex = Index.fromOneBased(1);
        new ViewMissionCommand(MISSION_ID, true, CLOCK).execute(model);

        CommandResult viewResult = new ViewCommand(firstIndex).execute(model);
        assertEquals(bob, viewResult.getStudentToView().orElseThrow());

        new MarkMissionCommand(firstIndex, MISSION_ID).execute(model);
        assertEquals(List.of(), model.getFilteredStudentList());
        assertTrue(model.getAddressBook().getStudentList().get(1)
                .getMissionSubmissions().hasSubmission(MISSION_ID));

        new ViewMissionCommand(MISSION_ID, false, CLOCK).execute(model);
        new UnmarkMissionCommand(Index.fromOneBased(2), MISSION_ID).execute(model);
        assertFalse(model.getAddressBook().getStudentList().get(1)
                .getMissionSubmissions().hasSubmission(MISSION_ID));

        new ViewMissionCommand(MISSION_ID, true, CLOCK).execute(model);
        assertEquals(List.of(bob), model.getFilteredStudentList());
    }

    @Test
    public void execute_markFirstOverdueStudent_updatesRemainingDisplayedIndexes() throws Exception {
        Student charlie = new StudentBuilder().withName("Charlie Lim").build();
        model.addStudent(charlie);
        new ViewMissionCommand(MISSION_ID, true, CLOCK).execute(model);
        assertEquals(List.of(bob, charlie), model.getFilteredStudentList());

        new MarkMissionCommand(Index.fromOneBased(1), MISSION_ID).execute(model);

        assertEquals(List.of(charlie), model.getFilteredStudentList());
        CommandResult result = new ViewCommand(Index.fromOneBased(1)).execute(model);
        assertEquals(charlie, result.getStudentToView().orElseThrow());
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);

        assertEquals(command, command);
    }

    @Test
    public void equals_sameArguments_returnsTrue() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);
        ViewMissionCommand equivalentCommand =
                new ViewMissionCommand(new MissionId("M03"), false, CLOCK);

        assertEquals(command, equivalentCommand);
        assertEquals(equivalentCommand, command);
    }

    @Test
    public void equals_differentArguments_returnsFalse() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);

        assertNotEquals(command,
                new ViewMissionCommand(new MissionId("M04"), false, CLOCK));
        assertNotEquals(command,
                new ViewMissionCommand(MISSION_ID, true, CLOCK));
        assertNotEquals(command,
                new ViewMissionCommand(MISSION_ID, false,
                        Clock.fixed(CLOCK.instant().plusSeconds(1), CLOCK.getZone())));
    }

    @Test
    public void equals_nullOrDifferentType_returnsFalse() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);

        assertNotEquals(command, null);
        assertNotEquals(command, "M03");
    }

    @Test
    public void hashCode_equalCommands_returnsSameHashCode() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);
        ViewMissionCommand equivalentCommand =
                new ViewMissionCommand(new MissionId("M03"), false, CLOCK);

        assertEquals(command.hashCode(), equivalentCommand.hashCode());
    }

    @Test
    public void toString_validCommand_containsCommandFields() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, true, CLOCK);

        String expected = ViewMissionCommand.class.getCanonicalName()
                + "{missionId=" + MISSION_ID
                + ", overdueOnly=true"
                + ", clock=" + CLOCK + "}";

        assertEquals(expected, command.toString());
    }

    @Test
    public void constructor_nullMissionId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewMissionCommand(null));
        assertThrows(NullPointerException.class, () -> new ViewMissionCommand(null, true));
        assertThrows(NullPointerException.class, () ->
                new ViewMissionCommand(null, false, CLOCK));
    }

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new ViewMissionCommand(MISSION_ID, false, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ViewMissionCommand command = new ViewMissionCommand(MISSION_ID, false, CLOCK);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
