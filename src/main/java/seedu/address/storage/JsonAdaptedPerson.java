package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.Student.Address;
import seedu.address.model.Student.Email;
import seedu.address.model.Student.MissionSubmissions;
import seedu.address.model.Student.Name;
import seedu.address.model.Student.Student;
import seedu.address.model.Student.Phone;
import seedu.address.model.Student.StudioGroup;
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
    private final List<Integer> missionSubmissions = new ArrayList<>();

    /**
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
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("studioGroup") String studioGroup,
            @JsonProperty("missionSubmissions") List<Integer> missionSubmissions) {
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
     * Copies a Student into a storage representation with submission weeks sorted in ascending order.
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
     * Converts this Jackson-friendly adapted Student object into the model's {@code Student} object.
     *
     * @return A Student containing the validated saved details.
     * @throws IllegalValueException If a required field is missing or a saved value violates model constraints.
     */
    public Student toModelType() throws IllegalValueException {
        final List<Tag> StudentTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            StudentTags.add(tag.toModelType());
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

        final Set<Tag> modelTags = new HashSet<>(StudentTags);
        final MissionSubmissions modelMissionSubmissions = toModelMissionSubmissions();
        return new Student(modelName, modelPhone, modelEmail, modelAddress, modelTags, modelStudioGroup,
                modelMissionSubmissions);
    }

    /**
     * Validates saved tutorial weeks and converts them into immutable mission submissions.
     * Repeated weeks are represented by a single submission.
     *
     * @return Mission submissions containing the unique saved weeks.
     * @throws IllegalValueException If a saved week is null or outside the valid range.
     */
    public MissionSubmissions toModelMissionSubmissions() throws IllegalValueException {
        for (Integer week : missionSubmissions) {
            if (week == null || !MissionSubmissions.isValidWeek(week)) {
                throw new IllegalValueException(MissionSubmissions.MESSAGE_CONSTRAINTS);
            }
        }

        return new MissionSubmissions(new HashSet<>(missionSubmissions));
    }
}
