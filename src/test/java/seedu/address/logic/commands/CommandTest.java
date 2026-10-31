package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.getOutOfBoundsStudentIndexMessage;
import static seedu.address.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;

/**
 * Tests common command behaviour.
 */
public class CommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final TestCommand command = new TestCommand();

    @Test
    public void getStudentAtDisplayedIndex_validIndex_returnsDisplayedStudent() throws CommandException {
        Student expectedStudent = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());

        Student actualStudent = command.getStudentAtDisplayedIndexForTest(model, INDEX_FIRST_STUDENT);

        assertEquals(expectedStudent, actualStudent);
    }

    @Test
    public void getStudentAtDisplayedIndex_invalidIndex_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        CommandException exception = assertThrows(CommandException.class, () ->
                command.getStudentAtDisplayedIndexForTest(model, invalidIndex));

        assertEquals(getOutOfBoundsStudentIndexMessage(invalidIndex, model), exception.getMessage());
    }

    @Test
    public void getStudentAtDisplayedIndex_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        CommandException exception = assertThrows(CommandException.class, () ->
                command.getStudentAtDisplayedIndexForTest(emptyModel, INDEX_FIRST_STUDENT));

        assertEquals(getOutOfBoundsStudentIndexMessage(INDEX_FIRST_STUDENT, emptyModel), exception.getMessage());
    }

    @Test
    public void getStudentAtDisplayedIndex_filteredList_returnsStudentFromFilteredList() throws CommandException {
        Student expectedStudent = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);

        Student actualStudent = command.getStudentAtDisplayedIndexForTest(model, INDEX_FIRST_STUDENT);

        assertEquals(expectedStudent, actualStudent);
    }

    /**
     * Exposes the protected shared lookup for testing.
     */
    private static class TestCommand extends Command {

        @Override
        public CommandResult execute(Model model) {
            throw new UnsupportedOperationException("Test command is not executable.");
        }

        /**
         * Returns the student selected by the inherited displayed-index lookup.
         *
         * @param model the model containing the displayed students
         * @param targetIndex the displayed student index
         * @return the selected student
         * @throws CommandException if the index is outside the displayed student list
         */
        private Student getStudentAtDisplayedIndexForTest(Model model, Index targetIndex) throws CommandException {
            return getStudentAtDisplayedIndex(model, targetIndex);
        }
    }
}
