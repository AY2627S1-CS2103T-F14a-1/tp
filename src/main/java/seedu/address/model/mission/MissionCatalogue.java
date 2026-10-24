package seedu.address.model.mission;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Owns mission definitions and the persistent allocation counter.
 */
public final class MissionCatalogue {

    public static final String MESSAGE_DUPLICATE_NAME = "Another mission already uses this name.";
    public static final String MESSAGE_IDS_EXHAUSTED = "No further mission IDs can be allocated.";

    private final ObservableList<Mission> missions = FXCollections.observableArrayList();
    private final ObservableList<Mission> unmodifiableMissions = FXCollections.unmodifiableObservableList(missions);
    private long nextMissionNumber = 1;

    /**
     * Allocates a mission ID only after its contents have been validated.
     *
     * @param name Nonblank mission name.
     * @param deadline Submission deadline.
     * @return Newly stored mission.
     * @throws NullPointerException If either argument is null.
     * @throws IllegalArgumentException If the name is blank or duplicates another mission.
     * @throws IllegalStateException If the allocation counter is exhausted.
     */
    public Mission add(String name, LocalDate deadline) {
        if (nextMissionNumber == Long.MAX_VALUE) {
            throw new IllegalStateException(MESSAGE_IDS_EXHAUSTED);
        }
        MissionId id = new MissionId(String.format(Locale.ROOT, "M%02d", nextMissionNumber));
        Mission mission = new Mission(id, name, deadline);
        checkArgument(missions.stream().noneMatch(existing -> existing.hasSameName(mission.getName())),
                MESSAGE_DUPLICATE_NAME);
        nextMissionNumber++;
        missions.add(mission);
        return mission;
    }

    /**
     * Replaces saved catalogue state after validating the entire replacement.
     *
     * @param records Missions to restore, with unique IDs and case-insensitively unique names.
     * @param nextNumber Allocation counter greater than every existing ID number.
     * @throws NullPointerException If the list or a mission is null.
     * @throws IllegalArgumentException If an ID or name is duplicated, or the counter is invalid.
     */
    public void restore(List<Mission> records, long nextNumber) {
        List<Mission> copiedRecords = List.copyOf(records);
        checkArgument(nextNumber > 0, "Next mission number must be positive.");
        Set<MissionId> ids = new HashSet<>();
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Mission mission : copiedRecords) {
            checkArgument(ids.add(mission.getId()), "Duplicate mission ID: " + mission.getId());
            checkArgument(names.add(mission.getName()), MESSAGE_DUPLICATE_NAME);
            checkArgument(mission.getId().getNumber() < nextNumber,
                    "Next mission number must be greater than every saved mission ID.");
        }
        nextMissionNumber = nextNumber;
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
     * Returns the next unused number, which must also be saved when the catalogue is empty.
     *
     * @return Persistent allocation counter.
     */
    public long getNextMissionNumber() {
        return nextMissionNumber;
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
