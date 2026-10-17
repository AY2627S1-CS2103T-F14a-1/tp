package seedu.address.model;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Student> PREDICATE_SHOW_ALL_STUDENTS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a student with the same identity as {@code student} exists in the address book.
     */
    boolean hasStudent(Student student);

    /**
     * Deletes the given student.
     * The student must exist in the address book.
     */
    void deleteStudent(Student target);

    /**
     * Adds the given student.
     * {@code student} must not already exist in the address book.
     */
    void addStudent(Student student);

    /**
     * Replaces the given student {@code target} with {@code editedStudent}.
     * {@code target} must exist in the address book.
     * The student identity of {@code editedStudent} must not be the same
     * as another existing student in the address book.
     */
    void setStudent(Student target, Student editedStudent);

    /** Returns an unmodifiable view of the filtered student list */
    ObservableList<Student> getFilteredStudentList();

    /**
     * Updates the filter of the filtered student list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredStudentList(Predicate<Student> predicate);

    /**
     * Checks whether a mission has the supplied name, ignoring case and whitespace differences.
     *
     * @param name Mission name to compare without changing its display text.
     * @return True if a matching name exists.
     */
    default boolean hasMissionName(String name) {
        java.util.Objects.requireNonNull(name);
        return getMissionList().stream()
                .anyMatch(mission -> mission.hasSameName(name));
    }

    /**
     * Creates a configured mission with an automatically allocated ID.
     *
     * @param description Nonblank mission description.
     * @param deadline Submission deadline.
     * @return Newly created mission.
     * @throws NullPointerException If either argument is null.
     * @throws IllegalArgumentException If the description is blank.
     * @throws IllegalStateException If mission IDs are exhausted.
     */
    Mission addMission(String description, LocalDate deadline);

    /**
     * Finds a mission by its stable ID.
     *
     * @param id Mission identifier.
     * @return Matching mission, if present.
     * @throws NullPointerException If the ID is null.
     */
    Optional<Mission> findMission(MissionId id);

    /**
     * Returns the read-only mission catalogue.
     *
     * @return Configured missions.
     */
    ObservableList<Mission> getMissionList();
}
