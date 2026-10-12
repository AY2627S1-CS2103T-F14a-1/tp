package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/** Verifies that invalid saved data cannot become a writable empty database at startup. */
public class MainAppMissionStorageTest {

    @TempDir
    public Path temporaryDirectory;

    @Test
    public void initModelManager_missingCatalogue_stopsWithoutChangingFile() throws Exception {
        Path file = temporaryDirectory.resolve("missingCatalogue.json");
        String original = "{\"students\":[]}";
        Files.writeString(file, original);
        Storage storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(temporaryDirectory.resolve("prefs.json")));
        Method initialize = MainApp.class.getDeclaredMethod("initModelManager", Storage.class, ReadOnlyUserPrefs.class);
        initialize.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () ->
                initialize.invoke(new MainApp(), storage, new UserPrefs()));
        assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertEquals(original, Files.readString(file));
    }
}
