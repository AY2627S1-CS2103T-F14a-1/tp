package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A UI component that displays feedback from command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private Label resultDisplay;

    /**
     * Creates a {@code ResultDisplay} and loads its FXML layout.
     */
    public ResultDisplay() {
        super(FXML);
    }

    /**
     * Displays feedback to the user.
     *
     * @param feedbackToUser feedback message to display
     * @throws NullPointerException if {@code feedbackToUser} is null
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
    }
}
