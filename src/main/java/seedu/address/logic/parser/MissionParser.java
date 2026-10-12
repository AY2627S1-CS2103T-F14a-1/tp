package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Validates a displayed student index and a single mission ID. */
public class MissionParser {

    private final Index index;
    private final MissionId missionId;

    /**
     * Parses a mission submission command using its usage message for syntax errors.
     *
     * @param args Arguments following the command word.
     * @param usage Usage instructions for the calling command.
     * @throws NullPointerException If either argument is null.
     * @throws ParseException If the index or mission argument is missing, repeated or invalid.
     */
    public MissionParser(String args, String usage) throws ParseException {
        requireNonNull(args);
        requireNonNull(usage);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.strip().replaceAll("\\s+", " "),
                PREFIX_MISSION);
        String indexText = arguments.getPreamble();
        if (!indexText.matches("[1-9][0-9]*") || arguments.getAllValues(PREFIX_MISSION).size() != 1) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        index = ParserUtil.parseIndex(indexText);
        try {
            missionId = new MissionId(arguments.getValue(PREFIX_MISSION).orElseThrow());
        } catch (IllegalArgumentException e) {
            throw new ParseException(MissionId.MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * Returns the validated student index.
     *
     * @return Displayed student index.
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Returns the validated mission identifier.
     *
     * @return Canonical mission ID.
     */
    public MissionId getMissionId() {
        return missionId;
    }
}
