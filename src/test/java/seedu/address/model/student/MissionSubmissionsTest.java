package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests mission submission validation, updates, and immutability.
 */
public class MissionSubmissionsTest {

    @Test
    public void constructor_noArguments_createsEmptySubmissions() {
        MissionSubmissions submissions = new MissionSubmissions();

        assertTrue(submissions.getSubmittedWeeks().isEmpty());
        assertFalse(submissions.hasSubmission(3));
    }

    @Test
    public void constructor_boundaryWeeks_acceptsWeeks() {
        MissionSubmissions submissions = new MissionSubmissions(Set.of(3, 13));

        assertTrue(submissions.hasSubmission(3));
        assertTrue(submissions.hasSubmission(13));
    }

    @Test
    public void constructor_invalidWeeks_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new MissionSubmissions(Set.of(0)));
        assertThrows(IllegalArgumentException.class, () ->
                new MissionSubmissions(Set.of(14)));
        assertThrows(IllegalArgumentException.class, () ->
                new MissionSubmissions(Set.of(-1)));
    }

    @Test
    public void constructor_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new MissionSubmissions(null));

        Set<Integer> weeks = new HashSet<>();
        weeks.add(null);
        assertThrows(NullPointerException.class, () ->
                new MissionSubmissions(weeks));
    }

    @Test
    public void constructor_inputSetModified_preservesStoredWeeks() {
        Set<Integer> weeks = new HashSet<>(Set.of(3));
        MissionSubmissions submissions = new MissionSubmissions(weeks);

        weeks.add(5);

        assertEquals(Set.of(3), submissions.getSubmittedWeeks());
    }

    @Test
    public void withSubmission_newWeek_preservesOriginal() {
        MissionSubmissions original = new MissionSubmissions(Set.of(5, 4));

        MissionSubmissions updated = original.withSubmission(3);

        assertEquals(Set.of(5, 4), original.getSubmittedWeeks());
        assertEquals(Set.of(5, 3, 4), updated.getSubmittedWeeks());
    }

    @Test
    public void withSubmission_existingWeek_returnsUnchangedValue() {
        MissionSubmissions original = new MissionSubmissions(Set.of(3));

        MissionSubmissions updated = original.withSubmission(3);

        assertEquals(original, updated);
        assertEquals(Set.of(3), updated.getSubmittedWeeks());
    }

    @Test
    public void withSubmission_invalidWeek_preservesOriginal() {
        MissionSubmissions original = new MissionSubmissions(Set.of(3));

        assertThrows(IllegalArgumentException.class, () ->
                original.withSubmission(2));
        assertThrows(IllegalArgumentException.class, () ->
                original.withSubmission(14));

        assertEquals(Set.of(3), original.getSubmittedWeeks());
    }

    @Test
    public void hasSubmission_invalidWeek_throwsIllegalArgumentException() {
        MissionSubmissions submissions = new MissionSubmissions();

        assertThrows(IllegalArgumentException.class, () ->
                submissions.hasSubmission(2));
        assertThrows(IllegalArgumentException.class, () ->
                submissions.hasSubmission(14));
    }

    @Test
    public void getSubmittedWeeks_modifySet_throwsUnsupportedOperationException() {
        MissionSubmissions submissions = new MissionSubmissions(Set.of(3));

        assertThrows(UnsupportedOperationException.class, () ->
                submissions.getSubmittedWeeks().add(5));

        assertEquals(Set.of(3), submissions.getSubmittedWeeks());
    }

    @Test
    public void equals_matchingWeeks_returnsTrue() {
        MissionSubmissions first = new MissionSubmissions(Set.of(3, 5));
        MissionSubmissions second = new MissionSubmissions(Set.of(5, 3));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new MissionSubmissions(Set.of(3)));
        assertNotEquals(first, null);
        assertNotEquals(first, Set.of(3, 5));
    }

    @Test
    public void withoutSubmission_existingWeek_preservesOriginalAndOtherWeeks() {
        MissionSubmissions original = new MissionSubmissions(Set.of(4, 3, 13));

        assertEquals(Set.of(4, 13), original.withoutSubmission(3).getSubmittedWeeks());
        assertEquals(Set.of(4, 3, 13), original.getSubmittedWeeks());
        assertTrue(original.withoutSubmission(4).withoutSubmission(3).withoutSubmission(13)
                .getSubmittedWeeks().isEmpty());
    }

    @Test
    public void withoutSubmission_missingWeek_preservesSubmissions() {
        MissionSubmissions original = new MissionSubmissions(Set.of(3));

        assertEquals(original, original.withoutSubmission(5));
        assertEquals(new MissionSubmissions(), new MissionSubmissions().withoutSubmission(4));
    }

    @Test
    public void withoutSubmission_invalidWeek_preservesOriginal() {
        MissionSubmissions original = new MissionSubmissions(Set.of(3));

        for (int week : new int[] {-1, 0, 1, 2, 14, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> original.withoutSubmission(week));
            assertEquals(Set.of(3), original.getSubmittedWeeks());
        }
    }

    @Test
    public void isValidWeek_newRange_acceptsEveryWeekAndRejectsOutsideRange() {
        for (int week = 3; week <= 13; week++) {
            assertTrue(MissionSubmissions.isValidWeek(week));
            MissionSubmissions submissions = new MissionSubmissions().withSubmission(week);
            assertTrue(submissions.hasSubmission(week));
            assertTrue(submissions.withoutSubmission(week).getSubmittedWeeks().isEmpty());
        }
        for (int week : new int[] {Integer.MIN_VALUE, 0, 1, 2, 14, Integer.MAX_VALUE}) {
            assertFalse(MissionSubmissions.isValidWeek(week));
            assertThrows(IllegalArgumentException.class, () -> new MissionSubmissions(Set.of(week)));
        }
    }
}
