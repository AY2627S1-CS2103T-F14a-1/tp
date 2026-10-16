package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an immutable collection of a student's weekly studio status.
 * Each recorded week is between {@value MIN_WEEK} and {@value MAX_WEEK}, inclusive.
 */
public final class StudioRecords {

    public static final int MIN_WEEK = 3;
    public static final int MAX_WEEK = 13;

    public static final String STUDIORECORD_MARKER = "StudioRecords: ";
    public static final String MESSAGE_CONSTRAINTS =
            "Tutorial week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive";

    private final Map<Integer, StudioRecord> studioRecords;

    /**
     * Creates {@code StudioRecords} with no recorded weeks.
     */
    public StudioRecords() {
        this.studioRecords = Collections.emptyMap();
    }

    /**
     * Creates studio records from the given studio records.
     *
     * @param studioRecords Weeks for which studio status has been recorded.
     * @throws NullPointerException If the map, any week, or any studio record is {@code null}.
     */
    public StudioRecords(Map<Integer, StudioRecord> studioRecords) {
        requireNonNull(studioRecords);

        Map<Integer, StudioRecord> copiedStudioRecords = new HashMap<>(studioRecords);
        for (Integer week : copiedStudioRecords.keySet()) {
            requireNonNull(week);
            checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
            requireNonNull(copiedStudioRecords.get(week));
        }
        this.studioRecords = Collections.unmodifiableMap(copiedStudioRecords);
    }

    /**
     * Returns whether studio status has been recorded for the given week.
     *
     * @param week Studio week to check.
     * @return True if studio status is recorded for the week.
     */
    public boolean hasStudioRecord(int week) {
        checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        return studioRecords.containsKey(week);
    }

    /**
     * Returns studio records including the given week without changing
     * this object. Returns this object if the week is already recorded.
     *
     * @param week Tutorial week to record.
     * @param studioRecord Studio record to include.
     * @return StudioRecords containing the existing weeks and given week.
     */
    public StudioRecords withStudioRecord(int week, StudioRecord studioRecord) {
        requireNonNull(studioRecord);
        if (hasStudioRecord(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(studioRecords);
        updatedRecords.put(week, studioRecord);

        return new StudioRecords(updatedRecords);
    }

    /**
     * Returns studio records not including the given week without changing
     * this object. Returns this object if the week is already not recorded.
     *
     * @param week Tutorial week to delete.
     * @return StudioRecords containing the existing weeks without given week.
     */
    public StudioRecords withoutStudioRecord(int week) {
        if (!hasStudioRecord(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(studioRecords);
        updatedRecords.remove(week);

        return new StudioRecords(updatedRecords);
    }

    /**
     * Returns an unmodifiable map of recorded studio weeks.
     *
     * @return Recorded weeks, with no guaranteed iteration order.
     */
    public Map<Integer, StudioRecord> getRecordsByWeek() {
        return studioRecords;
    }

    /**
     * Returns whether the given tutorial week is within the valid range
     *
     * @param week Tutorial week to check.
     * @return True if the week is between {@value #MIN_WEEK} and {@value #MAX_WEEK}, inclusive.
     */
    public static boolean isValidWeek(int week) {
        return week >= MIN_WEEK && week <= MAX_WEEK;
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

    @Override
    public int hashCode() {
        return studioRecords.hashCode();
    }

    /**
     * Returns the studio records in ascending week order.
     *
     * @return A string containing the recorded studio records in ascending week order.
     */
    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        for (int week = MIN_WEEK; week <= MAX_WEEK; week++) {
            StudioRecord studioRecord = studioRecords.get(week);
            boolean hasAttended = studioRecord != null;
            int effortScore = hasAttended ? studioRecord.getEffortScore() : 0;

            summary.append(week)
                    .append(": ").append(hasAttended ? "A" : "X")
                    .append(" | ").append(effortScore);
            if (week < MAX_WEEK) {
                summary.append(", ");
            }
        }
        return summary.toString();
    }
}
