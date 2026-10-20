package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.Student;

public class AddMasteryCheckCommand extends Command {
    public static final String COMMAND_WORD = "addmc";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a mastery check appointment for the selected student.\n"
            + "Parameters: INDEX " + PREFIX_NUMBER + "NUMBER" + PREFIX_DATETIME + "DATE-TIME\n"
            + "NUMBER must be a positive integer identifying a mastery check.\n"
            + "DATE-TIME must be a real date in YYYY-MM-DD|HH:MM format.\n"
            + "Example: " + COMMAND_WORD + " 1 num/1 dt/2026-09-27|23:59";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s's mastery check: %2$s.";

    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s mastery check has already been recorded for number %2$d. No changes made.";

    private final Index targetIndex;
    private final int number;
    private final MasteryCheck masteryCheck;

    /**
     * Creates a command to record a number's studio.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param number Studio number for the record.
     * @param masteryCheck studio to record.
     */
    public AddMasteryCheckCommand(Index targetIndex, int number, MasteryCheck masteryCheck) {
        requireNonNull(targetIndex);
        requireNonNull(masteryCheck);

        checkArgument(number > 0, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.number = number;
        this.masteryCheck = masteryCheck;
    }

    /**
     * Records studio without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the addition or an existing studio.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Student> lastShownList = model.getFilteredStudentList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToUpdate = lastShownList.get(targetIndex.getZeroBased());
        RegisteredMasteryChecks registeredMasteryChecks = studentToUpdate.getRegisteredMasteryChecks();

        if (registeredMasteryChecks.hasMasteryCheck(number)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName(), number));
        }

        Student updatedStudent = createUpdatedStudent(
                studentToUpdate, registeredMasteryChecks.withMasteryCheck(number, masteryCheck));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), number));
    }

    private Student createUpdatedStudent(Student student, RegisteredMasteryChecks registeredMasteryChecks) {
        assert student != null;
        assert registeredMasteryChecks != null;

        return new Student(student.getName(), student.getPhone(), student.getEmail(),
                student.getTags(), student.getStudioGroup(), student.getMissionSubmissions(),
                student.getRegisteredStudioRecords(), registeredMasteryChecks);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMasteryCheckCommand otherAddMasteryCheckCommand)) {
            return false;
        }

        return targetIndex.equals(otherAddMasteryCheckCommand.targetIndex)
                && number == otherAddMasteryCheckCommand.number
                && masteryCheck.equals(otherAddMasteryCheckCommand.masteryCheck);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("number", number)
                .add("masteryCheck", masteryCheck)
                .toString();
    }
}
