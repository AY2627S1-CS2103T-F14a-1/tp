package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

public class ListMasteryCheckCommandTest {

    private static final LocalDateTime NOW =
            LocalDateTime.of(2026, 10, 29, 12, 0);

    private static final Clock FIXED_CLOCK =
            Clock.fixed(
                    Instant.parse("2026-10-29T12:00:00Z"),
                    ZoneOffset.UTC);

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class, (
                ) -> new ListMasteryCheckCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        assertThrows(
                NullPointerException.class, (
                ) -> command.execute(null));
    }

    @Test
    public void execute_noUpcomingChecks_returnsNoUpcomingMessage() {
        Model model = new ModelManager(
                new seedu.address.model.AddressBook(),
                new UserPrefs());

        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        CommandResult result = command.execute(model);

        assertEquals(
                ListMasteryCheckCommand.MESSAGE_NO_UPCOMING_MASTERY_CHECKS,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_pastAndCurrentChecks_excludesThem() {
        MasteryCheck past = new MasteryCheck(
                LocalDateTime.of(2026, 10, 29, 11, 59));

        MasteryCheck current = new MasteryCheck(
                NOW);

        Student student = new StudentBuilder(AMY)
                .withMasteryChecks(Map.of(
                        1, past,
                        2, current))
                .build();

        Model model = createModelWithStudent(student);

        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        CommandResult result = command.execute(model);

        assertEquals(
                ListMasteryCheckCommand.MESSAGE_NO_UPCOMING_MASTERY_CHECKS,
                result.getFeedbackToUser());
    }

    @Test
    public void execute_upcomingChecks_returnsChronologicalRows() {
        MasteryCheck later = new MasteryCheck(
                LocalDateTime.of(2026, 11, 3, 15, 30));

        MasteryCheck earlier = new MasteryCheck(
                LocalDateTime.of(2026, 11, 2, 10, 0));

        Student student = new StudentBuilder(AMY)
                .withMasteryChecks(Map.of(
                        1, later,
                        2, earlier))
                .build();

        Model model = createModelWithStudent(student);

        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        CommandResult result = command.execute(model);

        String feedback = result.getFeedbackToUser();

        assertTrue(feedback.contains(
                "Listed all upcoming mastery checks as of 29 Oct 2026, 12:00"));

        int earlierPosition = feedback.indexOf(
                "02 Nov 2026, 10:00");

        int laterPosition = feedback.indexOf(
                "03 Nov 2026, 15:30");

        assertTrue(earlierPosition >= 0);
        assertTrue(laterPosition >= 0);
        assertTrue(earlierPosition < laterPosition);
    }

    @Test
    public void execute_completedCheck_displaysCompletedStatus() {
        MasteryCheck completed = new MasteryCheck(
                LocalDateTime.of(2026, 11, 2, 10, 0));
        completed.markComplete();

        Student student = new StudentBuilder(AMY)
                .withMasteryChecks(Map.of(1, completed))
                .build();

        Model model = createModelWithStudent(student);

        CommandResult result =
                new ListMasteryCheckCommand(FIXED_CLOCK)
                        .execute(model);

        assertTrue(result.getFeedbackToUser().contains("Completed"));
    }

    @Test
    public void execute_incompleteCheck_displaysIncompleteStatus() {
        MasteryCheck incomplete = new MasteryCheck(
                LocalDateTime.of(2026, 11, 2, 10, 0));

        Student student = new StudentBuilder(AMY)
                .withMasteryChecks(Map.of(1, incomplete))
                .build();

        Model model = createModelWithStudent(student);

        CommandResult result =
                new ListMasteryCheckCommand(FIXED_CLOCK)
                        .execute(model);

        assertTrue(result.getFeedbackToUser().contains("Not completed"));
    }

    @Test
    public void execute_onlyUsesFilteredStudentList() {
        MasteryCheck upcoming = new MasteryCheck(
                LocalDateTime.of(2026, 11, 2, 10, 0));

        Student student = new StudentBuilder(AMY)
                .withMasteryChecks(Map.of(1, upcoming))
                .build();

        Model model = createModelWithStudent(student);

        model.updateFilteredStudentList(
                filteredStudent -> false);

        CommandResult result =
                new ListMasteryCheckCommand(FIXED_CLOCK)
                        .execute(model);

        assertEquals(
                ListMasteryCheckCommand.MESSAGE_NO_UPCOMING_MASTERY_CHECKS,
                result.getFeedbackToUser());
    }

    @Test
    public void equals_sameClock_returnsTrue() {
        ListMasteryCheckCommand first =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        ListMasteryCheckCommand second =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentClock_returnsFalse() {
        Clock differentClock = Clock.fixed(
                Instant.parse("2026-10-30T12:00:00Z"),
                ZoneOffset.UTC);

        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        assertNotEquals(
                command,
                new ListMasteryCheckCommand(differentClock));

        assertNotEquals(command, null);
        assertNotEquals(command, "not a command");
    }

    @Test
    public void toString_containsClock() {
        ListMasteryCheckCommand command =
                new ListMasteryCheckCommand(FIXED_CLOCK);

        assertTrue(command.toString().contains("clock"));
    }

    private static Model createModelWithStudent(Student student) {
        seedu.address.model.AddressBook addressBook =
                new seedu.address.model.AddressBook();

        addressBook.addStudent(student);

        return new ModelManager(
                addressBook,
                new UserPrefs());
    }
}
