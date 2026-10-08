package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Remark;

public class RemarkCommandTest {

    Model model = new ModelManager();
    Remark amyRemark = new Remark(VALID_REMARK_AMY);
    Remark bobRemark = new Remark(VALID_REMARK_BOB);

    @Test
    public void execute() {
    }

    @Test
    public void equals() {
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, amyRemark);
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, amyRemark);

        assertTrue(standardCommand.equals(standardCommand));

        assertFalse(standardCommand.equals(null));

        assertFalse(standardCommand.equals(new ExitCommand()));

        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, amyRemark)));

        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, bobRemark)));
    }
}
