package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        CommandResult result = new ViewMissionCommand(MISSION_ID, CLOCK).execute(model);

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

        CommandResult result = new ViewMissionCommand(MISSION_ID, CLOCK).execute(model);

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
                new ViewMissionCommand(new MissionId("M99"), CLOCK).execute(model));

        assertEquals("Mission M99 does not exist.", exception.getMessage());
        assertEquals(List.of(bob), model.getFilteredStudentList());
        assertEquals(List.of(alice, bob), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_emptyAddressBook_displaysMissionAndZeroCount() throws Exception {
        model.deleteStudent(alice);
        model.deleteStudent(bob);

        CommandResult result = new ViewMissionCommand(MISSION_ID, CLOCK).execute(model);

        assertEquals(
                "M03 · Rune Reading (Due: 2026-10-25)\n"
                        + "0 student(s) listed.\n"
                        + ViewMissionCommand.MESSAGE_NO_RESULTS + "\n"
                        + ViewMissionCommand.MESSAGE_MANUAL_RECORDS,
                result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredStudentList());
    }
}
