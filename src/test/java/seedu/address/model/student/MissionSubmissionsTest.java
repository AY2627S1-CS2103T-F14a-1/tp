package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.mission.MissionId;

/** Tests immutable submission records identified by mission ID. */
public class MissionSubmissionsTest {

    private static final MissionId FIRST = new MissionId("M01");
    private static final MissionId SECOND = new MissionId("M105");

    @Test
    public void constructor_mutableInput_defensivelyCopiesRecords() {
        Set<MissionId> ids = new HashSet<>(Set.of(FIRST));
        MissionSubmissions submissions = new MissionSubmissions(ids);
        ids.clear();
        assertTrue(submissions.hasSubmission(FIRST));
        assertThrows(UnsupportedOperationException.class, () -> submissions.getSubmittedMissionIds().clear());
    }

    @Test
    public void submissionOperations_validIds_preserveOriginalAndOtherRecords() {
        MissionSubmissions empty = new MissionSubmissions();
        MissionSubmissions first = empty.withSubmission(FIRST);
        MissionSubmissions both = first.withSubmission(SECOND);
        assertFalse(empty.hasSubmission(FIRST));
        assertSame(first, first.withSubmission(FIRST));
        assertSame(first, first.withoutSubmission(SECOND));
        assertEquals(Set.of(SECOND), both.withoutSubmission(FIRST).getSubmittedMissionIds());
        assertEquals(Set.of(FIRST, SECOND), both.getSubmittedMissionIds());
        assertEquals("[M01, M105]", both.toString());
        assertEquals(first, new MissionSubmissions(Set.of(FIRST)));
        assertEquals(first.hashCode(), new MissionSubmissions(Set.of(FIRST)).hashCode());
    }

    @Test
    public void submissionOperations_nullIds_rejectsInput() {
        MissionSubmissions submissions = new MissionSubmissions();
        assertThrows(NullPointerException.class, () -> new MissionSubmissions(null));
        Set<MissionId> ids = new HashSet<>();
        ids.add(null);
        assertThrows(NullPointerException.class, () -> new MissionSubmissions(ids));
        assertThrows(NullPointerException.class, () -> submissions.hasSubmission(null));
        assertThrows(NullPointerException.class, () -> submissions.withSubmission(null));
        assertThrows(NullPointerException.class, () -> submissions.withoutSubmission(null));
    }
}
