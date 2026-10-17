package guitests.guihandles;

import java.util.List;
import java.util.Optional;
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

    private Optional<Student> lastRememberedSelectedStudentCard;

    public StudentListPanelHandle(ListView<Student> studentListPanelNode) {
        super(studentListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code StudentCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     * @throws IllegalStateException if the selected card is currently not in the scene graph.
     */
    public StudentCardHandle getHandleToSelectedCard() {
        List<Student> selectedStudentList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedStudentList.size() != 1) {
            throw new AssertionError("Student list size expected 1.");
        }

        return getAllCardNodes().stream()
                .map(StudentCardHandle::new)
                .filter(handle -> handle.equals(selectedStudentList.get(0)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<Student> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
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
     * Navigates the listview to {@code index}.
     */
    public void navigateToCard(int index) {
        if (index < 0 || index >= getRootNode().getItems().size()) {
            throw new IllegalArgumentException("Index is out of bounds.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(index);
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Selects the {@code StudentCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
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

    /**
     * Remembers the selected {@code StudentCard} in the list.
     */
    public void rememberSelectedStudentCard() {
        List<Student> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedStudentCard = Optional.empty();
        } else {
            lastRememberedSelectedStudentCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code StudentCard} is different from the value remembered by the most recent
     * {@code rememberSelectedStudentCard()} call.
     */
    public boolean isSelectedStudentCardChanged() {
        List<Student> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedStudentCard.isPresent();
        } else {
            return !lastRememberedSelectedStudentCard.isPresent()
                    || !lastRememberedSelectedStudentCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
