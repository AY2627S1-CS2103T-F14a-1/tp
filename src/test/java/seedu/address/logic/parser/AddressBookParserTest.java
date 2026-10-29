package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteMasteryCheckCommand;
import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.DeleteStudioRecordCommand;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkMasteryCheckCommand;
import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.commands.UnmarkMasteryCheckCommand;
import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MasteryCheckParserUtil;
import seedu.address.model.ModelManager;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.EditStudentDescriptorBuilder;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_addStudent() throws Exception {
        Student student = new StudentBuilder().build();
        AddStudentCommand command = (AddStudentCommand) parser.parseCommand(StudentUtil.getAddStudentCommand(student));
        assertEquals(new AddStudentCommand(student), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_deleteStudent() throws Exception {
        DeleteStudentCommand command = (DeleteStudentCommand) parser.parseCommand(
                DeleteStudentCommand.COMMAND_WORD + " id/" + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new DeleteStudentCommand(INDEX_FIRST_STUDENT), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student student = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(student).build();
        EditStudentCommand command = (EditStudentCommand) parser.parseCommand(EditStudentCommand.COMMAND_WORD + " id/"
                + INDEX_FIRST_STUDENT.getOneBased() + " " + StudentUtil.getEditStudentDescriptorDetails(descriptor));
        assertEquals(new EditStudentCommand(INDEX_FIRST_STUDENT, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(", ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertInstanceOf(HelpCommand.class, parser.parseCommand(HelpCommand.COMMAND_WORD));
        assertInstanceOf(HelpCommand.class, parser.parseCommand(HelpCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_listWithoutArguments_returnsCommandWithoutWarning() throws Exception {
        for (String input : List.of("list", "  list \t ", "LIST")) {
            ListCommand command = assertInstanceOf(ListCommand.class, parser.parseCommand(input));
            CommandResult result = command.execute(new ModelManager());

            assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
            assertEquals(List.of(), result.getWarnings());
        }
    }

    @Test
    public void parseCommand_listWithArguments_returnsCommandWithWarning() throws Exception {
        for (String input : List.of("list missions m/01", "list 3", "list m/M01", "LIST extra")) {
            ListCommand command = assertInstanceOf(ListCommand.class, parser.parseCommand(input));
            CommandResult result = command.execute(new ModelManager());

            assertEquals(ListCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
            assertEquals(List.of(ListCommand.MESSAGE_WARNING_EXTRA_ARGUMENTS), result.getWarnings());
        }
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(ViewCommand.COMMAND_WORD + " id/"
                + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_STUDENT), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_removedMissionCommands_throwsUnknownCommand() {
        for (String input : List.of(
                "viewmission m/M01",
                "VIEWMISSION m/M01 s/overdue",
                "listoverdue",
                "LISTOVERDUE")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(input));
        }
    }

    @Test
    public void parseCommand_markMissionIgnoringCase_returnsCommand() throws Exception {
        MarkMissionCommand expected = new MarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"));

        assertEquals(expected, parser.parseCommand("markmission id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("MARKMISSION id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("MarkMission id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("  markmission\tid/1\tm/M03  "));
    }

    @Test
    public void parseCommand_markMissionMissingIndex_reportsMissingIndex() {
        assertThrows(ParseException.class, () ->
                parser.parseCommand("markmission"));
    }

    @Test
    public void parseCommand_misspelledMarkMission_reportsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("markmisson 1 m/M03"));
    }

    @Test
    public void parseCommand_unmarkMissionIgnoringCase_returnsCommand() throws Exception {
        UnmarkMissionCommand expected = new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"));

        assertEquals(expected, parser.parseCommand("unmarkmission id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("UNMARKMISSION id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("UnmarkMission id/1 m/M03"));
        assertEquals(expected, parser.parseCommand("  unmarkmission\tid/1\tm/M03  "));
    }

    @Test
    public void parseCommand_unmarkMissionMissingIndex_reportsMissingIndex() {
        assertThrows(ParseException.class, () ->
                parser.parseCommand("unmarkmission"));
    }

    @Test
    public void parseCommand_misspelledUnmarkMission_reportsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("unmarkmisson 1 m/M03"));
    }

    @Test
    public void parseCommand_addStudioRecordIgnoringCase_returnsCommand() throws Exception {
        AddStudioRecordCommand expected = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(0));

        assertEquals(expected, parser.parseCommand("addstudiorecord id/1 w/3 ef/0"));
        assertEquals(expected, parser.parseCommand("ADDSTUDIORECORD id/1 w/3 ef/0"));
        assertEquals(expected, parser.parseCommand("AddStudioRecord id/1 w/3 ef/0"));
        assertEquals(expected, parser.parseCommand("  addstudiorecord\tid/1\tw/3 ef/0  "));
    }

    @Test
    public void parseCommand_addStudioRecordMissingArguments_reportsAllMissingPrefixes() {
        String expected = String.join("\n", CliSyntax.PREFIX_STUDENT_INDEX.getMissingMessage(),
                CliSyntax.PREFIX_WEEK.getMissingMessage(), CliSyntax.PREFIX_STUDIO_EFFORT.getMissingMessage())
                + MESSAGE_SEE_USAGE + AddStudioRecordCommand.MESSAGE_USAGE;

        assertThrows(ParseException.class, expected, () ->
                parser.parseCommand("addstudiorecord"));
    }

    @Test
    public void parseCommand_misspelledAddStudioRecord_reportsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("addattendence 1 w/3"));
    }

    @Test
    public void parseCommand_deleteStudioRecordIgnoringCase_returnsCommand() throws Exception {
        DeleteStudioRecordCommand expected = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);

        assertEquals(expected, parser.parseCommand("delstudiorecord id/1 w/3"));
        assertEquals(expected, parser.parseCommand("DELSTUDIORECORD id/1 w/3"));
        assertEquals(expected, parser.parseCommand("DelStudioRecord id/1 w/3"));
        assertEquals(expected, parser.parseCommand("  delstudiorecord\tid/1\tw/3  "));
    }

    @Test
    public void parseCommand_deleteStudioRecordMissingArguments_reportsAllMissingPrefixes() {
        String expected = String.join("\n", CliSyntax.PREFIX_STUDENT_INDEX.getMissingMessage(),
                CliSyntax.PREFIX_WEEK.getMissingMessage())
                        + MESSAGE_SEE_USAGE + DeleteStudioRecordCommand.MESSAGE_USAGE;

        assertThrows(ParseException.class, expected, () ->
                parser.parseCommand("delstudiorecord"));
    }

    @Test
    public void parseCommand_editStudioRecordIgnoringCase_returnsCommand() throws Exception {
        EditStudioRecordCommand expected = new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, 3, new StudioRecord(50));

        assertEquals(expected, parser.parseCommand("editstudiorecord id/1 w/3 ef/50"));
        assertEquals(expected, parser.parseCommand("EDITSTUDIORECORD id/1 w/3 ef/50"));
        assertEquals(expected, parser.parseCommand("  editstudiorecord\tid/1\tw/3 ef/50  "));
    }

    @Test
    public void parseCommand_misspelledEditStudioRecord_reportsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("editstudiorecor 1 w/3 ef/50"));
    }

    @Test
    public void parseCommand_misspelledDeleteStudioRecord_reportsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () ->
                parser.parseCommand("delstudorecord 1 w/3"));
    }

    @Test
    public void parseCommand_masteryCheck_dispatchesCaseInsensitiveCommand() throws Exception {
        AddMasteryCheckCommand expected = new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1,
                new MasteryCheck(LocalDateTime.of(LocalDate.now().getYear(), 8, 18, 14, 30)));
        assertEquals(expected, parser.parseCommand("addmc id/1 num/1 dt/18-08|14:30"));
        assertEquals(expected, parser.parseCommand("ADDMC id/1 num/1 dt/18-08|14:30"));
    }

    @Test
    public void parseCommand_masteryCheckInvalidDate_throwsParseException() {
        assertThrows(ParseException.class,
                MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME
                        + MESSAGE_SEE_USAGE + AddMasteryCheckCommand.MESSAGE_USAGE, () ->
                parser.parseCommand("addmc id/1 num/1 dt/31-04|14:30"));
    }


    @Test
    public void parseCommand_deleteMasteryCheckIgnoringCase_returnsDeletionCommand() throws Exception {
        DeleteMasteryCheckCommand expected = new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(expected, parser.parseCommand("delmc id/1 num/1"));
        assertEquals(expected, parser.parseCommand("DELMC id/1 num/1"));
        assertEquals(expected, parser.parseCommand("  DelMc id/1 num/1  "));
    }

    @Test
    public void parseCommand_deleteMasteryCheckInvalidNumber_reportsMasteryCheckConstraint() {
        assertThrows(ParseException.class,
                MasteryCheckParserUtil.MESSAGE_ERROR_NEGATIVE_NUMBER
                        + MESSAGE_SEE_USAGE + DeleteMasteryCheckCommand.MESSAGE_USAGE, () ->
                parser.parseCommand("delmc id/1 num/0"));
    }

    @Test
    public void parseCommand_markMasteryCheckIgnoringCase_returnsCommand() throws Exception {
        MarkMasteryCheckCommand expected = new MarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(expected, parser.parseCommand("markmc id/1 num/1"));
        assertEquals(expected, parser.parseCommand("MARKMC id/1 num/1"));
    }


    @Test
    public void parseCommand_unmarkMasteryCheckIgnoringCase_returnsCommand() throws Exception {
        UnmarkMasteryCheckCommand expected = new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(expected, parser.parseCommand("unmarkmc id/1 num/1"));
        assertEquals(expected, parser.parseCommand("UNMARKMC id/1 num/1"));
    }

    @Test
    public void parseCommand_addNoteIgnoringCase_returnsCommand() throws Exception {
        AddNoteCommand expected = new AddNoteCommand(INDEX_FIRST_STUDENT, new Note("Review lecture notes"));

        assertEquals(expected, parser.parseCommand("addnote id/1 nt/Review lecture notes"));
        assertEquals(expected, parser.parseCommand("ADDNOTE id/1 nt/Review lecture notes"));
        assertEquals(expected, parser.parseCommand("  AddNote\tid/1\tnt/Review lecture notes  "));
    }

    @Test
    public void parseCommand_deleteNoteIgnoringCase_returnsCommand() throws Exception {
        DeleteNoteCommand expected = new DeleteNoteCommand(INDEX_FIRST_STUDENT, Index.fromOneBased(3));

        assertEquals(expected, parser.parseCommand("delnote id/1 ni/3"));
        assertEquals(expected, parser.parseCommand("DELNOTE id/1 ni/3"));
        assertEquals(expected, parser.parseCommand("  DelNote\tid/1\tni/3  "));
    }

    @Test
    public void parseCommand_editNote() throws ParseException {
        EditNoteCommand command = (EditNoteCommand) parser.parseCommand(EditNoteCommand.COMMAND_WORD + " id/"
                + INDEX_FIRST_STUDENT.getOneBased() + " ni/1 nt/hello");
        assertEquals(new EditNoteCommand(INDEX_FIRST_STUDENT, Index.fromOneBased(1), new Note("hello")), command);
    }

}
