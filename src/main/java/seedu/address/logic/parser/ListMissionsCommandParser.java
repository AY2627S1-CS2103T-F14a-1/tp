package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Parses a mission ID for listing submitted students.
 */
public class ListMissionsCommandParser implements Parser<ListMissionsCommand> {

    public static final String MESSAGE_MISSING_MISSION =
            "Specify a mission ID using " + PREFIX_MISSION + "MISSION_ID.";

    /**
     * Parses exactly one mission ID without accessing application state.
     *
     * @param args Arguments following the command word.
     * @return Command containing the canonical mission ID.
     * @throws ParseException If arguments are missing, repeated, or invalid.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public ListMissionsCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MISSION);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, ListMissionsCommand.MESSAGE_USAGE));
        }
        if (argMultimap.getValue(PREFIX_MISSION).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MISSION);
        String missionIdText = argMultimap.getValue(PREFIX_MISSION).orElseThrow().strip();
        if (missionIdText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        MissionId missionId = MissionParser.parseMissionId(missionIdText);
        return new ListMissionsCommand(missionId);
    }
}
