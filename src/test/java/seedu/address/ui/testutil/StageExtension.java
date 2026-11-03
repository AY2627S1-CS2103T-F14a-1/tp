package seedu.address.ui.testutil;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testfx.api.FxToolkit;

/**
 * Properly sets up and tears down a JavaFx stage for our testing purposes.
 */
public class StageExtension implements BeforeEachCallback, AfterEachCallback {

    //@@author nuggettowin-reused
    //Reused from AB4
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        FxToolkit.registerPrimaryStage();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        FxToolkit.cleanupStages();
    }
    //@@author
}
