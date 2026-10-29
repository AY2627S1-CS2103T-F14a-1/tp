package guitests.guihandles;

import javafx.stage.Stage;
import seedu.address.ui.StudentDetailsPanel;

/**
 * Provides a handle for {@code MainWindow}.
 */
public class MainWindowHandle extends StageHandle {

    private final StudentListPanelHandle personListPanel;
    private final ResultDisplayHandle resultDisplay;
    private final CommandBoxHandle commandBox;
    private final StatusBarFooterHandle statusBarFooter;
    private final MainMenuHandle mainMenu;
    private final StudentDetailsPanelHandle studentDetailsPanel;

    public MainWindowHandle(Stage stage) {
        super(stage);

        personListPanel = new StudentListPanelHandle(getChildNode(StudentListPanelHandle.STUDENT_LIST_VIEW_ID));
        resultDisplay = new ResultDisplayHandle(getChildNode(ResultDisplayHandle.RESULT_DISPLAY_ID));
        commandBox = new CommandBoxHandle(getChildNode(CommandBoxHandle.COMMAND_INPUT_FIELD_ID));
        statusBarFooter = new StatusBarFooterHandle(getChildNode(StatusBarFooterHandle.STATUS_BAR_PLACEHOLDER));
        mainMenu = new MainMenuHandle(getChildNode(MainMenuHandle.MENU_BAR_ID));
        studentDetailsPanel = new StudentDetailsPanelHandle(getChildNode(StudentDetailsPanelHandle.STUDENT_DETAILS_PANEL_ID));
    }

    public StudentListPanelHandle getStudentListPanel() {
        return personListPanel;
    }

    public ResultDisplayHandle getResultDisplay() {
        return resultDisplay;
    }

    public CommandBoxHandle getCommandBox() {
        return commandBox;
    }

    public StatusBarFooterHandle getStatusBarFooter() {
        return statusBarFooter;
    }

    public MainMenuHandle getMainMenu() {
        return mainMenu;
    }
}
