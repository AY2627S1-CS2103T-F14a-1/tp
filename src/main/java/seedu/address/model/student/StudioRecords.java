package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.model.student.StudioRecord.MESSAGE_CONSTRAINTS;
import static seedu.address.model.student.StudioRecord.isValidWeek;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents an immutable collection of a student's weekly studio attendance.
 * Each recorded week is between {@value StudioRecord#MIN_WEEK} and {@value StudioRecord#MAX_WEEK}, inclusive.
 */
public final class StudioRecords {

    public static final String STUDIORECORD_MARKER = "StudioRecords: ";

    private final Map<Integer, StudioRecord> studioRecords;

    /**
     * Creates attendance with no attended weeks.
     */
    public StudioRecords() {
        this.studioRecords = Collections.emptyMap();
    }

    /**
     * Creates studio records from the given studio records.
     *
     * @param studioRecords Weeks for which attendance has been recorded.
     * @throws NullPointerException If the set or any of its elements is {@code null}.
     */
    public StudioRecords(Map<Integer, StudioRecord> studioRecords) {
        requireNonNull(studioRecords);

        Map<Integer, StudioRecord> copiedStudioRecords = new HashMap<>(studioRecords);
        for (Integer week : copiedStudioRecords.keySet()) {
            requireNonNull(week);
            checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        }
        this.studioRecords = Collections.unmodifiableMap(copiedStudioRecords);
    }

    /**
     * Returns whether studio status has been recorded for the given week.
     *
     * @param week Studio week to check.
     * @return True if studio status is recorded for the week.
     */
    public boolean hasAttendance(int week) {
        checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        return studioRecords.containsKey(week);
    }

    /**
     * Returns studio records including the given week without changing
     * this object. Returns this object if the week is already recorded.
     *
     * @param week Tutorial week to record.
     * @return StudioRecords containing the existing weeks and given week.
     */
    public StudioRecords withAttendedWeek(int week, StudioRecord studioRecord) {
        requireNonNull(studioRecord);
        if (hasAttendance(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(studioRecords);
        updatedRecords.put(week, studioRecord);

        return new StudioRecords(updatedRecords);
    }

    /**
     * Returns attendance not including the given week without changing
     * this object. Returns this object if the week is already not recorded.
     *
     * @param week Tutorial week to delete.
     * @return StudioRecords containing the existing weeks without given week.
     */
    public StudioRecords withoutAttendedWeek(int week, StudioRecord studioRecord) {
        if (!hasAttendance(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(studioRecords);
        updatedRecords.remove(week);

        return new StudioRecords(updatedRecords);
    }

    /**
     * Returns an unmodifiable set of recorded tutorial weeks.
     *
     * @return Recorded weeks, with no guaranteed iteration order.
     */
    public Map<Integer, StudioRecord> getStudioRecords() {
        return studioRecords;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudioRecords otherStudioRecords)) {
            return false;
        }

        return studioRecords.equals(otherStudioRecords.studioRecords);
    }

    /**
     * Returns the studio records in ascending week order for diagnostic output.
     *
     * @return A string containing the recorded studio records in ascending week order.
     */
    @Override
    public String toString() {
        return new TreeMap<>(studioRecords).toString();
    }
}
