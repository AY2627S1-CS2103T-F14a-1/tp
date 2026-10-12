package seedu.address.model.student;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s studio group matches any of the given keywords.
 */
public class StudioGroupContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> keywords;

    /**
     * Creates a predicate that matches students whose studio group contains any keyword.
     *
     * @param keywords the studio group keywords to match against
     */
    public StudioGroupContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Tests whether the student's studio group matches any search keyword.
     *
     * @param student student whose studio group is checked
     * @return true if any keyword matches; false otherwise
     */
    @Override
    public boolean test(Student student) {
        return keywords.stream()
                .anyMatch(keyword ->
                        student.getStudioGroup()
                                .equals(new StudioGroup(keyword.toUpperCase(Locale.ROOT))));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudioGroupContainsKeywordsPredicate otherTagContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherTagContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
