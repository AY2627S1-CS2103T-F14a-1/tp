package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import seedu.address.model.masterycheck.MasteryCheck;

/**
 * Stores a student's mastery check appointments by unique positive registration number.
 * The mapping is unmodifiable, but the contained checks retain their mutable completion status.
 */
public final class RegisteredMasteryChecks {

    public static final String MESSAGE_CONSTRAINTS = "Mastery check number should be an integer greater than 0";
    public static final String MESSAGE_MISSING = "No registered mastery checks with that number";

    private final Map<Integer, MasteryCheck> masteryAppointments;

    /**
     * Creates an empty collection of mastery check appointments.
     */
    public RegisteredMasteryChecks() {
        masteryAppointments = Collections.emptyMap();
    }

    /**
     * Copies a number-to-appointment mapping without copying the mastery check objects.
     *
     * @param givenAppointments Appointments keyed by positive mastery check number.
     * @throws NullPointerException If the map, any key, or any appointment is null.
     * @throws IllegalArgumentException If a mastery check number is not positive.
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
     * Returns whether a mastery check is registered for the given number.
     *
     * @param masteryCheckNumber Mastery check number to check.
     * @return True if a mastery check is recorded for the number.
     * @throws IllegalArgumentException If the mastery check number is not positive.
     */
    public boolean hasMasteryCheck(int masteryCheckNumber) {
        checkArgument(isValidMasteryCheckNumber(masteryCheckNumber), MESSAGE_CONSTRAINTS);
        return masteryAppointments.containsKey(masteryCheckNumber);
    }

    /**
     * Returns registrations including the supplied appointment without modifying this mapping.
     * If the number is already registered, returns this object and ignores the supplied check.
     *
     * @param masteryCheckNumber Positive mastery check number to register.
     * @param check Appointment to associate with the number if it is not already registered.
     * @return Registrations containing the appointment, or this object if the number already exists.
     * @throws IllegalArgumentException If the mastery check number is not positive.
     * @throws NullPointerException If the check is null and the number is not already registered.
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
     * Returns registrations with a copy of the numbered check set to the requested completion status.
     * Preserves the original check, appointment time, and all other registrations.
     * Returns this object if the check already has the requested status.
     *
     * @param masteryCheckNumber Positive number identifying a registered mastery check.
     * @param isComplete Whether the mastery check should be complete.
     * @return Updated registrations, or this object if the completion status is unchanged.
     * @throws IllegalArgumentException If the number is not positive or is not registered.
     */
    public RegisteredMasteryChecks withCompletionStatus(int masteryCheckNumber, boolean isComplete) {
        if (hasMasteryCheck(masteryCheckNumber) && isCompleteMasteryCheck(masteryCheckNumber) == isComplete) {
            return this;
        }

        Map<Integer, MasteryCheck> updatedAppointments = new HashMap<>(masteryAppointments);
        MasteryCheck check = getMasteryCheck(masteryCheckNumber);
        if (isComplete) {
            check.markComplete();
        } else {
            check.markIncomplete();
        }
        updatedAppointments.put(masteryCheckNumber, check);

        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns registrations excluding the given number without modifying this mapping.
     * Returns this object if the number is not registered.
     *
     * @param masteryCheckNumber Positive mastery check number to remove.
     * @return Registrations excluding the number, or this object if the number was absent.
     * @throws IllegalArgumentException If the mastery check number is not positive.
     */
    public RegisteredMasteryChecks withoutMasteryCheck(int masteryCheckNumber) {
        if (!hasMasteryCheck(masteryCheckNumber)) {
            return this;
        }

        Map<Integer, MasteryCheck> updatedAppointments = new HashMap<>(masteryAppointments);
        updatedAppointments.remove(masteryCheckNumber);

        return new RegisteredMasteryChecks(updatedAppointments);
    }

    /**
     * Returns the unmodifiable number-to-appointment mapping.
     * The contained mastery checks are shared and their completion statuses remain mutable.
     *
     * @return Registered appointments keyed by number, with no guaranteed iteration order.
     */
    public Map<Integer, MasteryCheck> getMasteryCheckAppointments() {
        return masteryAppointments;
    }

    /**
     * Returns an independent copy of the numbered check with its appointment time and completion status.
     *
     * @param number Positive number identifying a registered mastery check.
     * @return A copy whose completion status can change without modifying the registered check.
     * @throws IllegalArgumentException If the number is not positive or is not registered.
     */
    private MasteryCheck getMasteryCheck(int number) {
        checkArgument(hasMasteryCheck(number), MESSAGE_MISSING);
        MasteryCheck check = masteryAppointments.get(number);
        MasteryCheck clone = new MasteryCheck(check.getAppointmentTime());
        if (check.isComplete()) {
            clone.markComplete();
        }
        return clone;
    }

    /**
     * Checks whether a number can identify a mastery check registration.
     *
     * @param num Number to validate.
     * @return True if the number is greater than zero.
     */
    public static boolean isValidMasteryCheckNumber(int num) {
        return num > 0;
    }

    /**
     * Returns whether the numbered registered mastery check is complete without modifying it.
     *
     * @param number Positive number identifying a registered mastery check.
     * @return True if the registered check is complete, or false if it is incomplete.
     * @throws IllegalArgumentException If the number is not positive or is not registered.
     */
    public boolean isCompleteMasteryCheck(int number) {
        checkArgument(hasMasteryCheck(number), MESSAGE_MISSING);
        return masteryAppointments.get(number).isComplete();
    }

    /**
     * Compares registrations by their numbers and the corresponding mastery check values.
     *
     * @param other Object to compare with this object.
     * @return True if the objects have equal values.
     */
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

    /**
     * Returns a hash code based on the registration mapping and current appointment values.
     *
     * @return Hash code derived from the values used for equality.
     */
    @Override
    public int hashCode() {
        return masteryAppointments.hashCode();
    }

    /**
     * Returns comma-separated appointments in ascending registration-number order.
     * Each entry contains the number followed by a period, appointment time, and completion flag.
     *
     * @return The formatted appointments, or an empty string if none are registered.
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
