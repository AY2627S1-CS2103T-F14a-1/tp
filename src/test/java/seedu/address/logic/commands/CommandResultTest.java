package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String actual = commandResult.toString();
        assertTrue(actual.startsWith(CommandResult.class.getCanonicalName() + "{"));
        assertTrue(actual.contains("feedbackToUser=feedback"));
        assertTrue(actual.contains("showHelp=false"));
        assertTrue(actual.contains("exit=false"));
    }

    @Test
    public void studentResult_matchingPayload_comparesStudentAndPreservesDefaults() {
        CommandResult result = new CommandResult("details", AMY);
        CommandResult same = new CommandResult("details", AMY);
        assertEquals(AMY, result.getStudentToView().orElseThrow());
        assertEquals(result, same);
        assertEquals(result.hashCode(), same.hashCode());
        assertNotEquals(result, new CommandResult("details"));
        assertNotEquals(result, new CommandResult("details", BOB));
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
        assertTrue(new CommandResult("feedback").getStudentToView().isEmpty());
        assertTrue(new CommandResult("details", null).getStudentToView().isEmpty());
        assertThrows(NullPointerException.class, () -> new CommandResult(null, AMY));
    }

    @Test
    public void missions_constructorStoresMissionCatalogue() {
        Mission mission = new Mission(
                new MissionId("M01"),
                "Runes",
                java.time.LocalDate.of(2026, 10, 20));

        CommandResult result = new CommandResult(
                "Student details",
                AMY,
                List.of(mission));

        assertEquals(List.of(mission), result.getMissions());
    }
}
