package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guitests.guihandles.StatusBarFooterHandle;
import seedu.address.model.AddressBook;

public class StatusBarFooterTest extends GuiUnitTest {

    public static final Path STUB_SAVE_LOCATION = Paths.get("Stub");
    private static final Path RELATIVE_PATH = Paths.get(".");

    private StatusBarFooterHandle statusBarFooterHandle;
    private final AddressBook addressBook = new AddressBook();

    @BeforeEach
    public void setUp() {
        StatusBarFooter statusBarFooter = new StatusBarFooter(STUB_SAVE_LOCATION);
        uiPartExtension.setUiPart(statusBarFooter);

        statusBarFooterHandle = new StatusBarFooterHandle(statusBarFooter.getRoot());
    }

    @Test
    public void display() {
        // after address book is updated
        guiRobot.interact(() -> addressBook.addStudent(ALICE));
        assertStatusBarContent(RELATIVE_PATH.resolve(STUB_SAVE_LOCATION).toString());
    }

    /**
     * Asserts that the save location matches that of {@code expectedSaveLocation}.
     */
    private void assertStatusBarContent(String expectedSaveLocation) {
        assertEquals(expectedSaveLocation, statusBarFooterHandle.getSaveLocation());
    }

}
