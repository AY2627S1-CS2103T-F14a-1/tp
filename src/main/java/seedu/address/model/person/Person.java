package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final StudioGroup studioGroup;
    private final MissionSubmissions missionSubmissions;

    /**
     * Creates a person with no recorded mission submissions.
     *
     * @param name Person's name.
     * @param phone Person's phone number.
     * @param email Person's email address.
     * @param address Person's address.
     * @param tags Person's tags.
     * @throws NullPointerException If any argument is null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, StudioGroup studioGroup) {
        this(name, phone, email, address, tags, studioGroup, new MissionSubmissions());
    }

    /**
     * Creates a person with the given details and mission submissions.
     *
     * @param name Person's name.
     * @param phone Person's phone number.
     * @param email Person's email address.
     * @param address Person's address.
     * @param tags Person's tags.
     * @param missionSubmissions Person's recorded mission submissions.
     * @throws NullPointerException If any argument is null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
            StudioGroup studioGroup, MissionSubmissions missionSubmissions) {
        requireAllNonNull(name, phone, email, address, tags, missionSubmissions);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.studioGroup = studioGroup;
        this.missionSubmissions = missionSubmissions;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public StudioGroup getStudioGroup() {
        return studioGroup;
    }

    /**
     * Returns this person's immutable mission submissions.
     *
     * @return Recorded mission submissions.
     */
    public MissionSubmissions getMissionSubmissions() {
        return missionSubmissions;
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && studioGroup.equals(otherPerson.studioGroup)
                && missionSubmissions.equals(otherPerson.missionSubmissions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, studioGroup, missionSubmissions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("studioGroup", studioGroup)
                .add("missionSubmissions", missionSubmissions)
                .toString();
    }

}
