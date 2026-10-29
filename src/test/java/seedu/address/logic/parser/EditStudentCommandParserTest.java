package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STUDIO_GROUP_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.STUDIO_GROUP_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDIO_GROUP_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STUDIO_GROUP_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_STUDENT;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditStudentDescriptorBuilder;

public class EditStudentCommandParserTest {

    private static final String USAGE_SUFFIX = MESSAGE_SEE_USAGE + EditStudentCommand.MESSAGE_USAGE;

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;

    private EditStudentCommandParser parser = new EditStudentCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "id/1",
                EditStudentCommand.MESSAGE_NOT_EDITED + USAGE_SUFFIX);

        // no index and no field specified
        assertParseFailure(parser, "", CliSyntax.PREFIX_INDEX.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "id/1" + INVALID_NAME_DESC,
                Name.MESSAGE_CONSTRAINTS + USAGE_SUFFIX); // invalid name
        assertParseFailure(parser, "id/1" + INVALID_PHONE_DESC,
                Phone.MESSAGE_CONSTRAINTS + USAGE_SUFFIX); // invalid phone
        assertParseFailure(parser, "id/1" + INVALID_EMAIL_DESC,
                Email.MESSAGE_CONSTRAINTS + USAGE_SUFFIX); // invalid email
        assertParseFailure(parser, "id/1" + INVALID_TAG_DESC,
                Tag.MESSAGE_CONSTRAINTS + USAGE_SUFFIX); // invalid tag
        assertParseFailure(
                parser, "id/1" + INVALID_STUDIO_GROUP_DESC,
                StudioGroup.MESSAGE_CONSTRAINTS + USAGE_SUFFIX); // invalid studio group

        // invalid phone followed by valid email
        assertParseFailure(parser, "id/1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY,
                Phone.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Student} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "id/1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "id/1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_STUDENT;
        String userInput = "id/" + targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND + STUDIO_GROUP_DESC_AMY;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).withStudioGroup(VALID_STUDIO_GROUP_AMY)
                .build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_STUDENT;
        String userInput = "id/" + targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_STUDENT;
        String userInput = "id/" + targetIndex.getOneBased() + NAME_DESC_AMY;
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = "id/" + targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditStudentDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = "id/" + targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditStudentDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = "id/" + targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditStudentDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // studio group
        userInput = "id/" + targetIndex.getOneBased() + STUDIO_GROUP_DESC_AMY;
        descriptor = new EditStudentDescriptorBuilder().withStudioGroup(VALID_STUDIO_GROUP_AMY).build();
        expectedCommand = new EditStudentCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddStudentCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_STUDENT;
        String userInput = "id/" + targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // invalid followed by valid
        userInput = "id/" + targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple valid fields repeated
        userInput = "id/" + targetIndex.getOneBased() + PHONE_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND + STUDIO_GROUP_DESC_AMY
                + STUDIO_GROUP_DESC_BOB;

        assertParseFailure(parser, userInput,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);

        // multiple invalid values
        userInput = "id/" + targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_STUDIO_GROUP_DESC
                + INVALID_EMAIL_DESC + INVALID_PHONE_DESC
                + INVALID_EMAIL_DESC + INVALID_STUDIO_GROUP_DESC;

        assertParseFailure(parser, userInput,
                CliSyntax.PREFIX_PHONE.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_STUDENT;
        String userInput = "id/" + targetIndex.getOneBased() + TAG_EMPTY;

        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withTags().build();
        EditStudentCommand expectedCommand = new EditStudentCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_onlyStudioGroupSpecified_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setStudioGroup(new StudioGroup("2A"));

        EditStudentCommand expectedCommand = new EditStudentCommand(INDEX_FIRST_STUDENT, descriptor);

        assertParseSuccess(parser, "id/1 g/2A", expectedCommand);
    }

    @Test
    public void parse_blankSuppliedField_reportsMissingArgumentAndAcceptsLaterValidEdit() {
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditStudentCommand expectedCommand = new EditStudentCommand(INDEX_FIRST_STUDENT, descriptor);

        for (Prefix prefix : List.of(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE,
                CliSyntax.PREFIX_EMAIL, CliSyntax.PREFIX_STUDIO_GROUP)) {
            for (String blankValue : List.of("", " ", "\t", "\u2003")) {
                assertParseSuccess(parser, "id/1" + NAME_DESC_AMY, expectedCommand);
                assertParseFailure(parser, "id/1 " + prefix + blankValue,
                        "Prefix " + prefix + " is missing its argument." + USAGE_SUFFIX);
                assertParseSuccess(parser, "id/1" + NAME_DESC_AMY, expectedCommand);
            }
        }
    }

    @Test
    public void parse_emptyStudioGroup_failure() {
        assertParseFailure(parser, "id/1 g/",
                "Prefix g/ is missing its argument." + USAGE_SUFFIX);
    }

    @Test
    public void parse_invalidStudioGroup_failure() {
        assertParseFailure(parser, "id/1 g/2a",
                StudioGroup.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
    }

    @Test
    public void parse_presentIndexWithPreamble_reportsInvalidFormat() {
        assertParseFailure(parser, "unexpected id/1" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_repeatedIndex_reportsDuplicateIndex() {
        assertParseFailure(parser, "id/1 id/2" + NAME_DESC_AMY,
                CliSyntax.PREFIX_INDEX.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_invalidIndexWithValidEdit_reportsIndexConstraint() {
        assertParseFailure(parser, "id/0" + NAME_DESC_AMY,
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/" + NAME_DESC_AMY,
                "Prefix id/ is missing its argument." + USAGE_SUFFIX);
    }

    @Test
    public void parseTagsForEdit_nullCollection_throwsAssertionError() throws ReflectiveOperationException {
        assertTrue(EditStudentCommandParser.class.desiredAssertionStatus(), "This test requires assertions enabled");
        Method parseTags = EditStudentCommandParser.class.getDeclaredMethod("parseTagsForEdit", Collection.class);
        parseTags.setAccessible(true);

        // Normal parsing always supplies a non-null collection; invoke the helper to verify its internal contract.
        InvocationTargetException error = assertThrows(InvocationTargetException.class, () ->
                parseTags.invoke(parser, new Object[]{null}));
        assertInstanceOf(AssertionError.class, error.getCause());
    }
}
