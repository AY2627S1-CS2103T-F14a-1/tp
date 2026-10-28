package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListMissionCatalogueCommand;
import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.model.mission.MissionId;

/**
 * Parses catalogue-listing or mission-submission-listing requests.
 */
public class ListMissionsCommandParser implements Parser<Command> {

    public static final String MESSAGE_MISSING_MISSION =
            "Specify a mission ID using " + PREFIX_MISSION + "MISSION_ID.";

    /**
     * Creates a catalogue command for blank arguments or parses a mission ID.
     *
     * @param args Arguments following the command word.
     * @return Catalogue-listing command or submission-listing command.
     * @throws ParseException If nonblank arguments have invalid structure or values.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isBlank()) {
            return new ListMissionCatalogueCommand();
        }

        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(args, PREFIX_MISSION);

            MissionId missionId = MissionParserUtil.parseMissionId(argMultimap.getValue(PREFIX_MISSION).get());
            return new ListMissionsCommand(missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + ListMissionsCommand.MESSAGE_USAGE);
        }
    }
}
