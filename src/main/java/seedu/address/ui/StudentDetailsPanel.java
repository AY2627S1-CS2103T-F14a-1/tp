package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;

/**
 * UI panel that displays the selected student's profile, attendance,
 * notes, mission-submission information, and mastery check appointments.
 */
public class StudentDetailsPanel extends UiPart<Region> {

    private static final String FXML = "StudentDetailsPanel.fxml";
    private static final DateTimeFormatter APPOINTMENT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH);

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
    private GridPane notesGrid;

    @FXML
    private GridPane missionSubmissionsGrid;

    @FXML
    private GridPane masteryChecksGrid;

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
     * Displays the profile and records of the specified student.
     *
     * <p>If {@code student} is {@code null}, the panel is reset to its
     * default empty state.</p>
     *
     * @param student student whose profile should be displayed
     * @param missions missions used to display submission statuses
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

        String studentTags = student.getTags().stream()
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));

        studentName.setText(student.getName().fullName);
        studioGroup.setText(
                "Studio group: " + student.getStudioGroup().studioGroup);
        phone.setText("Phone: " + student.getPhone().value);
        email.setText("Email: " + student.getEmail().value);
        tags.setText("Tags: " + (studentTags.isEmpty() ? "-" : studentTags));

        populateNotes(student);
        populateMissionSubmissions(student, missions);
        populateStudioRecord(student);
        populateMasteryChecks(student);
    }

    /**
     * Populates the notes grid with notes in ascending note index order.
     * Displays an empty-state message when the student has no notes.
     *
     * @param student Student whose notes are displayed.
     */
    private void populateNotes(Student student) {
        notesGrid.getChildren().clear();
        addGridLabel(notesGrid, "Index", 0, 0, true);
        addGridLabel(notesGrid, "Note", 1, 0, true);

        List<Note> notes = student.getRegisteredNotes().getRegisteredNotes();
        if (notes.isEmpty()) {
            addGridLabel(notesGrid, "No notes recorded.", 0, 1, false);
            GridPane.setColumnSpan(notesGrid.getChildren().getLast(), 2);
            return;
        }

        int row = 1;
        for (int i = 1; i <= notes.size(); i++) {
            addGridLabel(notesGrid, String.valueOf(i), 0, row, false);
            addGridLabel(notesGrid, notes.get(i - 1).getNoteText(), 1, row, false);
            row++;
        }
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
     * Populates the mission-submission grid.
     *
     * <p>Each mission is displayed with its name, deadline, and submission
     * status for the specified student.</p>
     *
     * @param student student whose submissions are checked
     * @param missions missions to display
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
     * Populates the studio-record grid for the specified student.
     *
     * <p>Each supported week is displayed with its attendance and corresponding
     * effort score. A week is attended when a record exists. Otherwise, it is
     * not attended and has a displayed score of zero.</p>
     *
     * @param student student whose studio records are displayed
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
                "Attendance",
                1,
                0,
                true);

        addGridLabel(
                studioRecordGrid,
                "Effort Score",
                2,
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
                    hasAttended ? "Attended" : "Not attended",
                    1,
                    row,
                    false);

            addGridLabel(
                    studioRecordGrid,
                    String.valueOf(score),
                    2,
                    row,
                    false);

            row++;
        }
    }

    /**
     * Populates the registered mastery checks grid with appointments in ascending registration-number order.
     * Rows show the number, appointment time in English MMM d, HH:mm format, and completion status.
     * Displays an empty-state message below the headers when the student has no registered appointments.
     *
     * @param student Student whose mastery check appointments are displayed.
     */
    private void populateMasteryChecks(Student student) {
        masteryChecksGrid.getChildren().clear();
        addGridLabel(masteryChecksGrid, "Number", 0, 0, true);
        addGridLabel(masteryChecksGrid, "Appointment time", 1, 0, true);
        addGridLabel(masteryChecksGrid, "Status", 2, 0, true);

        Map<Integer, MasteryCheck> appointments = student.getRegisteredMasteryChecks().getMasteryCheckAppointments();
        if (appointments.isEmpty()) {
            addGridLabel(masteryChecksGrid, "No mastery checks registered.", 0, 1, false);
            GridPane.setColumnSpan(masteryChecksGrid.getChildren().getLast(), 3);
            return;
        }

        int row = 1;
        for (Map.Entry<Integer, MasteryCheck> entry : appointments.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).toList()) {
            MasteryCheck check = entry.getValue();
            addGridLabel(masteryChecksGrid, String.valueOf(entry.getKey()), 0, row, false);
            addGridLabel(masteryChecksGrid,
                    check.getAppointmentTime().format(APPOINTMENT_TIME_FORMATTER), 1, row, false);
            addGridLabel(masteryChecksGrid, check.isComplete() ? "Completed" : "Not completed", 2, row, false);
            row++;
        }
    }

    /**
     * Creates and adds a styled label to a grid cell.
     *
     * @param grid grid to which the label is added
     * @param text text displayed by the label
     * @param column zero-based column index
     * @param row zero-based row index
     * @param isHeader whether the label is a table header
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

    /**
     * Creates and adds a mission-information cell to the mission grid.
     *
     * <p>The cell contains the mission name and deadline.</p>
     *
     * @param grid grid to which the cell is added
     * @param mission mission whose information is displayed
     * @param row zero-based row index
     */
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
