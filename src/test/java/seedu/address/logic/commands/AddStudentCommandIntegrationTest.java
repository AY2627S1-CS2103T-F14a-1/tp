package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddStudentCommand}.
 */
public class AddStudentCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newStudent_success() {
        Student validStudent = new StudentBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addStudent(validStudent);

        assertCommandSuccess(new AddStudentCommand(validStudent), model,
                String.format(AddStudentCommand.MESSAGE_SUCCESS, Messages.format(validStudent)),
                expectedModel);
    }

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        Student studentInList = model.getAddressBook().getStudentList().get(0);
        assertCommandFailure(new AddStudentCommand(studentInList), model,
                AddStudentCommand.MESSAGE_DUPLICATE_STUDENT);
    }


    @Test
    public void execute_sameMatricNumberDifferentName_rejectsWithoutChangingModel() {
        Student existing = model.getAddressBook().getStudentList().get(0);
        Student duplicate = new StudentBuilder(existing).withName("Different Name").build();
        assertCommandFailure(new AddStudentCommand(duplicate), model, AddStudentCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_sameNameDifferentMatricNumber_addsStudent() {
        Student existing = model.getAddressBook().getStudentList().get(0);
        Student distinct = new StudentBuilder(existing).withMatricNumber("A9999999Z").build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addStudent(distinct);

        assertCommandSuccess(new AddStudentCommand(distinct), model,
                String.format(AddStudentCommand.MESSAGE_SUCCESS, Messages.format(distinct)), expectedModel);
    }
}
