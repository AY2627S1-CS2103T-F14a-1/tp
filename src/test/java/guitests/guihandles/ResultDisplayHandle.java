package guitests.guihandles;

import javafx.scene.control.Label;

/**
 * A handler for the {@code ResultDisplay} of the UI
 */
public class ResultDisplayHandle extends NodeHandle<Label> {

    //@@author nuggettowin-reused
    //Reused from AB4
    public static final String RESULT_DISPLAY_ID = "#resultDisplay";

    public ResultDisplayHandle(Label resultDisplayNode) {
        super(resultDisplayNode);
    }

    /**
     * Returns the text in the result display.
     */
    public String getText() {
        return getRootNode().getText();
    }
    //@@author
}
