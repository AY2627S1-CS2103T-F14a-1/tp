package seedu.address.ui;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Student;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;

    // Independent Ui parts residing in this Ui container
    private StudentListPanel studentListPanel;
    private StudentDetailsPanel studentDetailsPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane studentListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane studentDetailsPanelPlaceholder;

    @FXML
    private Label lastCommandLabel;

    @FXML
    private SplitPane mainSplitPane;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        mainSplitPane.setDividerPositions(0.30);

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    /**
     * Returns the primary application stage.
     *
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Registers keyboard shortcuts for menu actions.
     */
    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Returns the student details panel displayed in the main window.
     *
     * @return the student details panel
     */
    StudentDetailsPanel getStudentDetailsPanel() {
        return studentDetailsPanel;
    }

    /**
     * Assigns a keyboard shortcut to a menu item.
     *
     * <p>An event filter is used because text input controls may consume
     * keyboard events before menu accelerators receive them.</p>
     *
     * @param menuItem menu item that should be triggered
     * @param keyCombination keyboard shortcut assigned to the menu item
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Creates and inserts all UI components into their FXML placeholders.
     */
    void fillInnerParts() {
        studentListPanel = new StudentListPanel(
                logic.getFilteredStudentList());

        Region studentListRoot = studentListPanel.getRoot();

        studentListRoot.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        studentListPanelPlaceholder.getChildren()
                .add(studentListRoot);

        studentDetailsPanel = new StudentDetailsPanel();

        Region studentDetailsRoot =
                studentDetailsPanel.getRoot();

        studentDetailsRoot.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE);

        studentDetailsRoot.prefWidthProperty().bind(
                studentDetailsPanelPlaceholder.widthProperty());

        studentDetailsRoot.prefHeightProperty().bind(
                studentDetailsPanelPlaceholder.heightProperty());

        StackPane.setAlignment(
                studentDetailsRoot,
                Pos.TOP_LEFT);

        ObservableList<Student> displayedStudents =
                logic.getFilteredStudentList();

        displayedStudents.addListener(
                (ListChangeListener<Student>) change -> {
                    Student displayedStudent =
                            studentDetailsPanel.getDisplayedStudent();

                    if (displayedStudent == null) {
                        return;
                    }

                    Student updatedStudent = displayedStudents.stream()
                            .filter(student ->
                                    student.getName().equals(
                                            displayedStudent.getName()))
                            .findFirst()
                            .orElse(null);

                    if (updatedStudent == null) {
                        studentDetailsPanel.clearStudent();
                        Platform.runLater(() ->
                                studentListPanel.getStudentListView()
                                        .getSelectionModel()
                                        .clearSelection());
                    } else if (updatedStudent != displayedStudent) {
                        studentDetailsPanel.showStudent(
                                updatedStudent,
                                logic.getMissions());
                    }
                });

        studentDetailsPanelPlaceholder.getChildren()
                .add(studentDetailsRoot);

        studentListPanel.getStudentListView()
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldStudent, newStudent) -> {
                    if (newStudent != null) {
                        studentDetailsPanel.showStudent(
                                newStudent,
                                logic.getMissions());
                    }
                });

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren()
                .add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter =
                new StatusBarFooter(dataFilePath);

        statusbarPlaceholder.getChildren()
                .add(statusBarFooter.getRoot());

        CommandBox commandBox =
                new CommandBox(this::executeCommand);

        commandBoxPlaceholder.getChildren()
                .add(commandBox.getRoot());
    }

    /**
     * Applies the saved GUI settings to the primary stage.
     *
     * @param guiSettings GUI settings containing size and position
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses it if already open.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Displays the primary application window.
     */
    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public StudentListPanel getStudentListPanel() {
        return studentListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            lastCommandLabel.setText(commandText);
            CommandResult commandResult = logic.execute(commandText);

            logger.info(
                    "Result: " + commandResult.getFeedbackToUser());

            handleCommandResult(commandResult);

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    /**
     * Updates the user interface using the result of an executed command.
     *
     * <p>Displays command feedback and warnings, refreshes mission information
     * when the mission catalogue has changed, and displays the returned student
     * if that student remains in the current filtered list. If the student is no
     * longer visible, the details panel and list selection are cleared.</p>
     *
     * @param commandResult result returned after executing a command
     */
    void handleCommandResult(CommandResult commandResult) {
        List<String> warnings = commandResult.getWarnings();

        resultDisplay.setFeedbackToUser(
                commandResult.getFeedbackToUser(),
                warnings);

        if (commandResult.isMissionUpdated()) {
            studentDetailsPanel.updateMissions(
                    commandResult.getMissions());
        }

        commandResult.getStudentToView().ifPresent(student -> {
            boolean studentStillVisible =
                    logic.getFilteredStudentList()
                            .stream()
                            .anyMatch(filteredStudent ->
                                    filteredStudent.getName().equals(
                                            student.getName()));

            if (studentStillVisible) {
                studentListPanel.getStudentListView()
                        .getSelectionModel()
                        .select(student);

                studentDetailsPanel.showStudent(
                        student,
                        logic.getMissions());
            } else {
                studentDetailsPanel.clearStudent();

                studentListPanel.getStudentListView()
                        .getSelectionModel()
                        .clearSelection();
            }
        });
    }
}
