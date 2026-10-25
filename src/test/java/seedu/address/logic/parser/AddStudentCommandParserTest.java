package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDIO_GROUP_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.commands.CommandTestUtil.STUDIO_GROUP_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDIO_GROUP_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDIO_GROUP_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.StudentBuilder;

public class AddStudentCommandParserTest {

    private static final String USAGE_SUFFIX = "\n" + AddStudentCommand.MESSAGE_USAGE;
    private AddStudentCommandParser parser = new AddStudentCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Student expectedStudent = new StudentBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB, new AddStudentCommand(expectedStudent));


        // multiple tags - all accepted
        Student expectedStudentMultipleTags = new StudentBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB,
                new AddStudentCommand(expectedStudentMultipleTags));
    }

    @Test
    public void parse_uppercasePrefixes_success() {
        Student expectedStudent = new StudentBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        assertParseSuccess(parser, "N/" + VALID_NAME_BOB + " P/" + VALID_PHONE_BOB
                        + " E/" + VALID_EMAIL_BOB + " T/" + VALID_TAG_FRIEND + " G/" + VALID_STUDIO_GROUP_BOB,
                new AddStudentCommand(expectedStudent));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedStudentString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedStudentString,
                CliSyntax.PREFIX_NAME.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedStudentString,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedStudentString,
                CliSyntax.PREFIX_EMAIL.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple studio groups
        assertParseFailure(parser, STUDIO_GROUP_DESC_AMY + validExpectedStudentString,
                CliSyntax.PREFIX_STUDIO_GROUP.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedStudentString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY
                        + STUDIO_GROUP_DESC_AMY + validExpectedStudentString,
                CliSyntax.PREFIX_NAME.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedStudentString,
                CliSyntax.PREFIX_NAME.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedStudentString,
                CliSyntax.PREFIX_EMAIL.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedStudentString,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid studio group
        assertParseFailure(parser, INVALID_STUDIO_GROUP_DESC + validExpectedStudentString,
                CliSyntax.PREFIX_STUDIO_GROUP.getDuplicateMessage() + USAGE_SUFFIX);

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedStudentString + INVALID_NAME_DESC,
                CliSyntax.PREFIX_NAME.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid email
        assertParseFailure(parser, validExpectedStudentString + INVALID_EMAIL_DESC,
                CliSyntax.PREFIX_EMAIL.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid phone
        assertParseFailure(parser, validExpectedStudentString + INVALID_PHONE_DESC,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid studio group
        assertParseFailure(parser, validExpectedStudentString + INVALID_STUDIO_GROUP_DESC,
                CliSyntax.PREFIX_STUDIO_GROUP.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Student expectedStudent = new StudentBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                        + STUDIO_GROUP_DESC_AMY,
                new AddStudentCommand(expectedStudent));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + STUDIO_GROUP_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB + EMAIL_DESC_BOB
                        + STUDIO_GROUP_DESC_BOB,
                CliSyntax.PREFIX_PHONE.getMissingMessage() + USAGE_SUFFIX);

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB
                        + STUDIO_GROUP_DESC_BOB,
                CliSyntax.PREFIX_EMAIL.getMissingMessage() + USAGE_SUFFIX);

        // missing studio group prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + VALID_STUDIO_GROUP_BOB,
                CliSyntax.PREFIX_STUDIO_GROUP.getMissingMessage() + USAGE_SUFFIX);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB
                        + VALID_STUDIO_GROUP_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB,
                Phone.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB,
                Email.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND + STUDIO_GROUP_DESC_BOB,
                Tag.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // invalid studio group
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + INVALID_STUDIO_GROUP_DESC,
                StudioGroup.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser,
                INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + STUDIO_GROUP_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_BOB,
                MESSAGE_INVALID_COMMAND + USAGE_SUFFIX);
    }
}
