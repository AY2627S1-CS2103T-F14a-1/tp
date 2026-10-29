package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.logic.parser.util.StudentParserUtil;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditStudentCommand object
 */
public class EditStudentCommandParser implements Parser<EditStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditStudentCommand
     * and returns an EditStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditStudentCommand parse(String args) throws ParseException {
        requireNonNull(args);

        try {
            ArgumentMultimap argMultimap =
                    ArgumentStructureValidator.validateStructureWithOptionals(args,
                            new Prefix[]{PREFIX_INDEX},
                            new Prefix[]{PREFIX_INDEX, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_STUDIO_GROUP},
                            PREFIX_INDEX, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_TAG, PREFIX_STUDIO_GROUP);

            EditStudentDescriptor editStudentDescriptor = buildEditStudentDescriptor(argMultimap);
            if (!editStudentDescriptor.isAnyFieldEdited()) {
                throw new ParseException(EditStudentCommand.MESSAGE_NOT_EDITED);
            }

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_INDEX));
            return new EditStudentCommand(index, editStudentDescriptor);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + EditStudentCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Builds an {@code EditStudentDescriptor} from the optional edit fields, leaving fields without prefixes unchanged
     * in the descriptor.
     * A single empty tag value clears the tags.
     *
     * @param argMultimap Tokenized command arguments containing the optional edit fields.
     * @return A descriptor containing only the supplied edits.
     * @throws ParseException If a supplied field value is invalid.
     */
    private EditStudentDescriptor buildEditStudentDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editStudentDescriptor.setName(StudentParserUtil.parseName(argMultimap.getRequiredValue(PREFIX_NAME)));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editStudentDescriptor.setPhone(StudentParserUtil.parsePhone(argMultimap.getRequiredValue(PREFIX_PHONE)));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editStudentDescriptor.setEmail(StudentParserUtil.parseEmail(argMultimap.getRequiredValue(PREFIX_EMAIL)));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editStudentDescriptor::setTags);
        if (argMultimap.getValue(PREFIX_STUDIO_GROUP).isPresent()) {
            editStudentDescriptor.setStudioGroup(
                    StudentParserUtil.parseStudioGroup(argMultimap.getRequiredValue(PREFIX_STUDIO_GROUP)));
        }
        return editStudentDescriptor;
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contains only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(StudentParserUtil.parseTags(tagSet));
    }

}
