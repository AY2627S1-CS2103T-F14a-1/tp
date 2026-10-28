package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.Locale;
import java.util.Optional;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListMissionCatalogueCommand;
import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.model.mission.MissionId;
import seedu.address.model.mission.MissionSubmissionStatus;

/**
 * Parses catalogue-listing or mission-submission-listing requests.
 */
public class ListMissionsCommandParser implements Parser<Command> {

    public static final String MESSAGE_INVALID_STATUS =
            "Invalid status filter. Use s/submitted or s/overdue.";

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
            ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                    args,
                    new Prefix[]{PREFIX_MISSION},
                    new Prefix[]{PREFIX_MISSION, PREFIX_STATUS},
                    PREFIX_MISSION, PREFIX_STATUS);

            MissionId missionId = MissionParserUtil.parseMissionId(
                    arguments.getValue(PREFIX_MISSION).orElseThrow());
            Optional<MissionSubmissionStatus> status = parseStatus(arguments.getValue(PREFIX_STATUS));
            if (status.isEmpty()) {
                return new ListMissionsCommand(missionId);
            }

            return ListMissionsCommand.withStatus(missionId, status.orElseThrow());
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + ListMissionsCommand.MESSAGE_USAGE);
        }
    }

    /**
     * Parses an optional submitted or overdue status filter.
     *
     * @param value Status value supplied after s/, if present.
     * @return Requested status, or empty when no status filter was supplied.
     * @throws ParseException If the supplied status is empty or unsupported.
     */
    private Optional<MissionSubmissionStatus> parseStatus(Optional<String> value) throws ParseException {
        assert value != null;

        if (value.isEmpty()) {
            return Optional.empty();
        }

        MissionSubmissionStatus status = switch (value.orElseThrow().strip().toLowerCase(Locale.ROOT)) {
            case "submitted" -> MissionSubmissionStatus.SUBMITTED;
            case "overdue" -> MissionSubmissionStatus.OVERDUE;
            default -> throw new ParseException(MESSAGE_INVALID_STATUS);
        };
        return Optional.of(status);
    }
}

