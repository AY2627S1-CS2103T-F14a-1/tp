package guitests.guihandles;

import javafx.scene.Node;

/**
 * A handler for the {@code StudentDetailsPanel} of the UI.
 */
public class StudentDetailsPanelHandle extends NodeHandle<Node> {

    //@@author nuggettowin-reused
    //Reused from AB4
    public static final String STUDENT_DETAILS_PANEL_ID = "#studentDetailsScrollPane";

    public StudentDetailsPanelHandle(Node studentDetailsPanelNode) {
        super(studentDetailsPanelNode);
    }
    //@@author
}
