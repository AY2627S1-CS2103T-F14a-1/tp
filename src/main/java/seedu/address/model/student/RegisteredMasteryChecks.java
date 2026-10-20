package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import seedu.address.model.masterycheck.MasteryCheck;

/**
 * Represents an immutable collection of a student's mastery check appointments.
 */
public final class RegisteredMasteryChecks {

    public static final String MESSAGE_CONSTRAINTS = "Mastery check number should be an integer greater than 0";

    private final Map<Integer, MasteryCheck> masteryAppointments;

    /**
     * Creates an empty collection of mastery check appointments.
     */
    public RegisteredMasteryChecks() {
        masteryAppointments = Collections.emptyMap();
    }

    /**
     * Copies mastery checks from a given set.
     *
     * @param givenAppointments Mission IDs with recorded submissions.
     * @throws NullPointerException If the set or any of its elements is null.
     */
    public RegisteredMasteryChecks(Map<Integer, MasteryCheck> givenAppointments) {
        requireNonNull(givenAppointments);

        Map<Integer, MasteryCheck> copiedAppointments = new HashMap<>(givenAppointments);
        for (Integer number : copiedAppointments.keySet()) {
            requireNonNull(number);
            checkArgument(isValidMasteryCheckNumber(number), MESSAGE_CONSTRAINTS);
            requireNonNull(copiedAppointments.get(number));
        }
        this.masteryAppointments = Collections.unmodifiableMap(copiedAppointments);
    }

    /**
     * Returns whether a Mastery check has been recorded for the given number.
     *
     * @param masteryCheckNumber Mastery check number to check.
     * @return True if a mastery check is recorded for the number.
     */
    public boolean hasMasteryCheck(int masteryCheckNumber) {
        checkArgument(isValidMasteryCheckNumber(masteryCheckNumber), MESSAGE_CONSTRAINTS);
        return masteryAppointments.containsKey(masteryCheckNumber);
    }

    /**
     * Returns mission submissions including the given mission ID without changing
     * this object. Returns this object if the mission ID is already recorded.
     *
     * @param masteryCheckNumber Mastery check number to record.
     * @return Mission submissions containing the existing mission IDs and given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public RegisteredMasteryChecks withMasteryCheck(int masteryCheckNumber, MasteryCheck check) {
        if (hasMasteryCheck(masteryCheckNumber)) {
            return this;
        }

        Map<Integer, MasteryCheck> updatedAppointments = new HashMap<>(masteryAppointments);
        updatedAppointments.put(masteryCheckNumber, check);

        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns submissions excluding the given mission ID without modifying this object.
     * Returns this object if the mission ID is not recorded.
     *
     * @param masteryCheckNumber Mastery check number to remove.
     * @return Submissions containing all existing mission IDs except the given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public RegisteredMasteryChecks withoutSubmission(int masteryCheckNumber) {
        if (!hasMasteryCheck(masteryCheckNumber)) {
            return this;
        }

        Map<Integer, MasteryCheck> updatedAppointments = new HashMap<>(masteryAppointments);
        updatedAppointments.remove(masteryCheckNumber);

        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns an unmodifiable set of recorded mission IDs.
     *
     * @return Recorded mission IDs, with no guaranteed iteration order.
     */
    public Map<Integer, MasteryCheck> getMasteryCheckAppointments() {
        return masteryAppointments;
    }

    public static boolean isValidMasteryCheckNumber(int num) {
        return num > 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RegisteredMasteryChecks otherAppointments)) {
            return false;
        }

        return masteryAppointments.equals(otherAppointments.masteryAppointments);
    }

    @Override
    public int hashCode() {
        return masteryAppointments.hashCode();
    }

    /**
     * Returns the submitted mission IDs in ascending order for diagnostic output.
     *
     * @return A string containing the recorded mission IDs in ascending order.
     */
    @Override
    public String toString() {
        return masteryAppointments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ". " + entry.getValue().getAppointmentTime()
                        + " " + entry.getValue().isComplete())
                .collect(java.util.stream.Collectors.joining(", "));
    }

}
