package seedu.address.testutil;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.student.Address;
import seedu.address.model.student.Attendance;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Student objects.
 */
public class StudentBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_STUDIO_GROUP = "1A";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private StudioGroup studioGroup;
    private MissionSubmissions missionSubmissions;
    private Attendance attendance;

    /**
     * Creates a {@code StudentBuilder} with the default details.
     */
    public StudentBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        studioGroup = new StudioGroup(DEFAULT_STUDIO_GROUP);
        missionSubmissions = new MissionSubmissions();
        attendance = new Attendance();
    }

    /**
     * Initializes the StudentBuilder with the data of {@code studentToCopy}.
     */
    public StudentBuilder(Student studentToCopy) {
        name = studentToCopy.getName();
        phone = studentToCopy.getPhone();
        email = studentToCopy.getEmail();
        address = studentToCopy.getAddress();
        tags = new HashSet<>(studentToCopy.getTags());
        studioGroup = studentToCopy.getStudioGroup();
        missionSubmissions = studentToCopy.getMissionSubmissions();
        attendance = studentToCopy.getAttendance();
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
     * Sets the {@code Address} of the {@code Student} that we are building.
     */
    public StudentBuilder withAddress(String address) {
        this.address = new Address(address);
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
     * Sets the mission submission weeks of the student being built.
     *
     * @param weeks Tutorial weeks with recorded submissions.
     * @return This builder.
     * @throws NullPointerException If the array is null.
     * @throws IllegalArgumentException If any week is outside the valid range.
     */
    public StudentBuilder withMissionSubmissions(int... weeks) {
        requireNonNull(weeks);

        Set<Integer> submittedWeeks = new HashSet<>();
        for (int week : weeks) {
            submittedWeeks.add(week);
        }
        this.missionSubmissions = new MissionSubmissions(submittedWeeks);
        return this;
    }

    /**
     * Sets the attendance weeks of the student being built.
     *
     * @param weeks Tutorial weeks with recorded attendance.
     * @return This builder.
     * @throws NullPointerException If the array is null.
     * @throws IllegalArgumentException If any week is outside the valid range.
     */
    public StudentBuilder withAttendance(int... weeks) {
        requireNonNull(weeks);

        Set<Integer> attendedWeeks = new HashSet<>();
        for (int week : weeks) {
            attendedWeeks.add(week);
        }
        this.attendance = new Attendance(attendedWeeks);
        return this;
    }

    /**
     * Builds a student using this builder's current values.
     *
     * @return A student with the configured details.
     */
    public Student build() {
        return new Student(name, phone, email, address, tags, studioGroup, missionSubmissions, attendance);
    }

}
