package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class StudioRecordTest {

    @Test
    public void constructor_validWeeks_recordsAttendance() {
        for (int i = StudioRecords.MIN_WEEK; i <= StudioRecords.MAX_WEEK; i++) {
            StudioRecords studioRecords = new StudioRecords(Set.of(i));
            assertEquals(Set.of(i), studioRecords.getStudioRecords());
        }

        // multiple valid weeks
        Set<Integer> numbers = IntStream.rangeClosed(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK)
                .boxed()
                .collect(Collectors.toSet());
        assertEquals(numbers, new StudioRecords(numbers).getStudioRecords());
    }

    @Test
    public void constructor_invalidWeeks_throwsIllegalArgumentException() {

        for (int i = -1; i < StudioRecords.MIN_WEEK; i++) {
            int week = i;
            assertThrows(IllegalArgumentException.class, () ->
                    new StudioRecords(Set.of(week)));
        }
        assertThrows(IllegalArgumentException.class, () ->
                new StudioRecords(Set.of(StudioRecords.MAX_WEEK + 1)));
    }
}
