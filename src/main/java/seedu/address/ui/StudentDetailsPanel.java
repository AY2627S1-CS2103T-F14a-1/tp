package seedu.address.ui;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;

/**
 * UI panel that displays the selected student's profile, attendance,
 * and mission-submission information.
 */
public class StudentDetailsPanel extends UiPart<Region> {

    private static final String FXML = "StudentDetailsPanel.fxml";

    @FXML
    private Label studentName;

    @FXML
    private Label studioGroup;

    @FXML
    private Label phone;

    @FXML
    private Label email;

    @FXML
    private Label tags;

    @FXML
    private VBox emptyMessage;

    @FXML
    private VBox studentDetails;

    @FXML
    private GridPane studioRecordGrid;

    @FXML
    private GridPane missionSubmissionsGrid;

    @FXML
    private ScrollPane studentDetailsScrollPane;

    /**
     * Creates a {@code StudentDetailsPanel} and loads its FXML layout.
     */
    public StudentDetailsPanel() {
        super(FXML);
        clearStudent();
    }

    /**
     * Displays the details of the given student.
     *
     * <p>If {@code student} is {@code null}, the panel is reset to its
     * default empty state.</p>
     *
     * @param student student whose profile should be displayed
     */
    public void showStudent(
            Student student,
            List<Mission> missions) {

        if (student == null) {
            clearStudent();
            return;
        }

        emptyMessage.setVisible(false);
        emptyMessage.setManaged(false);

        studentDetailsScrollPane.setVisible(true);
        studentDetailsScrollPane.setManaged(true);

        studentDetails.setVisible(true);
        studentDetails.setManaged(true);

        studentName.setText(student.getName().fullName);
        studioGroup.setText(
                "Studio group: " + student.getStudioGroup().studioGroup);
        phone.setText("Phone: " + student.getPhone().value);
        email.setText("Email: " + student.getEmail().value);
        tags.setText("Tags: " + student.getTags());

        populateMissionSubmissions(student, missions);
        populateStudioRecord(student);
    }

    /**
     * Resets the panel to its default state where no student is selected.
     */
    public void clearStudent() {
        emptyMessage.setVisible(true);
        emptyMessage.setManaged(true);

        studentDetailsScrollPane.setVisible(false);
        studentDetailsScrollPane.setManaged(false);

        studentDetails.setVisible(false);
        studentDetails.setManaged(false);
    }

    /**
     * Populates the mission-submission grid for the given student.
     *
     * <p>Each valid mission week is displayed together with either
     * {@code Submitted} or {@code Not submitted}.</p>
     *
     * @param student student whose mission-submission status is displayed
     * @param missions list of all missions
     */
    private void populateMissionSubmissions(
            Student student,
            List<Mission> missions) {

        missionSubmissionsGrid.getChildren().clear();

        if (missions.isEmpty()) {
            addGridLabel(
                    missionSubmissionsGrid,
                    "No missions configured.",
                    0,
                    0,
                    false);
            return;
        }

        addGridLabel(
                missionSubmissionsGrid,
                "Mission",
                0,
                0,
                true);

        addGridLabel(
                missionSubmissionsGrid,
                "Status",
                1,
                0,
                true);

        MissionSubmissions submissions =
                student.getMissionSubmissions();

        int row = 1;

        for (Mission mission : missions) {
            MissionId missionId = mission.getId();
            System.out.println(mission.getDeadline());

            String status = submissions.hasSubmission(missionId)
                    ? "Submitted"
                    : "Not submitted";

            addMissionCell(
                    missionSubmissionsGrid,
                    mission,
                    row);

            addGridLabel(
                    missionSubmissionsGrid,
                    status,
                    1,
                    row,
                    false);

            row++;
        }
    }

    /**
     * Populates the studio record grid for the given student.
     *
     * <p>Each valid week is displayed together with either
     * {@code Present} or {@code Absent}.</p>
     *
     * @param student student whose studio record is displayed
     */
    private void populateStudioRecord(Student student) {
        studioRecordGrid.getChildren().clear();

        RegisteredStudioRecords studioRecord = student.getRegisteredStudioRecords();

        int row = 1;

        addGridLabel(
                studioRecordGrid,
                "Week",
                0,
                0,
                true);

        addGridLabel(
                studioRecordGrid,
                "Effort Score",
                1,
                0,
                true);

        for (int week = RegisteredStudioRecords.MIN_WEEK;
             week <= RegisteredStudioRecords.MAX_WEEK; week++) {

            StudioRecord record = studioRecord.getRecordsByWeek().get(week);
            boolean hasAttended = record != null;
            int score = hasAttended ? record.getEffortScore() : 0;

            addGridLabel(
                    studioRecordGrid,
                    String.valueOf(week),
                    0,
                    row,
                    false);

            addGridLabel(
                    studioRecordGrid,
                    String.valueOf(score),
                    1,
                    row,
                    false);

            row++;
        }
    }

    /**
     * Creates and adds a label to the specified cell in a grid.
     *
     * @param grid grid to which the label should be added
     * @param text text displayed by the label
     * @param column zero-based column index
     * @param row zero-based row index
     * @param isHeader whether the label represents a column header
     */
    private void addGridLabel(
            GridPane grid,
            String text,
            int column,
            int row,
            boolean isHeader) {

        Label label = new Label(text);

        label.setWrapText(true);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);

        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setVgrow(label, Priority.ALWAYS);
        GridPane.setFillWidth(label, true);
        GridPane.setFillHeight(label, true);
        GridPane.setValignment(label, VPos.TOP);
        GridPane.setHalignment(label, javafx.geometry.HPos.LEFT);

        label.getStyleClass().add("grid-cell");

        if (isHeader) {
            label.getStyleClass().add("grid-header");
        } else {
            label.getStyleClass().add("grid-value");
        }

        grid.add(label, column, row);
    }

    private void addMissionCell(
            GridPane grid,
            Mission mission,
            int row) {

        VBox missionDetails = new VBox(2);

        missionDetails.getStyleClass().add("grid-cell");
        missionDetails.setMaxWidth(Double.MAX_VALUE);

        Label missionName = new Label(
                mission.getId()
                        + " · "
                        + mission.getName());

        Label deadline = new Label(
                "Due: " + mission.getDeadline());

        missionName.getStyleClass().add("grid-value");
        deadline.getStyleClass().add("mission-deadline");

        missionName.setWrapText(true);
        deadline.setWrapText(true);

        missionName.setMaxWidth(Double.MAX_VALUE);
        deadline.setMaxWidth(Double.MAX_VALUE);

        missionDetails.getChildren().addAll(
                missionName,
                deadline);

        GridPane.setHgrow(
                missionDetails,
                javafx.scene.layout.Priority.ALWAYS);

        GridPane.setFillWidth(
                missionDetails,
                true);

        grid.add(missionDetails, 0, row);
    }
}
