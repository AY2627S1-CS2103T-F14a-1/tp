package seedu.address.model.masterycheck;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a mastery check with a fixed appointment time and mutable completion status.
 * Its registration number is stored separately by the student's {@code RegisteredMasteryChecks}.
 */
public class MasteryCheck {
    private final LocalDateTime appointmentTime;
    private boolean isComplete;

    /**
     * Creates an incomplete mastery check at the specified appointment time.
     *
     * @param appointmentTime Date and time of the appointment.
     * @throws NullPointerException If the appointment time is null.
     */
    public MasteryCheck(LocalDateTime appointmentTime) {
        requireNonNull(appointmentTime);

        this.appointmentTime = appointmentTime;
        isComplete = false;
    }

    /**
     * Returns the mastery check appointment time.
     *
     * @return Appointment date and time.
     */
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * Returns whether this mastery check is complete.
     *
     * @return True if this mastery check has been marked complete.
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Marks this mastery check complete, leaving its appointment time unchanged.
     */
    public void markComplete() {
        isComplete = true;
    }

    /**
     * Marks this mastery check incomplete, leaving its appointment time unchanged.
     */
    public void markIncomplete() {
        isComplete = false;
    }

    /**
     * Compares mastery checks by appointment time and completion status.
     *
     * @param other Object to compare with this object.
     * @return True if the objects have equal values.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof MasteryCheck otherMasteryCheck
                && appointmentTime.isEqual(otherMasteryCheck.appointmentTime)
                && isComplete == otherMasteryCheck.isComplete;
    }

    /**
     * Returns a hash code based on the appointment time and current completion status.
     *
     * @return Hash code derived from the values used for equality.
     */
    @Override
    public int hashCode() {
        return Objects.hash(appointmentTime, isComplete);
    }

    /**
     * Returns the appointment time and completion status for diagnostic output.
     *
     * @return A diagnostic representation of this mastery check.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("appointment time", appointmentTime)
                .add("is complete", isComplete)
                .toString();
    }
}
