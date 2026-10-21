package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/** Checks that completion outcome logs use the correct command logger and identify the filtered student. */
public class MasteryCheckLoggingTest {
    @Test
    public void execute_markFilteredStudent_logsChangeAndRepeatedRequestUnderMarkLogger() throws Exception {
        assertOutcomeLogs(new MarkMasteryCheckCommand(Index.fromOneBased(1), 1), MarkMasteryCheckCommand.class, false);
    }

    @Test
    public void execute_unmarkFilteredStudent_logsChangeAndRepeatedRequestUnderUnmarkLogger() throws Exception {
        assertOutcomeLogs(new UnmarkMasteryCheckCommand(Index.fromOneBased(1), 1),
                UnmarkMasteryCheckCommand.class, true);
    }

    /**
     * Checks log levels and student identity for a changed outcome followed by a repeated request.
     *
     * @param command Completion command to execute twice.
     * @param commandClass Class whose logger should emit the outcomes.
     * @param initiallyComplete Whether the selected student's appointment starts complete.
     * @throws Exception If either command execution fails unexpectedly.
     */
    private void assertOutcomeLogs(Command command, Class<?> commandClass, boolean initiallyComplete) throws Exception {
        Model model = new ModelManager();
        Student first = new StudentBuilder().withName("Alice Example").build();
        MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        if (initiallyComplete) {
            check.markComplete();
        }
        Student selected = new StudentBuilder().withName("Bob Example").withMasteryChecks(Map.of(1, check)).build();
        model.addStudent(first);
        model.addStudent(selected);
        model.updateFilteredStudentList(student -> student.getName().equals(selected.getName()));
        Logger logger = LogsCenter.getLogger(commandClass);
        Level originalLevel = logger.getLevel();
        CapturingHandler handler = new CapturingHandler();
        logger.addHandler(handler);
        logger.setLevel(Level.FINE);
        try {
            command.execute(model);
            command.execute(model);
            assertEquals(List.of(Level.INFO, Level.FINE), handler.records.stream().map(LogRecord::getLevel).toList());
            for (LogRecord record : handler.records) {
                assertEquals(logger.getName(), record.getLoggerName());
                assertTrue(record.getMessage().contains("for student Bob Example"));
                assertTrue(record.getMessage().contains("mastery check 1"));
                assertTrue(record.getMessage().contains("displayed student index 1"));
            }
            assertEquals(first, model.getAddressBook().getStudentList().getFirst());
            assertEquals(!initiallyComplete, model.getFilteredStudentList().getFirst().getRegisteredMasteryChecks()
                    .getMasteryCheckAppointments().get(1).isComplete());
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(originalLevel);
        }
    }

    /** Captures outcomes without changing the application's normal output handlers. */
    private static class CapturingHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
            // Records are stored immediately in memory.
        }

        @Override
        public void close() {
            records.clear();
        }
    }
}
