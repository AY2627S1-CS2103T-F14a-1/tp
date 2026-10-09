package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents an immutable collection of a student's weekly tutorial attendance.
 * Each recorded week is between 1 and 10, inclusive.
 */
public class Attendance {

    public static final int MIN_WEEK = 3;
    public static final int MAX_WEEK = 13;
    public static final String MESSAGE_CONSTRAINTS =
            "Tutorial week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive.";

    private final Set<Integer> attendedWeeks;

    public Attendance() {
        this.attendedWeeks = Collections.emptySet();
    }

    /**
     * Creates attendance from the given tutorial weeks.
     *
     * @param attendedWeeks Weeks for which submissions have been recorded.
     */
    public Attendance(Set<Integer> attendedWeeks) {
        requireNonNull(attendedWeeks);

        Set<Integer> copiedWeeks = new HashSet<>(attendedWeeks);
        for (Integer week : copiedWeeks) {
            requireNonNull(week);
            checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        }
        this.attendedWeeks = copiedWeeks;
    }

    public static boolean isValidWeek(int week) {
        return week >= MIN_WEEK && week <= MAX_WEEK;
    }

    public boolean hasAttendence(int week) {
        return attendedWeeks.contains(week);
    }

    /**
     * Returns mission submissions including the given week without changing
     * this object. Returns this object if the week is already recorded.
     *
     * @param week Tutorial week to record.
     * @return Attendance containing the existing weeks and given week.
     */
    public Attendance withAttendedWeek(int week) {
        if (hasAttendence(week)) {
            return this;
        }

        Set<Integer> updatedWeeks = new HashSet<>(attendedWeeks);
        updatedWeeks.add(week);
        return new Attendance(updatedWeeks);
    }

    public Set<Integer> getAttendedWeeks() {
        return attendedWeeks;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Attendance otherAttendance)) {
            return false;
        }

        return attendedWeeks.equals(otherAttendance.attendedWeeks);
    }

    @Override
    public int hashCode() {
        return attendedWeeks.hashCode();
    }

    /**
     * Returns the submitted weeks in ascending order for diagnostic output.
     *
     * @return A string containing the recorded weeks in ascending order.
     */
    @Override
    public String toString() {
        return new TreeSet<>(attendedWeeks).toString();
    }
}
