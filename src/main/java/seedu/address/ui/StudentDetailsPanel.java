package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Student;

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
    private Label address;

    @FXML
    private Label tags;

    @FXML
    private VBox emptyMessage;

    @FXML
    private VBox studentDetails;

    @FXML
    private GridPane missionSubmissionsGrid;

    @FXML
    private ScrollPane studentDetailsScrollPane;

    /**
     * Creates a {@code StudentDetailsPanel} and loads its FXML layout.
     */
    public StudentDetailsPanel() {
        super(FXML);
    }

    /**
     * Displays the details of the given student.
     *
     * <p>If {@code student} is {@code null}, the panel is reset to its
     * default empty state.</p>
     *
     * @param student student whose profile should be displayed
     */
    public void showStudent(Student student) {
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
        address.setText("Address: " + student.getAddress().value);
        tags.setText("Tags: " + student.getTags());

        populateMissionSubmissions(student);
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
     */
    private void populateMissionSubmissions(Student student) {
        missionSubmissionsGrid.getChildren().clear();

        addGridLabel(missionSubmissionsGrid, "Week", 0, 0, true);
        addGridLabel(missionSubmissionsGrid, "Status", 1, 0, true);

        MissionSubmissions submissions =
                student.getMissionSubmissions();

        int row = 1;

        for (int week = MissionSubmissions.MIN_WEEK;
             week <= MissionSubmissions.MAX_WEEK;
             week++) {

            String status = submissions.hasSubmission(week)
                    ? "Submitted"
                    : "Not submitted";

            addGridLabel(
                    missionSubmissionsGrid,
                    String.valueOf(week),
                    0,
                    row,
                    false);

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

        if (isHeader) {
            label.getStyleClass().add("grid-header");
        } else {
            label.getStyleClass().add("grid-value");
        }

        GridPane.setHalignment(
                label,
                column == 0
                        ? javafx.geometry.HPos.LEFT
                        : javafx.geometry.HPos.LEFT);

        grid.add(label, column, row);
    }
}
