package seedu.address.model.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Student[] getSampleStudents() {
        return new Student[] {
            new Student(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                getTagSet("friends"), new StudioGroup("1A")),
            new Student(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                getTagSet("colleagues", "friends"), new StudioGroup("1A")),
            new Student(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                getTagSet("neighbours"), new StudioGroup("1A")),
            new Student(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                getTagSet("family"), new StudioGroup("1A")),
            new Student(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                getTagSet("classmates"), new StudioGroup("1A")),
            new Student(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                getTagSet("colleagues"), new StudioGroup("1A"))
        };
    }

    /**
     * Creates sample students and missions with illustrative deadlines, not an official course schedule.
     * All student submissions start unrecorded. Existing saved address books are loaded separately.
     *
     * @return A fresh address book ready for trying mission submission commands.
     */
    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Student sampleStudent : getSampleStudents()) {
            sampleAb.addStudent(sampleStudent);
        }
        sampleAb.addMission(new seedu.address.model.mission.MissionId("M01"),
                "Understanding the Force", LocalDate.of(2026, 10, 15));
        sampleAb.addMission(new seedu.address.model.mission.MissionId("M02"),
                "Rune Reading", LocalDate.of(2026, 10, 22));
        sampleAb.addMission(new seedu.address.model.mission.MissionId("M03"),
                "Beyond the Second Dimension", LocalDate.of(2026, 10, 29));
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
