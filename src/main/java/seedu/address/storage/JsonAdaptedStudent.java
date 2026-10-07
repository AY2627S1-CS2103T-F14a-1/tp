package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Address;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String studioGroup;
    // Preserve JSON types until validation so fractional weeks cannot be silently truncated.
    private final List<Object> missionSubmissions = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     * Constructs a storage representation, deferring validation until model conversion.
     * Missing or null mission submissions default to an empty collection for older saved files.
     *
     * @param name Student's name.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param address Student's address.
     * @param tags Student's tags, or null for no tags.
     * @param studioGroup Student's studio group.
     * @param missionSubmissions Recorded tutorial weeks, or null for no submissions.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("studio group") String studioGroup,
            @JsonProperty("missionSubmissions") List<?> missionSubmissions) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.studioGroup = studioGroup;
        if (missionSubmissions != null) {
            this.missionSubmissions.addAll(missionSubmissions);
        }
    }

    /**
     * Copies a student into a storage representation with submission weeks sorted in ascending order.
     *
     * @param source Student to save.
     * @throws NullPointerException If the source is null.
     */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        studioGroup = source.getStudioGroup().studioGroup;
        missionSubmissions.addAll(source.getMissionSubmissions().getSubmittedWeeks()
                .stream()
                .sorted()
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

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        if (studioGroup == null) {
            throw new IllegalValueException(String.format(
                    MISSING_FIELD_MESSAGE_FORMAT, StudioGroup.class.getSimpleName()));
        }
        if (!StudioGroup.isValidStudioGroup(studioGroup)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final StudioGroup modelStudioGroup = new StudioGroup(studioGroup);

        final Set<Tag> modelTags = new HashSet<>(studentTags);
        final MissionSubmissions modelMissionSubmissions = toModelMissionSubmissions();
        return new Student(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelStudioGroup,
                modelMissionSubmissions);
    }

    /**
     * Validates saved tutorial weeks and converts them into immutable mission submissions.
     * Repeated weeks are represented by a single submission.
     *
     * @return Mission submissions containing the unique saved weeks.
     * @throws IllegalValueException If a saved week is not an integer in the valid range.
     */
    public MissionSubmissions toModelMissionSubmissions() throws IllegalValueException {
        Set<Integer> submittedWeeks = new HashSet<>();
        for (Object value : missionSubmissions) {
            if (!(value instanceof Integer week) || !MissionSubmissions.isValidWeek(week)) {
                throw new IllegalValueException(MissionSubmissions.MESSAGE_CONSTRAINTS);
            }
            submittedWeeks.add(week);
        }

        return new MissionSubmissions(submittedWeeks);
    }
}
