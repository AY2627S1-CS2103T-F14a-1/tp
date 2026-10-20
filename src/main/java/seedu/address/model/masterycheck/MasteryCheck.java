package seedu.address.model.masterycheck;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a numbered mastery check task for a student with a fixed appointment time.
 */
public class MasteryCheck {
    private final int id;
    private final LocalDateTime appointmentTime;
    private boolean isComplete;

    public MasteryCheck(int id, LocalDateTime appointmentTime) {
        requireNonNull(appointmentTime);

        this.id = id;
        this.appointmentTime = appointmentTime;
        isComplete = false;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the mastery check appointment time.
     *
     * @return Appointment date and time.
     */
    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof MasteryCheck otherMasteryCheck
                && id == otherMasteryCheck.id
                && appointmentTime.isEqual(otherMasteryCheck.appointmentTime)
                && isComplete == otherMasteryCheck.isComplete;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, appointmentTime, isComplete);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", id)
                .add("appointment time", appointmentTime)
                .add("is complete", isComplete)
                .toString();
    }
}
