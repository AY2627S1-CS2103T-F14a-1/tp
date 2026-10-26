package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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

        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructureWithOptionals(
                    args,
                    new Prefix[]{PREFIX_MISSION},
                    new Prefix[]{PREFIX_MISSION, PREFIX_NAME, PREFIX_DATE},
                    PREFIX_MISSION, PREFIX_NAME, PREFIX_DATE);
            EditMissionDescriptor editMissionDescriptor = new EditMissionDescriptor();
            if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
                editMissionDescriptor.setName(ParserUtil.parseMissionName(
                        argMultimap.getValue(PREFIX_NAME).orElseThrow())); // orElseThrow() should never happen normally
            }
            if (argMultimap.getValue(PREFIX_DATE).isPresent()) {
                editMissionDescriptor.setDeadline(ParserUtil.parseMissionDeadline(
                        argMultimap.getValue(PREFIX_DATE).orElseThrow()));
            }
            if (!editMissionDescriptor.isAnyFieldEdited()) {
                throw new ParseException(EditMissionCommand.MESSAGE_NOT_EDITED);
            }
            MissionId missionId = ParserUtil.parseMissionId(argMultimap.getValue(PREFIX_MISSION).orElseThrow());
            return new EditMissionCommand(missionId, editMissionDescriptor);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + EditMissionCommand.MESSAGE_USAGE);
        }
    }
}
