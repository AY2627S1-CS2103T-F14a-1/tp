package guitests.guihandles;

import javafx.scene.Node;

/**
 * A handler for the {@code StudentDetailsPanel} of the UI.
 */
public class StudentDetailsPanelHandle extends NodeHandle<Node> {

    public static final String STUDENT_DETAILS_PANEL_ID = "#studentDetailsScrollPane";

    public StudentDetailsPanelHandle(Node studentDetailsPanelNode) {
        super(studentDetailsPanelNode);
    }

}
