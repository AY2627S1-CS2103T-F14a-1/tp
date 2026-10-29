package guitests.guihandles;

import javafx.scene.Node;
import javafx.scene.control.Labeled;

/**
 * A handle for the {@code StatusBarFooter} at the footer of the application.
 */
public class StatusBarFooterHandle extends NodeHandle<Node> {

    public static final String STATUS_BAR_PLACEHOLDER = "#statusbarPlaceholder";

    private static final String SAVE_LOCATION_STATUS_ID = "#saveLocationStatus";

    private final Labeled saveLocationNode;

    /**
     * Creates a {@code StatusBarFooterHandle} with the given {@code statusBarFooterNode}.
     */
    public StatusBarFooterHandle(Node statusBarFooterNode) {
        super(statusBarFooterNode);

        saveLocationNode = getChildNode(SAVE_LOCATION_STATUS_ID);
    }

    /**
     * Returns the text of the 'save location' portion of the status bar.
     */
    public String getSaveLocation() {
        return saveLocationNode.getText();
    }
}
