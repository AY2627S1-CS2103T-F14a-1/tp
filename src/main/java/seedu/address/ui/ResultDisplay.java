package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Displays command feedback and warnings in a scrollable area.
 * Grows with its content up to the maximum height assigned by its parent.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private Label resultDisplay;

    @FXML
    private Label warningDisplay;

    @FXML
    private ScrollPane resultScrollPane;

    @FXML
    private VBox resultContent;

    /**
     * Creates a {@code ResultDisplay} and configures its content-based height.
     */
    public ResultDisplay() {
        super(FXML);

        Region root = getRoot();
        root.setMinHeight(0);
        root.setMaxHeight(Double.MAX_VALUE);
        root.prefHeightProperty().bind(Bindings.createDoubleBinding(
                this::calculatePreferredHeight,
                root.widthProperty(),
                root.maxHeightProperty(),
                resultScrollPane.viewportBoundsProperty(),
                resultScrollPane.insetsProperty(),
                resultDisplay.textProperty(),
                resultDisplay.fontProperty(),
                warningDisplay.textProperty(),
                warningDisplay.fontProperty(),
                warningDisplay.managedProperty(),
                resultContent.insetsProperty()));
    }

    /**
     * Calculates the required height from the content's preferred size.
     *
     * @return Content height, including scroll-pane insets, limited by the
     *         maximum height allocated by the main window.
     */
    private double calculatePreferredHeight() {
        double viewportWidth = resultScrollPane.getViewportBounds().getWidth();
        double horizontalInsets = resultScrollPane.getInsets().getLeft()
                + resultScrollPane.getInsets().getRight();
        double availableWidth = viewportWidth > 0
                ? viewportWidth
                : Math.max(0, getRoot().getWidth() - horizontalInsets);
        double verticalInsets = resultScrollPane.getInsets().getTop()
                + resultScrollPane.getInsets().getBottom();

        return Math.min(getRoot().getMaxHeight(),
                resultContent.prefHeight(availableWidth) + verticalInsets);
    }

    /**
     * Displays feedback and scrolls to its beginning.
     *
     * @param feedbackToUser Feedback message to display.
     * @throws NullPointerException If the feedback is null.
     */
    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);

        resultDisplay.setText(feedbackToUser);
        warningDisplay.setText("");
        warningDisplay.setManaged(false);
        warningDisplay.setVisible(false);
        resultScrollPane.setVvalue(resultScrollPane.getVmin());
    }

    /**
     * Displays feedback and its warnings in separate visual styles.
     *
     * @param feedbackToUser Feedback message to display.
     * @param warnings Warnings to display below the feedback.
     * @throws NullPointerException If the feedback, warnings, or a warning is null.
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
