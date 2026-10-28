package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentBuilder.StudioRecordSet;

public class StudentDetailsPanelTest {

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
    public void showStudent_displaysStudentDetails() throws Exception {
        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            Student student = new StudentBuilder()
                    .withName("Amy")
                    .withPhone("91234567")
                    .withEmail("amy@example.com")
                    .withStudioGroup("1A")
                    .build();

            panel.showStudent(student, List.of());

            assertEquals(
                    student.getName().fullName,
                    getLabel(panel, "#studentName").getText());

            assertEquals(
                    "Studio group: "
                            + student.getStudioGroup().studioGroup,
                    getLabel(panel, "#studioGroup").getText());

            assertEquals(
                    "Phone: " + student.getPhone().value,
                    getLabel(panel, "#phone").getText());

            assertEquals(
                    "Email: " + student.getEmail().value,
                    getLabel(panel, "#email").getText());

            assertEquals(
                    "Tags: " + student.getTags(),
                    getLabel(panel, "#tags").getText());

            assertTrue(
                    panel.getRoot()
                            .lookup("#studentDetailsScrollPane")
                            .isVisible());
        });
    }

    @Test
    public void showStudent_displaysMissionStatusAndDeadline()
            throws Exception {

        runOnJavaFxThread(() -> {
            Mission mission = new Mission(
                    new MissionId("M01"),
                    "Runes",
                    LocalDate.of(2026, 10, 20));

            Student student = new StudentBuilder(AMY)
                    .withMissionSubmissions("M01")
                    .build();

            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(student, List.of(mission));

            GridPane missionGrid =
                    getMissionGrid(panel);

            assertEquals(4, missionGrid.getChildren().size());

            Label status =
                    (Label) missionGrid.getChildren().get(3);

            assertEquals("Submitted", status.getText());

            VBox missionDetails =
                    (VBox) missionGrid.getChildren().get(2);

            assertEquals(2, missionDetails.getChildren().size());

            Label missionName =
                    (Label) missionDetails.getChildren().get(0);

            Label deadline =
                    (Label) missionDetails.getChildren().get(1);

            assertEquals(
                    "M01 · Runes",
                    missionName.getText());

            assertEquals(
                    "Due: 2026-10-20",
                    deadline.getText());
        });
    }

    @Test
    public void showStudent_withNoMissions_displaysMessage()
            throws Exception {

        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(
                    new StudentBuilder(AMY).build(),
                    List.of());

            GridPane missionGrid =
                    getMissionGrid(panel);

            assertEquals(1, missionGrid.getChildren().size());

            Label message =
                    (Label) missionGrid.getChildren().get(0);

            assertEquals(
                    "No missions configured.",
                    message.getText());
        });
    }

    @Test
    public void showStudent_nullStudent_clearsPanel()
            throws Exception {

        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(null, List.of());

            assertTrue(
                    panel.getRoot()
                            .lookup("#emptyMessage")
                            .isVisible());

            assertFalse(
                    panel.getRoot()
                            .lookup("#studentDetailsScrollPane")
                            .isVisible());
        });
    }

    @Test
    public void showStudent_studioRecords_displaysAttendanceFromRecordPresence() throws Exception {
        runOnJavaFxThread(() -> {
            Student student = new StudentBuilder(AMY)
                    .withStudioRecords(
                            new StudioRecordSet(3, new StudioRecord(50)),
                            new StudioRecordSet(4, new StudioRecord(0)))
                    .build();
            StudentDetailsPanel panel = new StudentDetailsPanel();

            panel.showStudent(student, List.of());

            GridPane grid = getStudioRecordGrid(panel);
            assertEquals("Week", getGridCell(grid, 0, 0));
            assertEquals("Attendance", getGridCell(grid, 1, 0));
            assertEquals("Effort Score", getGridCell(grid, 2, 0));
            assertEquals("Attended", getGridCell(grid, 1, getStudioRow(3)));
            assertEquals("50", getGridCell(grid, 2, getStudioRow(3)));
            assertEquals("Attended", getGridCell(grid, 1, getStudioRow(4)));
            assertEquals("0", getGridCell(grid, 2, getStudioRow(4)));
            assertEquals("Not attended", getGridCell(grid, 1, getStudioRow(5)));
            assertEquals("0", getGridCell(grid, 2, getStudioRow(5)));
        });
    }

    @Test
    public void showStudent_switchStudents_refreshesStudioAttendance() throws Exception {
        runOnJavaFxThread(() -> {
            Student attendedStudent = new StudentBuilder(AMY)
                    .withStudioRecords(new StudioRecordSet(3, new StudioRecord(50)))
                    .build();
            Student studentWithoutRecords = new StudentBuilder(AMY).withStudioRecords().build();
            StudentDetailsPanel panel = new StudentDetailsPanel();

            panel.showStudent(attendedStudent, List.of());
            GridPane grid = getStudioRecordGrid(panel);
            assertEquals("Attended", getGridCell(grid, 1, getStudioRow(3)));

            panel.showStudent(studentWithoutRecords, List.of());
            assertEquals("Not attended", getGridCell(grid, 1, getStudioRow(3)));

            panel.showStudent(attendedStudent, List.of());
            assertEquals("Attended", getGridCell(grid, 1, getStudioRow(3)));
            int displayedWeeks = RegisteredStudioRecords.MAX_WEEK - RegisteredStudioRecords.MIN_WEEK + 1;
            assertEquals((displayedWeeks + 1) * 3, grid.getChildren().size());
        });
    }

    /**
     * Returns the studio grid row corresponding to the specified week.
     *
     * @param week Studio week whose row is required.
     * @return One-based data-row index for the specified week.
     */
    private static int getStudioRow(int week) {
        return week - RegisteredStudioRecords.MIN_WEEK + 1;
    }

    private static GridPane getMissionGrid(
            StudentDetailsPanel panel) {

        prepareRoot(panel);

        return (GridPane) panel.getRoot()
                .lookup("#missionSubmissionsGrid");
    }

    private static GridPane getStudioRecordGrid(StudentDetailsPanel panel) {
        prepareRoot(panel);
        return (GridPane) panel.getRoot().lookup("#studioRecordGrid");
    }

    private static Label getLabel(
            StudentDetailsPanel panel,
            String selector) {

        prepareRoot(panel);

        return (Label) panel.getRoot().lookup(selector);
    }

    private static void prepareRoot(
            StudentDetailsPanel panel) {

        if (panel.getRoot().getScene() == null) {
            new Scene(panel.getRoot());
        }

        panel.getRoot().applyCss();
        panel.getRoot().layout();
    }

    private static void runOnJavaFxThread(
            Runnable assertions) throws Exception {

        FutureTask<Void> task =
                new FutureTask<>(assertions, null);

        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void showStudent_masteryChecks_displaysSortedNumbersFormattedTimesAndStatuses() throws Exception {
        runOnJavaFxThread(() -> {
            MasteryCheck complete = new MasteryCheck(LocalDateTime.of(2026, 8, 19, 0, 5));
            complete.markComplete();
            Student student = new StudentBuilder(AMY).withMasteryChecks(Map.of(2, complete,
                    1, new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30)))).build();
            StudentDetailsPanel panel = new StudentDetailsPanel();
            panel.showStudent(student, List.of());
            prepareRoot(panel);
            GridPane grid = (GridPane) panel.getRoot().lookup("#masteryChecksGrid");
            assertEquals(9, grid.getChildren().size());
            assertEquals("Number", getMasteryCell(grid, 0, 0));
            assertEquals("Appointment time", getMasteryCell(grid, 1, 0));
            assertEquals("Status", getMasteryCell(grid, 2, 0));
            assertEquals("1", getMasteryCell(grid, 0, 1));
            assertEquals("Aug 18, 14:30", getMasteryCell(grid, 1, 1));
            assertEquals("Not completed", getMasteryCell(grid, 2, 1));
            assertEquals("2", getMasteryCell(grid, 0, 2));
            assertEquals("Aug 19, 00:05", getMasteryCell(grid, 1, 2));
            assertEquals("Completed", getMasteryCell(grid, 2, 2));
        });
    }

    @Test
    public void showStudent_noMasteryChecks_displaysHeadersAndSpanningEmptyMessage() throws Exception {
        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel = new StudentDetailsPanel();
            panel.showStudent(new StudentBuilder(AMY).withMasteryChecks(Map.of()).build(), List.of());
            prepareRoot(panel);
            GridPane grid = (GridPane) panel.getRoot().lookup("#masteryChecksGrid");
            assertEquals(4, grid.getChildren().size());
            assertEquals("Number", getMasteryCell(grid, 0, 0));
            assertEquals("Appointment time", getMasteryCell(grid, 1, 0));
            assertEquals("Status", getMasteryCell(grid, 2, 0));
            assertEquals("No mastery checks registered.", getMasteryCell(grid, 0, 1));
            assertEquals(3, GridPane.getColumnSpan(grid.getChildren().getLast()));
        });
    }

    @Test
    public void showStudent_switchStudentsAndCompletion_clearsOldRowsAndRefreshesStatus() throws Exception {
        runOnJavaFxThread(() -> {
            MasteryCheck check = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
            Student student = new StudentBuilder(AMY).withMasteryChecks(Map.of(1, check)).build();
            Student empty = new StudentBuilder(AMY).withMasteryChecks(Map.of()).build();
            StudentDetailsPanel panel = new StudentDetailsPanel();
            panel.showStudent(student, List.of());
            prepareRoot(panel);
            GridPane grid = (GridPane) panel.getRoot().lookup("#masteryChecksGrid");
            assertEquals("Not completed", getMasteryCell(grid, 2, 1));
            panel.showStudent(empty, List.of());
            assertEquals(4, grid.getChildren().size());
            assertEquals("No mastery checks registered.", getMasteryCell(grid, 0, 1));
            check.markComplete();
            panel.showStudent(student, List.of());
            assertEquals(6, grid.getChildren().size());
            assertEquals("Completed", getMasteryCell(grid, 2, 1));
            panel.showStudent(null, List.of());

            assertTrue(
                    panel.getRoot()
                            .lookup("#studentDetailsScrollPane")
                            .isVisible());

            panel.showStudent(student, List.of());
            prepareRoot(panel);
            assertEquals(6, grid.getChildren().size());
            assertEquals("Completed", getMasteryCell(grid, 2, 1));
        });
    }

    /**
     * Returns the text of a mastery check label at the specified grid coordinates.
     *
     * @param grid Grid containing the labels.
     * @param column Zero-based column index.
     * @param row Zero-based row index.
     * @return Text displayed in the specified grid cell.
     */
    private static String getMasteryCell(GridPane grid, int column, int row) {
        return getGridCell(grid, column, row);
    }

    /**
     * Returns the text of a label at the specified grid coordinates.
     *
     * @param grid Grid containing the labels.
     * @param column Zero-based column index.
     * @param row Zero-based row index.
     * @return Text displayed in the specified grid cell.
     */
    private static String getGridCell(GridPane grid, int column, int row) {
        return grid.getChildren().stream()
                .filter(node -> Integer.valueOf(column).equals(GridPane.getColumnIndex(node))
                        && Integer.valueOf(row).equals(GridPane.getRowIndex(node)))
                .map(node -> ((Label) node).getText())
                .findFirst().orElseThrow();
    }

}
