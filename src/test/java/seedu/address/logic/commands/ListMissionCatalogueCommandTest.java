package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests catalogue feedback, numeric ordering, and preservation of model state.
 */
public class ListMissionCatalogueCommandTest {

    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-10-20T12:00:00Z"), ZoneOffset.UTC);

    @Test
    public void execute_emptyCatalogue_reportsNoMissions() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "No missions configured.", expectedModel);
    }

    @Test
    public void execute_unsortedCatalogue_listsMissionsInNumericIdOrder() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M10"), "Curves", LocalDate.of(2026, 10, 21));
        model.addMission(new MissionId("M02"), "Runes", LocalDate.of(2026, 10, 20));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M02 · Runes (Due: 2026-10-20) | Submitted: 0/0\n"
                        + "M10 · Curves (Due: 2026-10-21) | Submitted: 0/0",
                expectedModel);
    }

    @Test
    public void execute_filteredStudents_listsCatalogueAndPreservesFilter() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        Student john = new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Runes (Due: 2026-10-20) | Submitted: 1/1",
                expectedModel);
    }

    @Test
    public void execute_mixedSubmissionStates_countsSubmittedAndTotalStudents() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        model.addMission(new MissionId("M02"), "Curves", LocalDate.of(2026, 10, 21));
        model.addStudent(new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build());
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Runes (Due: 2026-10-20) | Submitted: 1/2\n"
                        + "M02 · Curves (Due: 2026-10-21) | Submitted: 0/2",
                expectedModel);
    }

    @Test
    public void execute_hiddenSubmittedStudent_excludesSubmissionFromCount() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        model.addStudent(new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build());
        Student betsy = new StudentBuilder().withName("Betsy Crowe").build();
        model.addStudent(betsy);
        model.updateFilteredStudentList(student -> student.equals(betsy));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(betsy));

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Runes (Due: 2026-10-20) | Submitted: 0/1",
                expectedModel);
    }

    @Test
    public void execute_emptyFilteredList_showsCatalogueWithZeroCounts() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        model.addStudent(new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build());
        model.updateFilteredStudentList(student -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> false);

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Runes (Due: 2026-10-20) | Submitted: 0/0",
                expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionCatalogueCommand(CLOCK).execute(null));
    }

    @Test
    public void execute_pastTodayAndFutureDeadlines_labelsOnlyPastDeadline() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Past", LocalDate.of(2026, 10, 19));
        model.addMission(new MissionId("M02"), "Today", LocalDate.of(2026, 10, 20));
        model.addMission(new MissionId("M03"), "Future", LocalDate.of(2026, 10, 21));
        model.addStudent(new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(CLOCK), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Past (Due: 2026-10-19 — Deadline passed) | Submitted: 1/1\n"
                        + "M02 · Today (Due: 2026-10-20) | Submitted: 0/1\n"
                        + "M03 · Future (Due: 2026-10-21) | Submitted: 0/1",
                expectedModel);
    }

    @Test
    public void execute_localDateDiffersFromUtc_usesClockTimeZone() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Clock localClock = Clock.fixed(
                Instant.parse("2026-10-20T17:00:00Z"), ZoneId.of("Asia/Makassar"));

        assertCommandSuccess(new ListMissionCatalogueCommand(localClock), model,
                "All missions (submissions in current student list):\n"
                        + "M01 · Runes (Due: 2026-10-20 — Deadline passed) | Submitted: 0/0",
                expectedModel);
    }

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionCatalogueCommand(null));
    }
}
