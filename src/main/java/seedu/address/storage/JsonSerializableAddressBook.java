package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";

    private final List<JsonAdaptedStudent> students = new ArrayList<>();
    private final List<JsonAdaptedMission> missions;
    private final Object nextMissionNumber;

    /**
     * Reads catalogue fields without coercing malformed counters into integers.
     * Catalogue fields are required to validate mission references.
     *
     * @param students Saved students.
     * @param missions Saved catalogue.
     * @param nextMissionNumber Saved integer allocation counter.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("students") List<JsonAdaptedStudent> students,
            @JsonProperty("missions") List<JsonAdaptedMission> missions,
            @JsonProperty("nextMissionNumber") Object nextMissionNumber) {
        this.students.addAll(students);
        this.missions = missions;
        this.nextMissionNumber = nextMissionNumber;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        students.addAll(source.getStudentList().stream().map(JsonAdaptedStudent::new).collect(Collectors.toList()));
        missions = source.getMissionList().stream().map(JsonAdaptedMission::new).collect(Collectors.toList());
        nextMissionNumber = source.getNextMissionNumber();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        restoreMissions(addressBook);
        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            Student student = jsonAdaptedStudent.toModelType();
            if (addressBook.hasStudent(student)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT);
            }
            for (var id : student.getMissionSubmissions().getSubmittedMissionIds()) {
                if (addressBook.findMission(id).isEmpty()) {
                    throw new IllegalValueException("Submission refers to nonexistent mission: " + id);
                }
            }
            addressBook.addStudent(student);
        }
        return addressBook;
    }

    /**
     * Loads required catalogue state and rejects incompatible or incomplete files.
     *
     * @param addressBook Fresh address book receiving the validated catalogue.
     * @throws IllegalValueException If the catalogue or allocation counter is invalid.
     */
    private void restoreMissions(AddressBook addressBook) throws IllegalValueException {
        if (missions == null || !(nextMissionNumber instanceof Integer || nextMissionNumber instanceof Long)) {
            throw new IllegalValueException("Mission catalogue requires missions and an integer nextMissionNumber.");
        }
        List<Mission> restoredMissions = new ArrayList<>();
        for (JsonAdaptedMission mission : missions) {
            if (mission == null) {
                throw new IllegalValueException("Mission records must not be null.");
            }
            restoredMissions.add(mission.toModelType());
        }
        try {
            addressBook.restoreMissions(restoredMissions, ((Number) nextMissionNumber).longValue());
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }

}
