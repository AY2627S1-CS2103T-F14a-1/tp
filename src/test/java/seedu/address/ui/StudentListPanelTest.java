package seedu.address.ui;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.getTypicalStudents;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysStudent;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import guitests.guihandles.StudentCardHandle;
import guitests.guihandles.StudentListPanelHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import seedu.address.model.student.Email;
import seedu.address.model.student.MatriculationNumber;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;

public class StudentListPanelTest extends GuiUnitTest {

    private static final ObservableList<Student> TYPICAL_STUDENTS =
            FXCollections.observableList(getTypicalStudents());

    private static final long CARD_CREATION_AND_DELETION_TIMEOUT = 2500;

    private StudentListPanelHandle studentListPanelHandle;

    /**
     * Initializes {@code studentListPanelHandle} with a {@code StudentListPanel} backed by {@code backingList}.
     * Also shows the {@code Stage} that displays only {@code StudentListPanel}.
     */
    private void initUi(ObservableList<Student> backingList) {
        StudentListPanel studentListPanel =
                new StudentListPanel(backingList);
        uiPartExtension.setUiPart(studentListPanel);

        studentListPanelHandle = new StudentListPanelHandle(getChildNode(studentListPanel.getRoot(),
                StudentListPanelHandle.STUDENT_LIST_VIEW_ID));
    }

    @Test
    public void display() {
        initUi(TYPICAL_STUDENTS);

        for (int i = 0; i < TYPICAL_STUDENTS.size(); i++) {
            studentListPanelHandle.navigateToCard(TYPICAL_STUDENTS.get(i));
            Student expectedStudent = TYPICAL_STUDENTS.get(i);
            StudentCardHandle actualCard = studentListPanelHandle.getStudentCardHandle(i);

            assertCardDisplaysStudent(expectedStudent, actualCard);
            assertEquals(Integer.toString(i + 1), actualCard.getId());
        }
    }

    /**
     * Verifies that creating and deleting large number of students in {@code StudentListPanel} requires less than
     * {@code CARD_CREATION_AND_DELETION_TIMEOUT} milliseconds to execute.
     */
    @Test
    public void performanceTest() {
        ObservableList<Student> backingList = createBackingList(10000);

        assertTimeoutPreemptively(ofMillis(CARD_CREATION_AND_DELETION_TIMEOUT), () -> {
            initUi(backingList);
            guiRobot.interact(backingList::clear);
        }, "Creation and deletion of student cards exceeded time limit");
    }

    /**
     * Returns a list of students containing {@code studentCount} students that is used to populate the
     * {@code StudentListPanel}.
     */

    private ObservableList<Student> createBackingList(int studentCount) {
        ObservableList<Student> backingList = FXCollections.observableArrayList();
        for (int i = 0; i < studentCount; i++) {
            Name name = new Name(i + "a");
            MatriculationNumber matricNumber = new MatriculationNumber(String.format("A%07dA", i));
            Phone phone = new Phone("000");
            Email email = new Email("a@aa");
            Student student = new Student(name, matricNumber,
                    phone, email, Collections.emptySet(), new StudioGroup("1A"));
            backingList.add(student);
        }
        return backingList;
    }

    @Test
    public void constructor_setsStudentListItems() {
        ObservableList<Student> students =
                FXCollections.observableArrayList(AMY);

        StudentListPanel panel = new StudentListPanel(students);
        ListView<Student> listView = panel.getStudentListView();

        assertSame(students, listView.getItems());
        assertEquals(1, listView.getItems().size());
        assertEquals(AMY, listView.getItems().get(0));
    }
}
