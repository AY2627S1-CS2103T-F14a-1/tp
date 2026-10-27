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
public final class RegisteredStudioRecords {

    public static final int MIN_WEEK = 0;
    public static final int MAX_WEEK = 13;

    public static final String MESSAGE_CONSTRAINTS =
            "Studio week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive";

    private final Map<Integer, StudioRecord> recordsByWeek;

    /**
     * Creates {@code RegisteredStudioRecords} with no recorded weeks.
     */
    public RegisteredStudioRecords() {
        this.recordsByWeek = Collections.emptyMap();
    }

    /**
     * Creates studio records from the given studio records.
     *
     * @param recordsByWeek Weeks for which studio status has been recorded.
     * @throws NullPointerException If the map, any week, or any studio record is {@code null}.
     */
    public RegisteredStudioRecords(Map<Integer, StudioRecord> recordsByWeek) {
        requireNonNull(recordsByWeek);

        Map<Integer, StudioRecord> copiedStudioRecords = new HashMap<>(recordsByWeek);
        for (Integer week : copiedStudioRecords.keySet()) {
            requireNonNull(week);
            checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
            requireNonNull(copiedStudioRecords.get(week));
        }
        this.recordsByWeek = Collections.unmodifiableMap(copiedStudioRecords);
    }

    /**
     * Returns whether studio status has been recorded for the given week.
     *
     * @param week Studio week to check.
     * @return True if studio status is recorded for the week.
     */
    public boolean hasStudioRecord(int week) {
        checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        return recordsByWeek.containsKey(week);
    }

    /**
     * Returns whether the same studio status has been recorded for the given week.
     *
     * @param week Studio week to check.
     * @param studioRecord Studio record to check.
     * @return True if the same studio status has been recorded for the given week.
     * @throws IllegalArgumentException If {@code week} is outside the valid studio-week range.
     * @throws NullPointerException If {@code studioRecord} is {@code null}.
     */
    public boolean hasMatchingStudioRecord(int week, StudioRecord studioRecord) {
        requireNonNull(studioRecord);
        checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);

        return recordsByWeek.containsKey(week) && recordsByWeek.get(week).equals(studioRecord);
    }

    /**
     * Returns studio records including the given week without changing
     * this object. Returns this object if the week is already recorded.
     *
     * @param week Studio week to record.
     * @param studioRecord Studio record to include.
     * @return RegisteredStudioRecords containing the existing weeks and given week.
     */
    public RegisteredStudioRecords withStudioRecord(int week, StudioRecord studioRecord) {
        requireNonNull(studioRecord);
        if (hasStudioRecord(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(recordsByWeek);
        updatedRecords.put(week, studioRecord);

        return new RegisteredStudioRecords(updatedRecords);
    }

    /**
     * Returns studio records not including the given week without changing
     * this object. Returns this object if the week is already not recorded.
     *
     * @param week Studio week to delete.
     * @return RegisteredStudioRecords containing the existing weeks without given week.
     */
    public RegisteredStudioRecords withoutStudioRecord(int week) {
        if (!hasStudioRecord(week)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(recordsByWeek);
        updatedRecords.remove(week);

        return new RegisteredStudioRecords(updatedRecords);
    }

    /**
     * Returns studio records including the new record
     * for the given week, if the week is already recorded.
     *
     * @param week Studio week to edit
     * @param studioRecord Studio record to include.
     * @return RegisteredStudioRecords containing the existing weeks and updated record for given week.
     */
    public RegisteredStudioRecords editStudioRecord(int week, StudioRecord studioRecord) {
        requireNonNull(studioRecord);
        if (!hasStudioRecord(week)) {
            return this;
        }

        if (hasMatchingStudioRecord(week, studioRecord)) {
            return this;
        }

        Map<Integer, StudioRecord> updatedRecords = new HashMap<>(recordsByWeek);
        updatedRecords.put(week, studioRecord);

        return new RegisteredStudioRecords(updatedRecords);
    }

    /**
     * Returns an unmodifiable map of recorded studio weeks.
     *
     * @return Recorded weeks, with no guaranteed iteration order.
     */
    public Map<Integer, StudioRecord> getRecordsByWeek() {
        return recordsByWeek;
    }

    /**
     * Returns whether the given studio week is within the valid range.
     *
     * @param week Studio week to check.
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
        if (!(other instanceof RegisteredStudioRecords otherRegisteredStudioRecords)) {
            return false;
        }

        return recordsByWeek.equals(otherRegisteredStudioRecords.recordsByWeek);
    }

    @Override
    public int hashCode() {
        return recordsByWeek.hashCode();
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
            StudioRecord studioRecord = recordsByWeek.get(week);
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
