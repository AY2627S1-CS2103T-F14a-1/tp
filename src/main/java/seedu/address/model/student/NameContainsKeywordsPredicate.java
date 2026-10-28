package seedu.address.model.student;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s {@code Name} contains all keywords in at least one
 * comma-separated alternative. Matching is case-insensitive and allows partial names.
 * Repeated keywords within an alternative require separate, non-overlapping occurrences.
 */
public class NameContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> keywords;

    public NameContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Tests whether the student's name matches any search alternative.
     *
     * @param student Student whose name is searched.
     * @return True if every keyword in at least one alternative is satisfied.
     */
    @Override
    public boolean test(Student student) {
        String normalizedName = student.getName().fullName.toLowerCase(Locale.ROOT);

        return keywords.stream()
                .filter(alternative -> !alternative.isBlank())
                .anyMatch(alternative -> matchesAlternative(normalizedName, alternative));
    }

    /**
     * Checks all keywords while requiring separate occurrences of repeated keywords.
     * Different keywords are matched independently to preserve partial-name matching.
     *
     * @param normalizedName Student name already converted to lowercase.
     * @param alternative Search alternative containing whitespace-separated keywords.
     * @return True if the name contains enough occurrences of every keyword.
     */
    private boolean matchesAlternative(String normalizedName, String alternative) {
        Map<String, Integer> nextSearchIndexes = new HashMap<>();

        for (String keyword : alternative.strip().toLowerCase(Locale.ROOT).split("\\s+")) {
            int searchIndex = nextSearchIndexes.getOrDefault(keyword, 0);
            int matchIndex = normalizedName.indexOf(keyword, searchIndex);
            if (matchIndex == -1) {
                return false;
            }

            nextSearchIndexes.put(keyword, matchIndex + keyword.length());
        }

        return true;
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
