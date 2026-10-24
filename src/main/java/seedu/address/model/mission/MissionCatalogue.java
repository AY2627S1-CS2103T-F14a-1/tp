package seedu.address.model.mission;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Owns mission definitions and validates mission IDs.
 */
public final class MissionCatalogue {

    public static final String MESSAGE_DUPLICATE_NAME = "Another mission already uses this name.";
    private final ObservableList<Mission> missions = FXCollections.observableArrayList();
    private final ObservableList<Mission> unmodifiableMissions = FXCollections.unmodifiableObservableList(missions);

    /**
     * Adds a mission with the supplied ID after validating all fields and catalogue constraints.
     *
     * @param id Unique mission ID.
     * @param name Nonblank mission name.
     * @param deadline Submission deadline.
     * @return Newly stored mission.
     * @throws NullPointerException If any argument is null.
     * @throws IllegalArgumentException If the ID or name duplicates another mission, or the name is blank.
     */
    public Mission add(MissionId id, String name, LocalDate deadline) {
        requireNonNull(id);
        Mission mission = new Mission(id, name, deadline);
        checkArgument(find(id).isEmpty(), "Duplicate mission ID: " + id);
        checkArgument(missions.stream().noneMatch(existing -> existing.hasSameName(mission.getName())),
                MESSAGE_DUPLICATE_NAME);
        missions.add(mission);
        return mission;
    }

    /**
     * Replaces saved catalogue state after validating the entire replacement.
     *
     * @param records Missions to restore, with unique IDs and case-insensitively unique names.
     * @throws NullPointerException If the list or a mission is null.
     * @throws IllegalArgumentException If an ID or name is duplicated.
     */
    public void restore(List<Mission> records) {
        List<Mission> copiedRecords = List.copyOf(records);
        Set<MissionId> ids = new HashSet<>();
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Mission mission : copiedRecords) {
            checkArgument(ids.add(mission.getId()), "Duplicate mission ID: " + mission.getId());
            checkArgument(names.add(mission.getName()), MESSAGE_DUPLICATE_NAME);
        }
        missions.setAll(copiedRecords);
    }

    /**
     * Looks up a mission without relying on list positions.
     *
     * @param id Mission identifier.
     * @return Matching mission, if present.
     * @throws NullPointerException If the ID is null.
     */
    public Optional<Mission> find(MissionId id) {
        requireNonNull(id);
        return missions.stream().filter(mission -> mission.getId().equals(id)).findFirst();
    }

    /**
     * Returns a read-only observable view of the catalogue.
     *
     * @return Configured missions.
     */
    public ObservableList<Mission> getMissions() {
        return unmodifiableMissions;
    }

    /**
     * Replaces an existing mission while preserving its ID and list position.
     *
     * @param target Mission to replace.
     * @param editedMission Replacement mission.
     * @throws NullPointerException If either mission is null.
     * @throws IllegalArgumentException If the target does not exist, the ID
     *     changes, or another mission already has the replacement's name.
     */
    public void setMission(Mission target, Mission editedMission) {
        requireAllNonNull(target, editedMission);

        int index = missions.indexOf(target);
        checkArgument(index != -1, "Mission does not exist: " + target.getId());
        checkArgument(target.getId().equals(editedMission.getId()),
                "A mission's ID cannot be changed.");
        boolean hasDuplicateName = missions.stream()
                .anyMatch(mission -> !mission.getId().equals(target.getId())
                        && mission.hasSameName(editedMission.getName()));
        checkArgument(!hasDuplicateName, MESSAGE_DUPLICATE_NAME);

        missions.set(index, editedMission);
    }
}
