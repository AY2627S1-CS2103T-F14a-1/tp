package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class ResultDisplayTest {

    @BeforeAll
    public static void setUpJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });

        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            Platform.runLater(startup);
        }

        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void setFeedbackToUser_displaysFeedback() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();

            resultDisplay.setFeedbackToUser(
                    "Invalid command format.");

            Scene scene = new Scene(resultDisplay.getRoot());
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            Label resultLabel =
                    (Label) resultDisplay.getRoot()
                            .lookup("#resultDisplay");

            assertNotNull(resultLabel);
            assertEquals(
                    "Invalid command format.",
                    resultLabel.getText());
        });
    }

    @Test
    public void setFeedbackToUser_displaysLongFeedback()
            throws Exception {

        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay =
                    new ResultDisplay();

            String feedback =
                    "Invalid command format! "
                            + "Parameters: INDEX "
                            + "Example: view 1";

            resultDisplay.setFeedbackToUser(feedback);

            Scene scene = new Scene(resultDisplay.getRoot());
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            Label resultLabel =
                    (Label) resultDisplay.getRoot()
                            .lookup("#resultDisplay");

            assertNotNull(resultLabel);
            assertEquals(feedback, resultLabel.getText());
        });
    }

    private static void runOnJavaFxThread(
            Runnable assertions) throws Exception {

        FutureTask<Void> task =
                new FutureTask<>(assertions, null);

        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
