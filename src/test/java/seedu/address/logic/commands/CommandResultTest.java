package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.BOB;

import java.util.ArrayList;
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
    public void equals_differentWarningsOrMissions_returnsFalse() {
        Mission mission = new Mission(
                new MissionId("M01"),
                "Runes",
                java.time.LocalDate.of(2026, 10, 20));
        CommandResult commandResult = new CommandResult(
                "feedback", false, false, AMY, List.of("Matching note text"), List.of(mission));
        CommandResult matchingResult = new CommandResult(
                "feedback", false, false, AMY, List.of("Matching note text"), List.of(mission));
        CommandResult differentWarnings = new CommandResult(
                "feedback", false, false, AMY, List.of("Other warning"), List.of(mission));
        CommandResult differentMissions = new CommandResult(
                "feedback", false, false, AMY, List.of("Matching note text"), List.of());

        assertEquals(commandResult, matchingResult);
        assertNotEquals(commandResult, differentWarnings);
        assertNotEquals(commandResult, differentMissions);
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
    public void withWarning_validWarning_returnsNewResultWithWarning() {
        CommandResult original = new CommandResult("Note added");

        CommandResult result = original.withWarning("Matching note text");

        assertTrue(original.getWarnings().isEmpty());
        assertEquals(List.of("Matching note text"), result.getWarnings());
        assertTrue(result.getStudentToView().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> result.getWarnings().add("Another warning"));
        assertThrows(NullPointerException.class, () -> original.withWarning(null));
    }

    @Test
    public void missions_constructorStoresMissionCatalogue() {
        Mission mission = new Mission(
                new MissionId("M01"),
                "Runes",
                java.time.LocalDate.of(2026, 10, 20));

        List<Mission> missions = new ArrayList<>(List.of(mission));
        CommandResult result = new CommandResult(
                "Student details",
                AMY,
                missions);
        missions.clear();

        assertEquals(List.of(mission), result.getMissions());
        assertThrows(UnsupportedOperationException.class, () -> result.getMissions().clear());
    }

    @Test
    public void missionUpdatedFlag_isStoredCorrectly() {
        CommandResult updatedResult = new CommandResult(
                "Mission updated",
                null,
                List.of(),
                true);

        CommandResult nonUpdatedResult = new CommandResult(
                "Mission not updated",
                null,
                List.of(),
                false);

        assertTrue(updatedResult.isMissionUpdated());
        assertFalse(nonUpdatedResult.isMissionUpdated());
    }

    @Test
    public void equals_differentMissionUpdatedFlag_returnsFalse() {
        CommandResult updatedResult = new CommandResult(
                "feedback",
                null,
                List.of(),
                true);

        CommandResult nonUpdatedResult = new CommandResult(
                "feedback",
                null,
                List.of(),
                false);

        assertNotEquals(updatedResult, nonUpdatedResult);
    }

    @Test
    public void hashCode_sameMissionUpdatedFlag_returnsSameHashCode() {
        CommandResult first = new CommandResult(
                "feedback",
                null,
                List.of(),
                true);

        CommandResult second = new CommandResult(
                "feedback",
                null,
                List.of(),
                true);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void missionUpdatedConstructor_storesMissions() {
        Mission mission = new Mission(
                new MissionId("M01"),
                "Runes",
                java.time.LocalDate.of(2026, 10, 20));

        CommandResult result = new CommandResult(
                "Mission updated",
                null,
                List.of(mission),
                true);

        assertEquals(List.of(mission), result.getMissions());
        assertTrue(result.isMissionUpdated());
    }

    @Test
    public void withWarning_preservesMissionUpdatedFlag() {
        CommandResult original = new CommandResult(
                "Mission updated",
                null,
                List.of(),
                true);

        CommandResult result =
                original.withWarning("A warning");

        assertTrue(result.isMissionUpdated());
        assertEquals(
                List.of("A warning"),
                result.getWarnings());
    }

    @Test
    public void withWarning_preservesMissionNotUpdatedFlag() {
        CommandResult original = new CommandResult(
                "No mission update",
                null,
                List.of(),
                false);

        CommandResult result =
                original.withWarning("A warning");

        assertFalse(result.isMissionUpdated());
    }

    @Test
    public void missionUpdatedResult_setsMissionUpdatedFlag() {
        CommandResult result = new CommandResult(
                "Mission updated",
                null,
                List.of(),
                true);

        assertTrue(result.isMissionUpdated());
    }

    @Test
    public void normalResult_doesNotSetMissionUpdatedFlag() {
        CommandResult result =
                new CommandResult("No update");

        assertFalse(result.isMissionUpdated());
    }

    @Test
    public void withWarning_addsWarningToResult() {
        CommandResult result = new CommandResult("Command completed")
                .withWarning("This is a warning.");

        assertEquals(
                List.of("This is a warning."),
                result.getWarnings());
    }
}
