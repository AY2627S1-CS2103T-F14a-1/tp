package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests overdue boundaries, result ordering, and student-filter preservation.
 */
public class ListOverdueCommandTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-20T12:00:00Z"), ZoneOffset.UTC);

    @Test
    public void execute_pastTodayAndFutureDeadlines_listsOnlyMissingPastSubmission() {
        Model model = new ModelManager();
        model.addMission("Past", LocalDate.of(2026, 10, 19));
        model.addMission("Today", LocalDate.of(2026, 10, 20));
        model.addMission("Future", LocalDate.of(2026, 10, 21));
        model.addStudent(new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build());
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListOverdueCommand(CLOCK), model,
                "Overdue submissions as of 2026-10-20 (current student list):\n"
                        + "2. Betsy Crowe | M01 · Past (Due: 2026-10-19)",
                expectedModel);
    }

    @Test
    public void execute_multipleMissingMissions_ordersByStudentThenMissionId() {
        Model model = new ModelManager();
        model.addMission("First", LocalDate.of(2026, 10, 18));
        model.addMission("Second", LocalDate.of(2026, 10, 19));
        model.addStudent(new StudentBuilder().withName("John Doe").build());
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());

        assertEquals("Overdue submissions as of 2026-10-20 (current student list):\n"
                        + "1. John Doe | M01 · First (Due: 2026-10-18)\n"
                        + "1. John Doe | M02 · Second (Due: 2026-10-19)\n"
                        + "2. Betsy Crowe | M01 · First (Due: 2026-10-18)\n"
                        + "2. Betsy Crowe | M02 · Second (Due: 2026-10-19)",
                new ListOverdueCommand(CLOCK).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_hiddenMissingSubmission_preservesFilterAndReportsNoOverdue() {
        Model model = new ModelManager();
        model.addMission("Past", LocalDate.of(2026, 10, 19));
        Student john = new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListOverdueCommand(CLOCK), model,
                "No overdue submissions in the current student list.", expectedModel);
    }

    @Test
    public void execute_emptyFilteredList_reportsNoOverdueSubmissions() {
        Model model = new ModelManager();
        model.addMission("Past", LocalDate.of(2026, 10, 19));
        model.addStudent(new StudentBuilder().build());
        model.updateFilteredStudentList(student -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> false);

        assertCommandSuccess(new ListOverdueCommand(CLOCK), model,
                "No overdue submissions in the current student list.", expectedModel);
    }

    @Test
    public void execute_noMissions_reportsNoOverdueSubmissions() {
        Model model = new ModelManager();
        model.addStudent(new StudentBuilder().build());

        assertEquals("No overdue submissions in the current student list.",
                new ListOverdueCommand(CLOCK).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_localDateDiffersFromUtc_usesClockTimeZone() {
        Model model = new ModelManager();
        model.addMission("Runes", LocalDate.of(2026, 10, 20));
        model.addStudent(new StudentBuilder().withName("John Doe").build());
        Clock localClock = Clock.fixed(
                Instant.parse("2026-10-20T17:00:00Z"), ZoneId.of("Asia/Makassar"));

        assertEquals("Overdue submissions as of 2026-10-21 (current student list):\n"
                        + "1. John Doe | M01 · Runes (Due: 2026-10-20)",
                new ListOverdueCommand(localClock).execute(model).getFeedbackToUser());
    }

    @Test
    public void parse_differentCaseAndExtraArguments_returnsOverdueCommand() throws Exception {
        assertInstanceOf(ListOverdueCommand.class,
                new AddressBookParser().parseCommand("LISTOVERDUE ignored"));
    }

    @Test
    public void constructorAndExecute_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListOverdueCommand(null));
        assertThrows(NullPointerException.class, () -> new ListOverdueCommand(CLOCK).execute(null));
    }
}
