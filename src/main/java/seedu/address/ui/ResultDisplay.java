package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

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

    @FXML
    private Label warningDisplay;

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
        warningDisplay.setText("");
        warningDisplay.setManaged(false);
        warningDisplay.setVisible(false);
    }

    /**
     * Displays feedback and its warnings to the user in separate visual styles.
     *
     * @param feedbackToUser feedback message to display
     * @param warnings warnings to display below the feedback
     * @throws NullPointerException if {@code feedbackToUser}, {@code warnings}, or a warning is null
     */
    public void setFeedbackToUser(String feedbackToUser, List<String> warnings) {
        requireNonNull(warnings);
        warnings.forEach(ResultDisplay::requireWarning);

        setFeedbackToUser(feedbackToUser);
        if (warnings.isEmpty()) {
            return;
        }

        warningDisplay.setText("WARNING:\n" + String.join("\n", warnings));
        warningDisplay.setManaged(true);
        warningDisplay.setVisible(true);
    }

    private static void requireWarning(String warning) {
        requireNonNull(warning);
    }
}
