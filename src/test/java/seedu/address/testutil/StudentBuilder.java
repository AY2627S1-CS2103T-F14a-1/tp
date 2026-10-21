package seedu.address.testutil;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.student.StudioRecord;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Student objects.
 */
public class StudentBuilder {

    /**
     * Associates a studio week with its studio record for building a student.
     *
     * @param week Studio week of the record.
     * @param studioRecord Studio record for that week.
     */
    public record StudioRecordSet(int week, StudioRecord studioRecord) {}

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_STUDIO_GROUP = "1A";

    private Name name;
    private Phone phone;
    private Email email;
    private Set<Tag> tags;
    private StudioGroup studioGroup;
    private MissionSubmissions missionSubmissions;
    private RegisteredStudioRecords registeredStudioRecords;
    private RegisteredMasteryChecks registeredMasteryChecks;

    /**
     * Creates a {@code StudentBuilder} with the default details.
     */
    public StudentBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        tags = new HashSet<>();
        studioGroup = new StudioGroup(DEFAULT_STUDIO_GROUP);
        missionSubmissions = new MissionSubmissions();
        registeredStudioRecords = new RegisteredStudioRecords();
        registeredMasteryChecks = new RegisteredMasteryChecks();
    }

    /**
     * Initializes the StudentBuilder with the data of {@code studentToCopy}.
     */
    public StudentBuilder(Student studentToCopy) {
        name = studentToCopy.getName();
        phone = studentToCopy.getPhone();
        email = studentToCopy.getEmail();
        tags = new HashSet<>(studentToCopy.getTags());
        studioGroup = studentToCopy.getStudioGroup();
        missionSubmissions = studentToCopy.getMissionSubmissions();
        registeredStudioRecords = studentToCopy.getRegisteredStudioRecords();
        registeredMasteryChecks = studentToCopy.getRegisteredMasteryChecks();
    }

    /**
     * Sets the {@code Name} of the {@code Student} that we are building.
     */
    public StudentBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Student} that we are building.
     */
    public StudentBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Student} that we are building.
     */
    public StudentBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Student} that we are building.
     */
    public StudentBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the Studio Group of the {@code Student} that we are building.
     */
    public StudentBuilder withStudioGroup(String studioGroup) {
        this.studioGroup = new StudioGroup(studioGroup);
        return this;
    }

    /**
     * Sets the student's submitted mission IDs.
     *
     * @param ids Canonical mission ID strings.
     * @return This builder.
     * @throws NullPointerException If the array or an ID is null.
     * @throws IllegalArgumentException If an ID is invalid.
     */
    public StudentBuilder withMissionSubmissions(String... ids) {
        requireNonNull(ids);
        Set<MissionId> submittedIds = new HashSet<>();
        for (String id : ids) {
            submittedIds.add(new MissionId(id));
        }
        this.missionSubmissions = new MissionSubmissions(submittedIds);
        return this;
    }

    /**
     * Sets the registered studio record weeks of the student being built.
     *
     * @param sets Studio records to add.
     * @return This builder.
     * @throws NullPointerException If the array is null.
     * @throws IllegalArgumentException If any week is outside the valid range.
     */
    public StudentBuilder withStudioRecords(StudioRecordSet... sets) {
        requireNonNull(sets);

        Map<Integer, StudioRecord> recordedStudios = new HashMap<>();
        for (StudioRecordSet set : sets) {
            StudioRecord studioRecord = set.studioRecord();
            requireNonNull(studioRecord);
            recordedStudios.put(set.week(), studioRecord);
        }
        this.registeredStudioRecords = new RegisteredStudioRecords(recordedStudios);
        return this;
    }

    /**
     * Sets the numbered mastery check appointments of the student being built.
     *
     * @param appointments Appointments keyed by positive mastery check number.
     * @return This builder.
     * @throws NullPointerException If the map, a key, or an appointment is null.
     * @throws IllegalArgumentException If a mastery check number is not positive.
     */
    public StudentBuilder withMasteryChecks(Map<Integer, MasteryCheck> appointments) {
        this.registeredMasteryChecks = new RegisteredMasteryChecks(appointments);
        return this;
    }

    /**
     * Builds a student using this builder's current values.
     *
     * @return A student with the configured details.
     */
    public Student build() {
        return new Student(name, phone, email, tags, studioGroup, missionSubmissions, registeredStudioRecords,
                registeredMasteryChecks);
    }

}
