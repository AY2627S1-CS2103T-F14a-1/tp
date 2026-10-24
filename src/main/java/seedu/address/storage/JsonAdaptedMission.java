package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/**
 * JSON representation of an assignment with a date-only deadline.
 */
class JsonAdaptedMission {

    private final String id;
    private final String name;
    private final String deadline;

    /**
     * Creates a saved mission for validation during model conversion.
     *
     * @param id Saved identifier.
     * @param name Saved name.
     * @param deadline Saved ISO calendar date.
     */
    @JsonCreator
    public JsonAdaptedMission(@JsonProperty("id") String id,
            @JsonProperty("name") String name, @JsonProperty("deadline") String deadline) {
        this.id = id;
        this.name = name;
        this.deadline = deadline;
    }

    /**
     * Copies an immutable mission into its storage representation.
     *
     * @param mission Mission to save.
     */
    public JsonAdaptedMission(Mission mission) {
        id = mission.getId().toString();
        name = mission.getName();
        deadline = mission.getDeadline().toString();
    }

    /**
     * Validates all saved fields before constructing a mission.
     *
     * @return Validated mission.
     * @throws IllegalValueException If a field is missing or invalid.
     */
    public Mission toModelType() throws IllegalValueException {
        if (id == null || name == null || deadline == null) {
            throw new IllegalValueException("Mission ID, name and deadline are required.");
        }
        try {
            return new Mission(new MissionId(id), name, LocalDate.parse(deadline));
        } catch (DateTimeParseException e) {
            throw new IllegalValueException("Invalid mission deadline: " + deadline);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
