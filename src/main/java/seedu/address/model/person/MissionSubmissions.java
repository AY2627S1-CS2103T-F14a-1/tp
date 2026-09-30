package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents an immutable collection of a student's mission submission weeks.
 * Each recorded week is between 1 and 10, inclusive.
 */
public final class MissionSubmissions {

    public static final int MIN_WEEK = 1;
    public static final int MAX_WEEK = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "Tutorial week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive.";

    private final Set<Integer> submittedWeeks;

    /**
     * Creates an empty collection of mission submissions.
     */
    public MissionSubmissions() {
        this.submittedWeeks = Collections.emptySet();
    }

    /**
     * Creates mission submissions from the given tutorial weeks.
     *
     * @param submissionWeeks Weeks for which submissions have been recorded.
     * @throws NullPointerException If the set or any of its elements is null.
     * @throws IllegalArgumentException If any week is outside the valid range.
     */
    public MissionSubmissions(Set<Integer> submissionWeeks) {
        requireNonNull(submissionWeeks);

        Set<Integer> copiedWeeks = new HashSet<>(submissionWeeks);
        for (Integer week : copiedWeeks) {
            requireNonNull(week);
            checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        }
        submittedWeeks = Collections.unmodifiableSet(copiedWeeks);
    }

    /**
     * Returns whether the given tutorial week is within the valid range.
     *
     * @param week Tutorial week to check.
     * @return True if the week is between 1 and 10, inclusive.
     */
    public static boolean isValidWeek(int week) {
        return week >= MIN_WEEK && week <= MAX_WEEK;
    }

    /**
     * Returns whether a submission has been recorded for the given week.
     *
     * @param week Tutorial week to check.
     * @return True if a submission is recorded for the week.
     * @throws IllegalArgumentException If the week is outside the valid range.
     */
    public boolean hasSubmission(int week) {
        checkArgument(isValidWeek(week), MESSAGE_CONSTRAINTS);
        return submittedWeeks.contains(week);
    }

    /**
     * Returns mission submissions including the given week without changing
     * this object. Returns this object if the week is already recorded.
     *
     * @param week Tutorial week to record.
     * @return Mission submissions containing the existing weeks and given week.
     * @throws IllegalArgumentException If the week is outside the valid range.
     */
    public MissionSubmissions withSubmission(int week) {
        if (hasSubmission(week)) {
            return this;
        }

        Set<Integer> updatedWeeks = new HashSet<>(submittedWeeks);
        updatedWeeks.add(week);
        return new MissionSubmissions(updatedWeeks);
    }

    /**
     * Returns an unmodifiable set of recorded tutorial weeks.
     *
     * @return Recorded weeks, with no guaranteed iteration order.
     */
    public Set<Integer> getSubmittedWeeks() {
        return submittedWeeks;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MissionSubmissions otherSubmissions)) {
            return false;
        }

        return submittedWeeks.equals(otherSubmissions.submittedWeeks);
    }

    @Override
    public int hashCode() {
        return submittedWeeks.hashCode();
    }

    /**
     * Returns the submitted weeks in ascending order for diagnostic output.
     *
     * @return A string containing the recorded weeks in ascending order.
     */
    @Override
    public String toString() {
        return new TreeSet<>(submittedWeeks).toString();
    }

}
