package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.TypicalStudents.AMY;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import seedu.address.model.student.Student;

public class StudentListPanelTest {

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
