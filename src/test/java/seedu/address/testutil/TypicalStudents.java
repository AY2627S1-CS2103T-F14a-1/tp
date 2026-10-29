package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MATRIC_NUMBER_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.student.Student;

/**
 * A utility class containing a list of {@code Student} objects to be used in tests.
 */
public class TypicalStudents {

    public static final Student ALICE = new StudentBuilder().withMatricNumber("A0000001A").withName("Alice Pauline")
            .withEmail("alice@example.com")
            .withPhone("94351253")
            .withTags("friends").withStudioGroup("1A").build();
    public static final Student BENSON = new StudentBuilder().withMatricNumber("A0000002A").withName("Benson Meier")
            .withEmail("johnd@example.com").withPhone("98765432")
            .withTags("owesMoney", "friends").withStudioGroup("1A").build();
    public static final Student CARL = new StudentBuilder().withMatricNumber("A0000003A")
            .withName("Carl Kurz").withPhone("95352563")
            .withEmail("heinz@example.com").withStudioGroup("1A").build();
    public static final Student DANIEL = new StudentBuilder().withMatricNumber("A0000004A")
            .withName("Daniel Meier").withPhone("87652533")
            .withEmail("cornelia@example.com").withTags("friends")
            .withStudioGroup("1A").build();
    public static final Student ELLE = new StudentBuilder().withMatricNumber("A0000005A")
            .withName("Elle Meyer").withPhone("9482224")
            .withEmail("werner@example.com").withStudioGroup("1A").build();
    public static final Student FIONA = new StudentBuilder().withMatricNumber("A0000006A")
            .withName("Fiona Kunz").withPhone("9482427")
            .withEmail("lydia@example.com").withStudioGroup("1A").build();
    public static final Student GEORGE = new StudentBuilder().withMatricNumber("A0000007A")
            .withName("George Best").withPhone("9482442")
            .withEmail("anna@example.com").withStudioGroup("1A").build();

    // Manually added
    public static final Student HOON = new StudentBuilder().withMatricNumber("A0000008A")
            .withName("Hoon Meier").withPhone("8482424")
            .withEmail("stefan@example.com").withStudioGroup("1A").build();
    public static final Student IDA = new StudentBuilder().withMatricNumber("A0000009A")
            .withName("Ida Mueller").withPhone("8482131")
            .withEmail("hans@example.com").withStudioGroup("1A").build();

    // Manually added - Student's details found in {@code CommandTestUtil}
    public static final Student AMY = new StudentBuilder().withMatricNumber(VALID_MATRIC_NUMBER_AMY)
            .withName(VALID_NAME_AMY).withPhone(VALID_PHONE_AMY)
            .withEmail(VALID_EMAIL_AMY).withTags(VALID_TAG_FRIEND)
            .withStudioGroup("1A").build();
    public static final Student BOB = new StudentBuilder().withMatricNumber(VALID_MATRIC_NUMBER_BOB)
            .withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withEmail(VALID_EMAIL_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .withStudioGroup("2B").build();

    public static final String KEYWORD_MATCHING_MEIER = "Meier"; // A keyword that matches MEIER

    private TypicalStudents() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical students.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Student student : getTypicalStudents()) {
            ab.addStudent(student);
        }
        return ab;
    }

    public static List<Student> getTypicalStudents() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
