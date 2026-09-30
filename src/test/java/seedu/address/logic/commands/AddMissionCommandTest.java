package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests mission submission execution and preservation of existing records.
 */
public class AddMissionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_invalidArguments_throwsException() {
        assertThrows(NullPointerException.class, () -> new AddMissionCommand(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new AddMissionCommand(INDEX_FIRST_PERSON, 0));
        assertThrows(IllegalArgumentException.class, () -> new AddMissionCommand(INDEX_FIRST_PERSON, 11));
    }

    @Test
    public void execute_validIndex_preservesOtherWeeksAndPeople() {
        Person original = model.getFilteredPersonList().get(0);
        Person student = new PersonBuilder(original).withMissionSubmissions(2, 4).build();
        model.setPerson(original, student);

        Person updated = new PersonBuilder(student).withMissionSubmissions(2, 3, 4).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(student, updated);

        String feedback = String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_PERSON, 3),
                model, feedback, expectedModel);
        assertFalse(student.getMissionSubmissions().hasSubmission(3));
    }

    @Test
    public void execute_boundaryWeeks_recordsBothWeeks() throws Exception {
        Person original = model.getFilteredPersonList().get(0);
        Person student = new PersonBuilder(original).withMissionSubmissions().build();
        model.setPerson(original, student);

        CommandResult firstResult = new AddMissionCommand(INDEX_FIRST_PERSON, 1).execute(model);
        CommandResult lastResult = new AddMissionCommand(INDEX_FIRST_PERSON, 10).execute(model);

        assertEquals(String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 1),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 10),
                lastResult.getFeedbackToUser());

        Person expected = new PersonBuilder(student).withMissionSubmissions(1, 10).build();
        assertEquals(expected, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_existingSubmission_leavesModelUnchanged() {
        Person original = model.getFilteredPersonList().get(0);
        Person student = new PersonBuilder(original).withMissionSubmissions(3).build();
        model.setPerson(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(AddMissionCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_PERSON, 3),
                model, feedback, expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertCommandFailure(new AddMissionCommand(invalidIndex, 3),
                model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new AddMissionCommand(INDEX_FIRST_PERSON, 3),
                emptyModel, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Person original = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person student = new PersonBuilder(original).withMissionSubmissions(2).build();
        model.setPerson(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        Person updated = new PersonBuilder(student).withMissionSubmissions(2, 3).build();
        expectedModel.setPerson(student, updated);

        String feedback = String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_PERSON, 3),
                model, feedback, expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(updated, model.getFilteredPersonList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertCommandFailure(new AddMissionCommand(INDEX_SECOND_PERSON, 3),
                model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndWeek_returnsTrue() {
        AddMissionCommand command = new AddMissionCommand(INDEX_FIRST_PERSON, 3);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMissionCommand(INDEX_FIRST_PERSON, 3)));
        assertFalse(command.equals(new AddMissionCommand(INDEX_SECOND_PERSON, 3)));
        assertFalse(command.equals(new AddMissionCommand(INDEX_FIRST_PERSON, 4)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addmission"));
    }
}
