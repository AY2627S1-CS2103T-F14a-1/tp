package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Student in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final StudioGroup studioGroup;
    private final MissionSubmissions missionSubmissions;
    private final RegisteredStudioRecords registeredStudioRecords;

    /**
     * Creates a student with no recorded mission submissions.
     *
     * @param name Student's name.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param tags Student's tags.
     * @throws NullPointerException If any argument is null.
     */
    public Student(
            Name name, Phone phone, Email email, Set<Tag> tags,
            StudioGroup studioGroup) {
        this(name, phone, email, tags, studioGroup, new MissionSubmissions(), new RegisteredStudioRecords());
    }

    /**
     * Creates a student with the given details and mission submissions.
     *
     * @param name Student's name.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param tags Student's tags.
     * @param missionSubmissions Student's recorded mission submissions.
     * @param registeredStudioRecords Student's registered studio records.
     * @throws NullPointerException If any argument is null.
     */
    public Student(Name name, Phone phone, Email email, Set<Tag> tags, StudioGroup studioGroup,
                   MissionSubmissions missionSubmissions, RegisteredStudioRecords registeredStudioRecords) {
        requireAllNonNull(name, phone, email, tags, missionSubmissions, registeredStudioRecords);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.tags.addAll(tags);
        this.studioGroup = studioGroup;
        this.missionSubmissions = missionSubmissions;
        this.registeredStudioRecords = registeredStudioRecords;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public StudioGroup getStudioGroup() {
        return studioGroup;
    }

    /**
     * Returns this student's immutable mission submissions.
     *
     * @return Recorded mission submissions.
     */
    public MissionSubmissions getMissionSubmissions() {
        return missionSubmissions;
    }

    public RegisteredStudioRecords getRegisteredStudioRecords() {
        return registeredStudioRecords;
    }

    /**
     * Returns student including the new studio records.
     *
     * @param registeredStudioRecords {@code RegisteredStudioRecords} to update.
     * @return @{code Student} containing the updated studio records.
     */
    public Student withRegisteredStudioRecords(RegisteredStudioRecords registeredStudioRecords) {
        requireNonNull(registeredStudioRecords);
        return new Student(name, phone, email, tags, studioGroup, missionSubmissions, registeredStudioRecords);
    }

    /**
     * Returns true if both students have the same name.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && otherStudent.getName().equals(getName());
    }

    /**
     * Returns true if both students have the same identity and data fields.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return name.equals(otherStudent.name)
                && phone.equals(otherStudent.phone)
                && email.equals(otherStudent.email)
                && tags.equals(otherStudent.tags)
                && studioGroup.equals(otherStudent.studioGroup)
                && missionSubmissions.equals(otherStudent.missionSubmissions)
                && registeredStudioRecords.equals(otherStudent.registeredStudioRecords);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, tags, studioGroup, missionSubmissions, registeredStudioRecords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("tags", tags)
                .add("studioGroup", studioGroup)
                .add("missionSubmissions", missionSubmissions)
                .add("registeredStudioRecords", registeredStudioRecords)
                .toString();
    }

}
