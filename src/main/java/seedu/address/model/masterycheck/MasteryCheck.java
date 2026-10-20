package seedu.address.model.masterycheck;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

/**
 * Represents a numbered mastery check task for a student with a fixed appointment time
 */
public class MasteryCheck {
    private final int id;
    private final LocalDateTime appointmentTime;

    public MasteryCheck(int id, LocalDateTime appointmentTime) {
        requireNonNull(appointmentTime);

        this.id = id;
        this.appointmentTime = appointmentTime;
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

}
