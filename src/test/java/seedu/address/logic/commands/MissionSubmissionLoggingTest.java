package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/** Verifies that submission outcomes identify the student after filtering changes indexes. */
public class MissionSubmissionLoggingTest {

    @Test
    public void execute_filteredStudent_logsNameForChangesAndRepeatedRequests() throws Exception {
        Model model = new ModelManager();
        Student first = new StudentBuilder().withMatricNumber("A0000100A")
                .withName("Alice Example").withMissionSubmissions().build();
        Student selected = new StudentBuilder().withMatricNumber("A0000101A")
                .withName("Bob Example").withMissionSubmissions().build();
        model.addStudent(first);
        model.addStudent(selected);
        MissionId missionId = new MissionId("M01");
        model.addMission(missionId, "Runes", LocalDate.of(2026, 10, 20));
        model.updateFilteredStudentList(student -> student.getName().equals(selected.getName()));
        Command mark = new MarkMissionCommand(Index.fromOneBased(1), missionId);
        Command unmark = new UnmarkMissionCommand(Index.fromOneBased(1), missionId);

        assertOutcomeLogs(mark, model, MarkMissionCommand.class, missionId);
        assertTrue(model.getFilteredStudentList().get(0).getMissionSubmissions().hasSubmission(missionId));
        assertOutcomeLogs(unmark, model, UnmarkMissionCommand.class, missionId);
        assertEquals(selected, model.getFilteredStudentList().get(0));
        assertEquals(first, model.getAddressBook().getStudentList().get(0));
    }

    /**
     * Checks the identity and levels of a changed outcome followed by a repeated request.
     *
     * @param command Command to execute twice.
     * @param model Model containing the filtered student.
     * @param commandClass Class whose logger is captured.
     * @param missionId Expected mission identifier.
     * @throws Exception If command execution fails unexpectedly.
     */
    private void assertOutcomeLogs(Command command, Model model, Class<?> commandClass, MissionId missionId)
            throws Exception {
        Logger logger = LogsCenter.getLogger(commandClass);
        Level originalLevel = logger.getLevel();
        CapturingHandler handler = new CapturingHandler();
        logger.addHandler(handler);
        logger.setLevel(Level.FINE);
        try {
            command.execute(model);
            command.execute(model);
            assertEquals(List.of(Level.INFO, Level.FINE),
                    handler.records.stream().map(LogRecord::getLevel).toList());
            for (LogRecord record : handler.records) {
                assertTrue(record.getMessage().contains("for student Bob Example"));
                assertTrue(record.getMessage().contains("mission " + missionId));
                assertTrue(record.getMessage().contains("displayed student index 1"));
            }
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(originalLevel);
        }
    }

    /** Captures log records without changing the application's normal output handlers. */
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
