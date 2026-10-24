package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionCatalogue;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.model.student.UniqueStudentList;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameStudent comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueStudentList students = new UniqueStudentList();
    private final MissionCatalogue missions = new MissionCatalogue();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Students in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the student list with {@code students}.
     * {@code students} must not contain duplicate students.
     */
    public void setStudents(List<Student> students) {
        this.students.setStudents(students);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        UniqueStudentList replacementStudents = new UniqueStudentList();
        replacementStudents.setStudents(newData.getStudentList());
        MissionCatalogue replacementMissions = new MissionCatalogue();
        replacementMissions.restore(newData.getMissionList(), newData.getNextMissionNumber());

        students.setStudents(replacementStudents);
        missions.restore(replacementMissions.getMissions(), replacementMissions.getNextMissionNumber());
    }

    /**
     * Creates a mission without changing existing student records.
     *
     * @param name Nonblank assignment name.
     * @param deadline Assignment deadline.
     * @return Newly allocated mission.
     * @throws IllegalArgumentException If the name is blank or duplicates another mission.
     * @throws NullPointerException If either argument is null.
     * @throws IllegalStateException If mission IDs are exhausted.
     */
    public Mission addMission(String name, LocalDate deadline) {
        return missions.add(name, deadline);
    }

    /**
     * Deletes a mission and its submissions from every student, preserving the allocation counter.
     * Replacement records are prepared before any state is changed.
     *
     * @param id Mission to delete.
     * @return Deleted mission.
     * @throws IllegalArgumentException If the mission does not exist.
     * @throws NullPointerException If the ID is null.
     */
    public Mission deleteMission(MissionId id) {
        requireNonNull(id);
        Mission deleted = missions.find(id).orElseThrow(() ->
                new IllegalArgumentException("Mission does not exist: " + id));
        List<Student> updatedStudents = students.asUnmodifiableObservableList().stream().map(student ->
                student.withMissionSubmissions(student.getMissionSubmissions().withoutSubmission(id))).toList();
        List<Mission> remaining = missions.getMissions().stream()
                .filter(mission -> !mission.getId().equals(id)).toList();
        students.setStudents(updatedStudents);
        missions.restore(remaining, missions.getNextMissionNumber());
        return deleted;
    }

    /**
     * Finds a configured mission by its stable ID.
     *
     * @param id Mission identifier.
     * @return Matching mission, if present.
     * @throws NullPointerException If the ID is null.
     */
    public Optional<Mission> findMission(MissionId id) {
        return missions.find(id);
    }

    /**
     * Replaces a mission without modifying student submissions or ID allocation.
     *
     * @param target Mission to replace.
     * @param editedMission Replacement retaining the target's ID.
     * @throws NullPointerException If either mission is null.
     * @throws IllegalArgumentException If the target is absent, its ID changes,
     *     or another mission already has the replacement's name.
     */
    public void setMission(Mission target, Mission editedMission) {
        missions.setMission(target, editedMission);
    }

    /**
     * Restores the catalogue after validating all records and allocation state.
     *
     * @param records Saved missions.
     * @param nextNumber Next unused mission number.
     * @throws IllegalArgumentException If IDs or names are duplicated, or the counter is invalid.
     * @throws NullPointerException If the list or a mission is null.
     */
    public void restoreMissions(List<Mission> records, long nextNumber) {
        missions.restore(records, nextNumber);
    }

    @Override
    public ObservableList<Mission> getMissionList() {
        return missions.getMissions();
    }

    @Override
    public long getNextMissionNumber() {
        return missions.getNextMissionNumber();
    }

    //// student-level operations

    /**
     * Returns true if a student with the same identity as {@code student} exists in the address book.
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.contains(student);
    }

    /**
     * Adds a student to the address book.
     * The student must not already exist in the address book.
     */
    public void addStudent(Student p) {
        students.add(p);
    }

    /**
     * Replaces the given student {@code target} in the list with {@code editedStudent}.
     * {@code target} must exist in the address book.
     * The student identity of {@code editedStudent} must not be the same
     * as another existing student in the address book.
     */
    public void setStudent(Student target, Student editedStudent) {
        requireNonNull(editedStudent);

        students.setStudent(target, editedStudent);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeStudent(Student key) {
        students.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("students", students)
                .toString();
    }

    @Override
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return students.equals(otherAddressBook.students)
                && getMissionList().equals(otherAddressBook.getMissionList())
                && getNextMissionNumber() == otherAddressBook.getNextMissionNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(students, getMissionList(), getNextMissionNumber());
    }
}
