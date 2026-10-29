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
 * Represents a student identified by matriculation number, with profile details, mission submissions,
 * studio records, mastery check registrations, and notes.
 */
public class Student {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final MatriculationNumber matricNumber;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final StudioGroup studioGroup;
    private final MissionSubmissions missionSubmissions;
    private final RegisteredStudioRecords registeredStudioRecords;
    private final RegisteredMasteryChecks registeredMasteryChecks;
    private final RegisteredNotes registeredNotes;

    /**
     * Creates a student with no mission submissions, studio records, mastery check registrations, or notes.
     *
     * @param name Student's name.
     * @param matricNumber Student's matriculation number.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param tags Student's tags.
     * @param studioGroup Student's studio group.
     * @throws NullPointerException If the name, matriculation number, phone, email, or tag set is null.
     */
    public Student(
            Name name, MatriculationNumber matricNumber, Phone phone, Email email, Set<Tag> tags,
            StudioGroup studioGroup) {
        this(name, matricNumber, phone, email, tags, studioGroup, new MissionSubmissions(),
                new RegisteredStudioRecords(), new RegisteredMasteryChecks(), new RegisteredNotes());
    }

    /**
     * Creates a student with the supplied profile details and recorded student activities.
     *
     * @param name Student's name.
     * @param matricNumber Student's matriculation number.
     * @param phone Student's phone number.
     * @param email Student's email address.
     * @param tags Student's tags.
     * @param studioGroup Student's studio group.
     * @param missionSubmissions Student's recorded mission submissions.
     * @param registeredStudioRecords Student's registered studio records.
     * @param registeredMasteryChecks Student's numbered mastery check appointments.
     * @param registeredNotes Student's registered notes.
     * @throws NullPointerException If the name, matriculation number, phone, email, tag set,
     *         or any activity collection is null.
     */
    public Student(Name name, MatriculationNumber matricNumber, Phone phone, Email email, Set<Tag> tags,
                   StudioGroup studioGroup, MissionSubmissions missionSubmissions,
                   RegisteredStudioRecords registeredStudioRecords, RegisteredMasteryChecks registeredMasteryChecks,
                   RegisteredNotes registeredNotes) {
        requireAllNonNull(
                name, matricNumber, phone, email, tags, missionSubmissions, registeredStudioRecords,
                registeredMasteryChecks, registeredNotes);
        this.name = name;
        this.matricNumber = matricNumber;
        this.phone = phone;
        this.email = email;
        this.tags.addAll(tags);
        this.studioGroup = studioGroup;
        this.missionSubmissions = missionSubmissions;
        this.registeredStudioRecords = registeredStudioRecords;
        this.registeredMasteryChecks = registeredMasteryChecks;
        this.registeredNotes = registeredNotes;
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

    public MatriculationNumber getMatricNumber() {
        return matricNumber;
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

    /**
     * Copies this student with replacement submissions, preserving every other field.
     *
     * @param submissions Replacement immutable submission records.
     * @return Student containing the new submissions and existing profile and activities.
     * @throws NullPointerException If the submissions are null.
     */
    public Student withMissionSubmissions(MissionSubmissions submissions) {
        requireNonNull(submissions);
        return new Student(name, matricNumber, phone, email, tags, studioGroup, submissions,
                registeredStudioRecords, registeredMasteryChecks, registeredNotes);
    }

    public RegisteredStudioRecords getRegisteredStudioRecords() {
        return registeredStudioRecords;
    }

    /**
     * Returns student including the new studio records.
     *
     * @param registeredStudioRecords {@code RegisteredStudioRecords} to update.
     * @return {@code Student} containing the updated studio records.
     */
    public Student withRegisteredStudioRecords(RegisteredStudioRecords registeredStudioRecords) {
        requireNonNull(registeredStudioRecords);
        return new Student(name, matricNumber, phone, email, tags, studioGroup, missionSubmissions,
                registeredStudioRecords, registeredMasteryChecks, registeredNotes);
    }

    /**
     * Returns student including the new registered notes.
     *
     * @param registeredNotes {@code RegisteredNotes} to update.
     * @return {@code Student} containing the updated registered notes.
     */
    public Student withRegisteredNotes(RegisteredNotes registeredNotes) {
        requireNonNull(registeredNotes);
        return new Student(name, matricNumber, phone, email, tags, studioGroup, missionSubmissions,
                registeredStudioRecords, registeredMasteryChecks, registeredNotes);
    }

    /**
     * Returns student including the new mastery checks.
     *
     * @param registeredMasteryChecks {@code RegisteredMasteryChecks} to update.
     * @return {@code Student} containing the updated studio records.
     */
    public Student withRegisteredMasteryChecks(RegisteredMasteryChecks registeredMasteryChecks) {
        requireNonNull(registeredMasteryChecks);
        return new Student(name, matricNumber, phone, email, tags, studioGroup, missionSubmissions,
                registeredStudioRecords, registeredMasteryChecks, registeredNotes);
    }

    /**
     * Returns the student's mastery check registrations.
     * The registration mapping is unmodifiable, but appointment completion statuses remain mutable.
     *
     * @return Numbered mastery check appointments registered for this student.
     */
    public RegisteredMasteryChecks getRegisteredMasteryChecks() {
        return registeredMasteryChecks;
    }

    /**
     * Returns this student's immutable registered notes.
     *
     * @return Registered notes.
     */
    public RegisteredNotes getRegisteredNotes() {
        return registeredNotes;
    }

    /**
     * Checks student identity using matriculation numbers, independently of names and other profile details.
     * This defines a weaker notion of equality than comparing every student field.
     *
     * @param otherStudent Student to compare with this student; may be null.
     * @return True if the other student has the same matriculation number.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && matricNumber.equals(otherStudent.matricNumber);
    }

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
                && matricNumber.equals(otherStudent.matricNumber)
                && phone.equals(otherStudent.phone)
                && email.equals(otherStudent.email)
                && tags.equals(otherStudent.tags)
                && studioGroup.equals(otherStudent.studioGroup)
                && missionSubmissions.equals(otherStudent.missionSubmissions)
                && registeredStudioRecords.equals(otherStudent.registeredStudioRecords)
                && registeredMasteryChecks.equals(otherStudent.registeredMasteryChecks)
                && registeredNotes.equals(otherStudent.registeredNotes);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, matricNumber, phone, email, tags, studioGroup, missionSubmissions,
                registeredStudioRecords, registeredMasteryChecks, registeredNotes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("matriculationNumber", matricNumber)
                .add("phone", phone)
                .add("email", email)
                .add("tags", tags)
                .add("studioGroup", studioGroup)
                .add("missionSubmissions", missionSubmissions)
                .add("registeredStudioRecords", registeredStudioRecords)
                .add("registeredMasteryChecks", registeredMasteryChecks)
                .add("registeredNotes", registeredNotes)
                .toString();
    }

}
