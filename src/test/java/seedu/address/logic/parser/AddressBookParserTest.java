package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.DeleteStudioRecordCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.NameContainsKeywordsPredicate;
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
                DeleteStudentCommand.COMMAND_WORD + " " + INDEX_FIRST_STUDENT.getOneBased());
        assertEquals(new DeleteStudentCommand(INDEX_FIRST_STUDENT), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Student student = new StudentBuilder().build();
        EditStudentDescriptor descriptor = new EditStudentDescriptorBuilder(student).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_STUDENT.getOneBased() + " " + StudentUtil.getEditStudentDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_STUDENT, descriptor), command);
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
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(ViewCommand.COMMAND_WORD + " "
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
    public void parseCommand_markMissionIgnoringCase_returnsCommand() throws Exception {
        MarkMissionCommand expected = new MarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"));

        assertEquals(expected, parser.parseCommand("markmission 1 m/M03"));
        assertEquals(expected, parser.parseCommand("MARKMISSION 1 m/M03"));
        assertEquals(expected, parser.parseCommand("MarkMission 1 m/M03"));
        assertEquals(expected, parser.parseCommand("  markmission\t1\tm/M03  "));
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

        assertEquals(expected, parser.parseCommand("unmarkmission 1 m/M03"));
        assertEquals(expected, parser.parseCommand("UNMARKMISSION 1 m/M03"));
        assertEquals(expected, parser.parseCommand("UnmarkMission 1 m/M03"));
        assertEquals(expected, parser.parseCommand("  unmarkmission\t1\tm/M03  "));
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

        assertEquals(expected, parser.parseCommand("addstudiorecord 1 w/3 ef/0"));
        assertEquals(expected, parser.parseCommand("ADDSTUDIORECORD 1 w/3 ef/0"));
        assertEquals(expected, parser.parseCommand("AddStudioRecord 1 W/3 EF/0"));
        assertEquals(expected, parser.parseCommand("  addstudiorecord\t1\tw/3 ef/0  "));
    }

    @Test
    public void parseCommand_addStudioRecordMissingIndex_reportsMissingIndex() {
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_INDEX, () ->
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

        assertEquals(expected, parser.parseCommand("delstudiorecord 1 w/3"));
        assertEquals(expected, parser.parseCommand("DELSTUDIORECORD 1 w/3"));
        assertEquals(expected, parser.parseCommand("DelStudioRecord 1 W/3"));
        assertEquals(expected, parser.parseCommand("  delstudiorecord\t1\tw/3  "));
    }

    @Test
    public void parseCommand_deleteStudioRecordMissingIndex_reportsMissingIndex() {
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_INDEX, () ->
                parser.parseCommand("delstudiorecord"));
    }

    @Test
    public void parseCommand_editStudioRecordIgnoringCase_returnsCommand() throws Exception {
        EditStudioRecordCommand expected = new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, 3, new StudioRecord(50));

        assertEquals(expected, parser.parseCommand("editstudiorecord 1 w/3 ef/50"));
        assertEquals(expected, parser.parseCommand("EDITSTUDIORECORD 1 w/3 ef/50"));
        assertEquals(expected, parser.parseCommand("EditStudioRecord 1 W/3 EF/50"));
        assertEquals(expected, parser.parseCommand("  editstudiorecord\t1\tw/3 ef/50  "));
    }

    @Test
    public void parseCommand_editStudioRecordMissingIndex_reportsMissingIndex() {
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_INDEX, () ->
                parser.parseCommand("editstudiorecord"));
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
        assertEquals(expected, parser.parseCommand("addmc 1 num/1 dt/18-08|14:30"));
        assertEquals(expected, parser.parseCommand("ADDMC 1 num/1 dt/18-08|14:30"));
    }

    @Test
    public void parseCommand_masteryCheckInvalidDate_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                parser.parseCommand("addmc 1 num/1 dt/31-04|14:30"));
    }

}
