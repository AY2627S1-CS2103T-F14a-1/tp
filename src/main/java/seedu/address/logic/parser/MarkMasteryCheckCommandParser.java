package seedu.address.logic.parser;

import seedu.address.logic.commands.MarkMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a student index and mastery check number into a command that marks the check complete.
 */
public class MarkMasteryCheckCommandParser implements Parser<MarkMasteryCheckCommand> {
    /**
     * Parses the required student index and single num/ argument using the shared mastery check parser.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated displayed index and positive mastery check number.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If an argument is missing or invalid, or the number prefix is repeated.
     */
    @Override
    public MarkMasteryCheckCommand parse(String args) throws ParseException {
        MasteryCheckParser parser = new MasteryCheckParser(args, MarkMasteryCheckCommand.MESSAGE_USAGE);
        return new MarkMasteryCheckCommand(parser.getIndex(), parser.getMasteryCheckNumber());
    }
}
