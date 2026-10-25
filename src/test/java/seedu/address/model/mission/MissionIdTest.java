package seedu.address.model.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests canonical mission IDs and their numeric boundaries independently of parsing. */
public class MissionIdTest {

    @Test
    public void constructor_validIds_preservesTextAndNumericValue() {
        for (String text : List.of("M01", "M09", "M10", "M99", "M100", "M105", "M9223372036854775807")) {
            MissionId id = new MissionId(text);
            assertEquals(text, id.toString());
            assertEquals(Long.parseLong(text.substring(1)), id.getNumber());
        }
    }

    @Test
    public void constructor_invalidIds_reportsConstraint() {
        for (String text : List.of("", " ", "M", "01", "M0", "M00", "M1", "M001", "M010",
                " M01", "M01 ", "M 01", "M+01", "M-01", "M1.0", "M1e2", "M０１",
                "M9223372036854775808", "M999999999999999999999999999")) {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> new MissionId(text));
            assertEquals(MissionId.MESSAGE_CONSTRAINTS, error.getMessage(), text);
        }
        assertThrows(NullPointerException.class, () -> new MissionId(null));
    }

    @Test
    public void constructor_lowercaseId_normalizesToUppercase() {
        MissionId missionId = new MissionId("m03");

        assertEquals("M03", missionId.toString());
        assertEquals(new MissionId("M03"), missionId);
    }

    @Test
    public void equals_sameAndDifferentValues_obeysValueContract() {
        MissionId first = new MissionId("M01");
        MissionId same = new MissionId("M01");
        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertTrue(same.equals(first));
        assertEquals(first.hashCode(), same.hashCode());
        assertFalse(first.equals(new MissionId("M02")));
        assertFalse(first.equals(null));
        assertFalse(first.equals("M01"));
    }
}
