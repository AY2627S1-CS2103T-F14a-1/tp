package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MATRIC_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.logic.parser.util.StudentParserUtil;
import seedu.address.model.student.Email;
import seedu.address.model.student.MatriculationNumber;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

/**
 * Parses required student profile fields and optional tags, and creates a new {@code AddStudentCommand}.
 */
public class AddStudentCommandParser implements Parser<AddStudentCommand> {

    /**
     * Parses the required name, matriculation number, phone, email, and studio group, together with optional tags.
     *
     * @param args Arguments following the add-student command word.
     * @return A command containing the validated student profile.
     * @throws NullPointerException If args is null.
     * @throws ParseException If the argument structure or any supplied profile value is invalid.
     */
    public AddStudentCommand parse(String args) throws ParseException {
        requireNonNull(args);

        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructureWithOptionals(
                    args,
                    new Prefix[]{PREFIX_NAME, PREFIX_MATRIC_NUMBER, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDIO_GROUP},
                    new Prefix[]{PREFIX_NAME, PREFIX_MATRIC_NUMBER, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDIO_GROUP},
                    PREFIX_NAME, PREFIX_MATRIC_NUMBER, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_TAG, PREFIX_STUDIO_GROUP);
            Name name = StudentParserUtil.parseName(argMultimap.getRequiredValue(PREFIX_NAME));
            MatriculationNumber matricNumber =
                    StudentParserUtil.parseMatricNumber(argMultimap.getRequiredValue(PREFIX_MATRIC_NUMBER));
            Phone phone = StudentParserUtil.parsePhone(argMultimap.getRequiredValue(PREFIX_PHONE));
            Email email = StudentParserUtil.parseEmail(argMultimap.getRequiredValue(PREFIX_EMAIL));
            Set<Tag> tagList = StudentParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
            StudioGroup studioGroup =
                    StudentParserUtil.parseStudioGroup(argMultimap.getRequiredValue(PREFIX_STUDIO_GROUP));
            Student student = new Student(name, matricNumber, phone, email, tagList, studioGroup);
            return new AddStudentCommand(student);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + AddStudentCommand.MESSAGE_USAGE);
        }
    }
}
