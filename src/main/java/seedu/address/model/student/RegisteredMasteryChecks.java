package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import seedu.address.model.masterycheck.MasteryCheck;

/**
 * Represents an immutable collection of a student's mastery check appointments.
 */
public final class RegisteredMasteryChecks {

    private final Set<MasteryCheck> masteryAppointments;

    /**
     * Creates an empty collection of mastery check appointments.
     */
    public RegisteredMasteryChecks() {
        masteryAppointments = Collections.emptySet();
    }

    /**
     * Copies mastery checks from a given set.
     *
     * @param givenAppointments Mission IDs with recorded submissions.
     * @throws NullPointerException If the set or any of its elements is null.
     */
    public RegisteredMasteryChecks(Set<MasteryCheck> givenAppointments) {
        requireNonNull(givenAppointments);

        Set<MasteryCheck> copiedIds = new HashSet<>(givenAppointments);
        for (MasteryCheck masteryCheck : copiedIds) {
            requireNonNull(masteryCheck);
        }
        this.masteryAppointments = Collections.unmodifiableSet(copiedIds);
    }

    /**
     * Returns whether a Mastery check has been recorded for the given ID.
     *
     * @param masteryCheckId Mastery check ID to check.
     * @return True if a mastery check is recorded for the ID.
     */
    public boolean hasMasteryCheck(int masteryCheckId) {
        return masteryAppointments.stream().anyMatch(check -> check.getId() == masteryCheckId);
    }

    /**
     * Returns mission submissions including the given mission ID without changing
     * this object. Returns this object if the mission ID is already recorded.
     *
     * @param masteryCheckId Mission ID to record.
     * @return Mission submissions containing the existing mission IDs and given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public RegisteredMasteryChecks withMasteryCheck(int masteryCheckId, MasteryCheck check) {
        if (hasMasteryCheck(masteryCheckId)) {
            return this;
        }

        Set<MasteryCheck> updatedAppointments = new HashSet<>(masteryAppointments);
        updatedAppointments.add(check);
        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns submissions excluding the given mission ID without modifying this object.
     * Returns this object if the mission ID is not recorded.
     *
     * @param masteryCheckId Mission ID to remove.
     * @return Submissions containing all existing mission IDs except the given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public RegisteredMasteryChecks withoutSubmission(int masteryCheckId) {
        Set<MasteryCheck> updatedAppointments = new HashSet<>(masteryAppointments);
        updatedAppointments.removeIf(check -> check.getId() == masteryCheckId);
        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns an unmodifiable set of recorded mission IDs.
     *
     * @return Recorded mission IDs, with no guaranteed iteration order.
     */
    public Set<MasteryCheck> getMasteryCheckAppointments() {
        return masteryAppointments;
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
        return masteryAppointments.stream().sorted(Comparator.comparingInt(MasteryCheck::getId))
                .map(MasteryCheck::toString).toList().toString();
    }

}
