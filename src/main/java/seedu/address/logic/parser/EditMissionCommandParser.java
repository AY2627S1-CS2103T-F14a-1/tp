package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.logic.commands.EditMissionCommand;
import seedu.address.logic.commands.EditMissionCommand.EditMissionDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Parses an existing mission ID and optional replacement fields.
 */
public class EditMissionCommandParser implements Parser<EditMissionCommand> {

    /**
     * Parses mission edits without accessing application state.
     *
     * @param args Arguments following the command word.
     * @return Command containing the mission ID and validated replacements.
     * @throws ParseException If required fields are missing, repeated, or invalid.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public EditMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_MISSION, PREFIX_NAME, PREFIX_DATE);
        if (!argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_MISSION).isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, EditMissionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MISSION, PREFIX_NAME, PREFIX_DATE);
        MissionId missionId = ParserUtil.parseMissionId(
                argMultimap.getValue(PREFIX_MISSION).orElseThrow());
        EditMissionDescriptor editMissionDescriptor = new EditMissionDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editMissionDescriptor.setName(ParserUtil.parseMissionName(
                    argMultimap.getValue(PREFIX_NAME).orElseThrow()));
        }
        if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
            editMissionDescriptor.setDeadline(ParserUtil.parseMissionDeadline(
                    argMultimap.getValue(PREFIX_DATE).orElseThrow()));
        }
        if (!editMissionDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditMissionCommand.MESSAGE_NOT_EDITED);
        }

        return new EditMissionCommand(missionId, editMissionDescriptor);
    }
}
