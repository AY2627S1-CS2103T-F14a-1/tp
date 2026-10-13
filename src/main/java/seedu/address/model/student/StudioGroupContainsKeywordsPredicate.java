package seedu.address.model.student;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s studio group matches any of the given keywords.
 */
public class StudioGroupContainsKeywordsPredicate implements Predicate<Student> {
    private final List<StudioGroup> studioGroups;

    /**
     * Creates a predicate that matches students whose studio group contains any keyword.
     *
     * @param studioGroups the studio groups to match against
     */
    public StudioGroupContainsKeywordsPredicate(
            List<StudioGroup> studioGroups) {
        this.studioGroups = studioGroups;
    }

    /**
     * Tests whether the student's studio group matches any search keyword.
     *
     * @param student student whose studio group is checked
     * @return true if any keyword matches; false otherwise
     */
    @Override
    public boolean test(Student student) {
        return studioGroups.stream()
                .anyMatch(group ->
                        student.getStudioGroup().equals(group));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudioGroupContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return studioGroups.equals(otherPredicate.studioGroups);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studioGroups", studioGroups)
                .toString();
    }
}
