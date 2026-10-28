package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;

/**
 * Lists configured missions without changing student filters or records.
 */
public class ListMissionCatalogueCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Configured missions:\n%1$s";
    public static final String MESSAGE_NO_MISSIONS = "No missions configured.";

    /**
     * Lists all configured missions in ascending numeric ID order.
     *
     * @param model Model containing the mission catalogue.
     * @return Mission details, or feedback explaining that the catalogue is empty.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        String missionRows = model.getMissionList().stream()
                .sorted(Comparator.comparingLong((Mission mission) -> mission.getId().getNumber()))
                .map(Messages::format)
                .collect(Collectors.joining("\n"));

        if (missionRows.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MISSIONS);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, missionRows));
    }
}
