package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import java.util.Locale;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Validates the shared student index and mission ID arguments for mission commands.
 */
public class MissionParser {

    public static final String MESSAGE_MISSING_INDEX = "Specify a student index using " + PREFIX_INDEX + "INDEX.";
    public static final String MESSAGE_MISSING_MISSION = "Specify a mission ID using "
            + PREFIX_MISSION + "MISSION_ID.";
    public static final String MESSAGE_DUPLICATE_MISSION = "Mission ID must be specified only once.";
    public static final String MESSAGE_INVALID_STUDENT_INDEX =
            "Student index must be a positive integer without a sign, decimal point, or leading zero.";
    public static final String MESSAGE_INVALID_MISSION_ID = MissionId.MESSAGE_CONSTRAINTS;
    private static final Pattern POSITIVE_INTEGER = Pattern.compile("[1-9][0-9]*");

    private final String usage;
    private final Index index;
    private final MissionId missionId;

    /**
     * Parses arguments using the calling command's usage in format errors.
     *
     * @param args Arguments following the command word.
     * @param usage Usage instructions for the calling command.
     * @throws NullPointerException If either argument is null.
     * @throws ParseException If required information is missing or invalid.
     */
    public MissionParser(String args, String usage) throws ParseException {
        requireNonNull(args);
        this.usage = requireNonNull(usage);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(
                normalizeArguments(args), PREFIX_INDEX, PREFIX_MISSION);
        index = extractIndex(arguments);
        missionId = parseMissionId(extractMissionId(arguments));
    }

    /**
     * Returns the validated displayed student index.
     *
     * @return The student index.
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Returns the canonical mission ID.
     *
     * @return The mission ID.
     */
    public MissionId getMissionId() {
        return missionId;
    }

    /**
     * Extracts and validates the student index from the required index prefix.
     *
     * @param arguments Tokenized command arguments.
     * @return The validated displayed student index.
     * @throws ParseException If the index prefix is missing or repeated, its value is invalid, or a preamble exists.
     */
    private Index extractIndex(ArgumentMultimap arguments) throws ParseException {
        if (!arguments.arePrefixesPresent(PREFIX_INDEX)
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_INDEX);
        String indexText = arguments.getValue(PREFIX_INDEX).get();
        rejectExtraArguments(indexText);
        return parseStudentIndex(indexText);
    }

    /**
     * Parses a student index without accepting signs or leading zeros.
     *
     * @param oneBasedIndex Student index text.
     * @return A valid one-based index.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the index syntax or numeric value is invalid.
     */
    public static Index parseStudentIndex(String oneBasedIndex) throws ParseException {
        requireNonNull(oneBasedIndex);

        String trimmedIndex = oneBasedIndex.trim();
        if (!POSITIVE_INTEGER.matcher(trimmedIndex).matches()) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_INDEX);
        }

        try {
            return ParserUtil.parseIndex(trimmedIndex);
        } catch (ParseException e) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_INDEX);
        }
    }

    /**
     * Parses a case-insensitive mission ID into its canonical uppercase form.
     *
     * @param missionIdText Mission ID text.
     * @return The validated mission ID.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the mission ID is invalid.
     */
    public static MissionId parseMissionId(String missionIdText) throws ParseException {
        requireNonNull(missionIdText);

        try {
            return new MissionId(missionIdText.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_MISSION_ID);
        }
    }

    /**
     * Normalizes whitespace while preserving the required lowercase prefix.
     * The leading space allows the tokenizer to detect an initial prefix.
     *
     * @param args Original command arguments.
     * @return Arguments suitable for the existing tokenizer.
     */
    private String normalizeArguments(String args) {
        return " " + args.strip().replaceAll("\\s+", " ");
    }

    /**
     * Extracts the single required mission ID value.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The non-empty mission ID text.
     * @throws ParseException If the mission ID is missing, repeated, or has extra arguments.
     */
    private String extractMissionId(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.arePrefixesPresent(PREFIX_MISSION)) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        if (argMultimap.getAllValues(PREFIX_MISSION).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_MISSION);
        }

        String missionIdText = argMultimap.getValue(PREFIX_MISSION).orElseThrow();
        if (missionIdText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        rejectExtraArguments(missionIdText);
        return missionIdText;
    }

    /**
     * Rejects extra arguments or unexpected prefixes within an index or mission ID field.
     *
     * @param text Trimmed, whitespace-normalized field text.
     * @throws ParseException If additional argument structure is present.
     */
    private void rejectExtraArguments(String text) throws ParseException {
        if (text.contains(" ") || text.contains("/")) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
    }
}
