package seedu.address.model.student;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s {@code Name} contains all keywords in at least one
 * comma-separated alternative. Matching is case-insensitive and allows partial names.
 */
public class NameContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Tests whether the student's name matches any search alternative.
     * Every word within a matching alternative must occur in the name.
     *
     * @param student Student whose name is searched.
     * @return True if an alternative matches; false if none match.
     */
    @Override
    public boolean test(Student student) {
        String normalizedName = student.getName().fullName.toLowerCase(Locale.ROOT);

        return keywords.stream()
                .filter(alternative -> !alternative.isBlank())
                .anyMatch(alternative -> Arrays.stream(alternative.strip().split("\\s+"))
                        .allMatch(keyword -> normalizedName.contains(keyword.toLowerCase(Locale.ROOT))));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
