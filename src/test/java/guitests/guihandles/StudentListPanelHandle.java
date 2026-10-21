package guitests.guihandles;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import seedu.address.model.student.Student;

/**
 * Provides a handle for {@code StudentListPanel} containing the list of {@code StudentCard}.
 */
public class StudentListPanelHandle extends NodeHandle<ListView<Student>> {
    public static final String STUDENT_LIST_VIEW_ID = "#studentListView";

    private static final String CARD_PANE_ID = "#cardPane";

    public StudentListPanelHandle(ListView<Student> studentListPanelNode) {
        super(studentListPanelNode);
    }

    /**
     * Navigates the listview to display {@code student}.
     */
    public void navigateToCard(Student student) {
        if (!getRootNode().getItems().contains(student)) {
            throw new IllegalArgumentException("Student does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(student);
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Returns the student card handle of a student associated with the {@code index} in the list.
     * @throws IllegalStateException if the selected card is currently not in the scene graph.
     */
    public StudentCardHandle getStudentCardHandle(int index) {
        return getAllCardNodes().stream()
                .map(StudentCardHandle::new)
                .filter(handle -> handle.equals(getStudent(index)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private Student getStudent(int index) {
        return getRootNode().getItems().get(index);
    }

    /**
     * Returns all card nodes in the scene graph.
     * Card nodes that are visible in the listview are definitely in the scene graph, while some nodes that are not
     * visible in the listview may also be in the scene graph.
     */
    private Set<Node> getAllCardNodes() {
        return guiRobot.lookup(CARD_PANE_ID).queryAll();
    }
}
