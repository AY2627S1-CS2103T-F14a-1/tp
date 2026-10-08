package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an immutable collection of a student's weekly tutorial attendance.
 * Each recorded week is between 1 and 10, inclusive.
 */
public class Attendance {

    public static final int MIN_WEEK = 1;
    public static final int MAX_WEEK = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "Tutorial week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive.";

    private final Set<Integer> attendedWeeks;

    public Attendance() {
        this.attendedWeeks = Collections.emptySet();
    }

    public Attendance(Set<Integer> attendedWeeks) {
        requireNonNull(attendedWeeks);

        Set<Integer> copiedWeeks = new HashSet<>(attendedWeeks);
    }

    public static boolean isValidWeek(int week) {
        return week >= MIN_WEEK && week <= MAX_WEEK;
    }

    public boolean hasAttendedWeek(int week) {
        return attendedWeeks.contains(week);
    }

    public Attendance withAttendedWeek(int week) {
        if (hasAttendedWeek(week)) {
            return this;
        }

        Set<Integer> updatedWeeks = new HashSet<>(attendedWeeks);
        updatedWeeks.add(week);
        return new Attendance(updatedWeeks);
    }
}
