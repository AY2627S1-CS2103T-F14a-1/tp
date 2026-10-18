package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the students list.
     * This list will not contain any duplicate students.
     */
    ObservableList<Student> getStudentList();

    /**
     * Returns the configured missions without permitting external modification.
     *
     * @return Unmodifiable mission catalogue.
     */
    ObservableList<Mission> getMissionList();

    /**
     * Returns the persisted allocation counter, including IDs no longer in use.
     *
     * @return Next unused mission number.
     */
    long getNextMissionNumber();
}
