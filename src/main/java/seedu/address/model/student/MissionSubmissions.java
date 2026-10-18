package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import seedu.address.model.mission.MissionId;

/**
 * Represents an immutable collection of a student's submitted mission IDs.
 */
public final class MissionSubmissions {

    private final Set<MissionId> submittedMissionIds;

    /**
     * Creates an empty collection of mission submissions.
     */
    public MissionSubmissions() {
        submittedMissionIds = Collections.emptySet();
    }

    /**
     * Creates mission submissions from the given mission IDs.
     *
     * @param submissionIds Mission IDs with recorded submissions.
     * @throws NullPointerException If the set or any of its elements is null.
     */
    public MissionSubmissions(Set<MissionId> submissionIds) {
        requireNonNull(submissionIds);

        Set<MissionId> copiedIds = new HashSet<>(submissionIds);
        for (MissionId missionId : copiedIds) {
            requireNonNull(missionId);
        }
        submittedMissionIds = Collections.unmodifiableSet(copiedIds);
    }

    /**
     * Returns whether a submission has been recorded for the given mission ID.
     *
     * @param missionId Mission ID to check.
     * @return True if a submission is recorded for the mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public boolean hasSubmission(MissionId missionId) {
        requireNonNull(missionId);
        return submittedMissionIds.contains(missionId);
    }

    /**
     * Returns mission submissions including the given mission ID without changing
     * this object. Returns this object if the mission ID is already recorded.
     *
     * @param missionId Mission ID to record.
     * @return Mission submissions containing the existing mission IDs and given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public MissionSubmissions withSubmission(MissionId missionId) {
        if (hasSubmission(missionId)) {
            return this;
        }

        Set<MissionId> updatedIds = new HashSet<>(submittedMissionIds);
        updatedIds.add(missionId);
        return new MissionSubmissions(updatedIds);
    }

    /**
     * Returns submissions excluding the given mission ID without modifying this object.
     * Returns this object if the mission ID is not recorded.
     *
     * @param missionId Mission ID to remove.
     * @return Submissions containing all existing mission IDs except the given mission ID.
     * @throws NullPointerException If the mission ID is null.
     */
    public MissionSubmissions withoutSubmission(MissionId missionId) {
        if (!hasSubmission(missionId)) {
            return this;
        }

        Set<MissionId> updatedIds = new HashSet<>(submittedMissionIds);
        updatedIds.remove(missionId);
        return new MissionSubmissions(updatedIds);
    }

    /**
     * Returns an unmodifiable set of recorded mission IDs.
     *
     * @return Recorded mission IDs, with no guaranteed iteration order.
     */
    public Set<MissionId> getSubmittedMissionIds() {
        return submittedMissionIds;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MissionSubmissions otherSubmissions)) {
            return false;
        }

        return submittedMissionIds.equals(otherSubmissions.submittedMissionIds);
    }

    @Override
    public int hashCode() {
        return submittedMissionIds.hashCode();
    }

    /**
     * Returns the submitted mission IDs in ascending order for diagnostic output.
     *
     * @return A string containing the recorded mission IDs in ascending order.
     */
    @Override
    public String toString() {
        return submittedMissionIds.stream().sorted(Comparator.comparingLong(MissionId::getNumber))
                .map(MissionId::toString).toList().toString();
    }

}
