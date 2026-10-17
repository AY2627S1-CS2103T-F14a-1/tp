package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Attendance;
import seedu.address.model.student.Student;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    @FXML
    private ScrollPane studentDetails;
    @FXML
    private Label studentSummary;
    @FXML
    private GridPane missionSubmissions;
    @FXML
    private GridPane attendance;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
        showStudentDetails(false);
        studentSummary.setText("");
        missionSubmissions.getChildren().clear();
    }

    /**
     * Displays ordinary feedback or a student's details with a mission status table.
     *
     * @param result Result of the executed command.
     * @throws NullPointerException If the result is null.
     */
    public void setCommandResult(CommandResult result) {
        requireNonNull(result);
        setFeedbackToUser(result.getFeedbackToUser());
        result.getStudentToView().ifPresent(student -> {
            studentSummary.setText(result.getFeedbackToUser());
            populateMissionSubmissions(student, result.getMissions());
            populateAttendance(student);
            showStudentDetails(true);
            studentDetails.setVvalue(0);
        });
    }

    /**
     * Rebuilds the table with configured missions and their submission statuses.
     *
     * @param student Student whose submissions are displayed.
     * @param missions Catalogue snapshot supplied by the view command.
     */
    private void populateMissionSubmissions(Student student, List<Mission> missions) {
        assert student != null;
        assert missions != null;

        missionSubmissions.getChildren().clear();
        if (missions.isEmpty()) {
            addMissionCell("No missions configured.", 0, 0, "mission-header");
            return;
        }
        addMissionCell("Mission", 0, 0, "mission-header");
        addMissionCell("Status", 1, 0, "mission-header");
        int row = 1;
        for (Mission mission : missions) {
            boolean isSubmitted = student.getMissionSubmissions().hasSubmission(mission.getId());
            addMissionCell(mission.getId() + " · " + mission.getName() + "\nDue: " + mission.getDeadline(),
                    0, row, "mission-description");
            addMissionCell(isSubmitted ? "Submitted" : "Not submitted", 1, row,
                    isSubmitted ? "mission-submitted" : "mission-not-submitted");
            row++;
        }
    }

    /**
     * Adds a styled, wrapping cell that fills its column and row so cell borders align.
     *
     * @param text Visible cell text.
     * @param column Column index.
     * @param row Row index, including the header.
     * @param styleClass Style identifying the cell's purpose or status.
     */
    private void addMissionCell(String text, int column, int row, String styleClass) {
        Label cell = new Label(text);
        cell.getStyleClass().addAll("mission-cell", styleClass);
        cell.setWrapText(true);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        missionSubmissions.add(cell, column, row);
    }

    /**
     * Rebuilds the table with every supported week in ascending order.
     *
     * @param student Student whose submission status is displayed.
     */
    private void populateAttendance(Student student) {
        assert student != null;

        attendance.getChildren().clear();
        addAttendanceCell("Week", 0, 0, "mission-header");
        addAttendanceCell("Status", 1, 0, "mission-header");
        for (int week = Attendance.MIN_WEEK; week <= Attendance.MAX_WEEK; week++) {
            int row = week - Attendance.MIN_WEEK + 1;
            boolean hasAttended = student.getAttendance().hasAttendance(week);
            addAttendanceCell(String.valueOf(week), 0, row, "mission-week");
            addAttendanceCell(hasAttended ? "Present" : "Absent", 1, row,
                    hasAttended ? "present" : "absent");
        }
    }

    /**
     * Adds a styled, wrapping cell that fills its table column.
     *
     * @param text Visible cell text.
     * @param column Column index.
     * @param row Row index, including the header.
     * @param styleClass Style identifying the cell's purpose or status.
     */
    private void addAttendanceCell(String text, int column, int row, String styleClass) {
        Label cell = new Label(text);
        cell.getStyleClass().addAll("mission-cell", styleClass);
        cell.setWrapText(true);
        cell.setMaxWidth(Double.MAX_VALUE);
        attendance.add(cell, column, row);
    }

    /**
     * Switches between text feedback and the scrollable student view.
     *
     * @param isVisible Whether to display student details.
     */
    private void showStudentDetails(boolean isVisible) {
        studentDetails.setVisible(isVisible);
        studentDetails.setManaged(isVisible);
        resultDisplay.setVisible(!isVisible);
        resultDisplay.setManaged(!isVisible);
    }
}
