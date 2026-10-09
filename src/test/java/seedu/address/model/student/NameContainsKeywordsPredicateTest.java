package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.StudentBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different student -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new StudentBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new StudentBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new StudentBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new StudentBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void test_partialKeyword_returnsTrue() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of("ale")).test(student));
    }

    @Test
    public void test_onlyOnePartialKeywordMatches_returnsTrue() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of("ale", "david")).test(student));
    }

    @Test
    public void test_noPartialKeywordsMatch_returnsFalse() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertFalse(new NameContainsKeywordsPredicate(List.of("dav", "lim")).test(student));
    }

    @Test
    public void test_mixedCasePartialKeyword_returnsTrue() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of("aLE", "DAVID")).test(student));
    }

    @Test
    public void test_keywordOrderChanged_returnsSameResult() {
        Student student = new StudentBuilder().withName("Alex Tan").build();
        NameContainsKeywordsPredicate firstPredicate =
                new NameContainsKeywordsPredicate(List.of("ale", "david"));
        NameContainsKeywordsPredicate secondPredicate =
                new NameContainsKeywordsPredicate(List.of("david", "ale"));

        assertTrue(firstPredicate.test(student));
        assertEquals(firstPredicate.test(student), secondPredicate.test(student));
    }

    @Test
    public void test_repeatedKeywords_returnsTrue() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertTrue(new NameContainsKeywordsPredicate(List.of("ale", "ale")).test(student));
    }

    @Test
    public void test_unusualCharacters_returnsFalse() {
        Student student = new StudentBuilder().withName("Alex Tan").build();

        assertFalse(new NameContainsKeywordsPredicate(List.of("@#$")).test(student));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
