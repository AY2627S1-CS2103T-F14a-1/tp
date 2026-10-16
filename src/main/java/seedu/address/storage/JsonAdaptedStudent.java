package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.student.StudioRecord;
import seedu.address.model.student.StudioRecords;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String studioGroup;
    private final List<Object> missionSubmissions = new ArrayList<>();
    private final List<Object> studioRecords = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     * Constructs a storage representation, deferring validation until model conversion.
     * Missing or null studio records default to empty collections.
     * Missing or null mission submissions represent no recorded submissions.
     *
     * @param name Student's name.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param tags Student's tags, or null for no tags.
     * @param studioGroup Student's studio group.
     * @param studioRecords Recorded weekly studio records, or null for no records.
     * @param missionSubmissions Recorded mission IDs, or null for no submissions.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("studio group") String studioGroup,
            @JsonProperty("missionSubmissions") List<?> missionSubmissions,
            @JsonProperty("studioRecords") List<?> studioRecords) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.studioGroup = studioGroup;
        if (missionSubmissions != null) {
            this.missionSubmissions.addAll(missionSubmissions);
        }
        if (studioRecords != null) {
            this.studioRecords.addAll(studioRecords);
        }
    }

    /**
     * Copies a student into a storage representation with mission IDs sorted in numeric order.
     *
     * @param source Student to save.
     * @throws NullPointerException If the source is null.
     */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        studioGroup = source.getStudioGroup().studioGroup;
        missionSubmissions.addAll(source.getMissionSubmissions().getSubmittedMissionIds()
                .stream()
                .sorted(java.util.Comparator.comparingLong(MissionId::getNumber))
                .map(MissionId::toString)
                .collect(Collectors.toList()));
        studioRecords.addAll(source.getStudioRecords().getRecordsByWeek().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> List.of(entry.getKey(), entry.getValue().getEffortScore()))
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted student object into the model's {@code Student} object.
     *
     * @return A student containing the validated saved details.
     * @throws IllegalValueException If a required field is missing or a saved value violates model constraints.
     */
    public Student toModelType() throws IllegalValueException {
        final List<Tag> studentTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            if (tag == null) {
                throw new IllegalValueException("Student tags must not be null.");
            }
            studentTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (studioGroup == null) {
            throw new IllegalValueException(String.format(
                    MISSING_FIELD_MESSAGE_FORMAT, StudioGroup.class.getSimpleName()));
        }
        if (!StudioGroup.isValidStudioGroup(studioGroup)) {
            throw new IllegalValueException(StudioGroup.MESSAGE_CONSTRAINTS);
        }
        final StudioGroup modelStudioGroup = new StudioGroup(studioGroup);

        final Set<Tag> modelTags = new HashSet<>(studentTags);
        final MissionSubmissions modelMissionSubmissions = toModelMissionSubmissions();
        final StudioRecords modelStudioRecords = toModelStudioRecords();
        return new Student(modelName, modelPhone, modelEmail, modelTags, modelStudioGroup,
                modelMissionSubmissions, modelStudioRecords);
    }

    /**
     * Validates saved mission IDs and creates immutable submission records.
     *
     * @return Unique submitted mission IDs.
     * @throws IllegalValueException If a saved value is not a canonical mission ID string.
     */
    public MissionSubmissions toModelMissionSubmissions() throws IllegalValueException {
        Set<MissionId> submittedIds = new HashSet<>();
        for (Object value : missionSubmissions) {
            if (!(value instanceof String id)) {
                throw new IllegalValueException(MissionId.MESSAGE_CONSTRAINTS);
            }
            try {
                submittedIds.add(new MissionId(id));
            } catch (IllegalArgumentException e) {
                throw new IllegalValueException(MissionId.MESSAGE_CONSTRAINTS);
            }
        }
        return new MissionSubmissions(submittedIds);
    }

    /**
     * Validates saved studio records and converts them into immutable records.
     * Each saved record contains a week followed by its effort score. If a week is repeated,
     * the last saved record for that week is retained.
     *
     * @return Studio records containing the unique saved weeks and their effort scores.
     * @throws IllegalValueException If a saved record is malformed or contains an invalid value.
     */
    public StudioRecords toModelStudioRecords() throws IllegalValueException {
        Map<Integer, StudioRecord> recordedWeeks = new HashMap<>();
        for (Object value : studioRecords) {
            if (!(value instanceof List<?> record) || record.size() != 2) {
                throw new IllegalValueException(StudioRecords.MESSAGE_CONSTRAINTS);
            }

            Object savedWeek = record.get(0);
            if (!(savedWeek instanceof Integer week) || !StudioRecords.isValidWeek(week)) {
                throw new IllegalValueException(StudioRecords.MESSAGE_CONSTRAINTS);
            }

            Object savedEffortScore = record.get(1);
            if (!(savedEffortScore instanceof Integer effortScore)
                    || !StudioRecord.isValidEffortScore(effortScore)) {
                throw new IllegalValueException(StudioRecord.MESSAGE_CONSTRAINTS);
            }
            recordedWeeks.put(week, new StudioRecord(effortScore));
        }

        return new StudioRecords(recordedWeeks);
    }
}
