package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddStudentCommand object
 */
public class AddStudentCommandParser implements Parser<AddStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddStudentCommand
     * and returns an AddStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddStudentCommand parse(String args) throws ParseException {
        requireNonNull(args);

        try {
            ArgumentMultimap argMultimap =
                    ArgumentStructureValidator.validateStructureWithOptionals(
                            args,
                            new Prefix[]{PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDIO_GROUP},
                            new Prefix[]{PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDIO_GROUP},
                            PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_TAG, PREFIX_STUDIO_GROUP);
            Name name = StudentParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            Phone phone = StudentParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            Email email = StudentParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
            Set<Tag> tagList = StudentParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
            StudioGroup studioGroup = StudentParserUtil.parseStudioGroup(
                    argMultimap.getValue(PREFIX_STUDIO_GROUP).get());
            Student student = new Student(name, phone, email, tagList, studioGroup);
            return new AddStudentCommand(student);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + AddStudentCommand.MESSAGE_USAGE);
        }
    }
}
