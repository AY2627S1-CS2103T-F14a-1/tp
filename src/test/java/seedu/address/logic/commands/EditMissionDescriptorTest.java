package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditMissionCommand.EditMissionDescriptor;

/**
 * Tests mission-edit field selection, copying, equality, and diagnostics.
 */
public class EditMissionDescriptorTest {

    private static final LocalDate DEADLINE = LocalDate.of(2026, 10, 22);

    @Test
    public void constructor_emptyDescriptor_hasNoReplacementFields() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();

        assertFalse(descriptor.isAnyFieldEdited());
        assertEquals(Optional.empty(), descriptor.getName());
        assertEquals(Optional.empty(), descriptor.getDeadline());
    }

    @Test
    public void isAnyFieldEdited_singleFieldDescriptors_reportsSuppliedFields() {
        EditMissionDescriptor nameDescriptor = new EditMissionDescriptor();
        nameDescriptor.setName("Runes");

        assertTrue(nameDescriptor.isAnyFieldEdited());
        assertEquals(Optional.of("Runes"), nameDescriptor.getName());
        assertEquals(Optional.empty(), nameDescriptor.getDeadline());

        EditMissionDescriptor deadlineDescriptor = new EditMissionDescriptor();
        deadlineDescriptor.setDeadline(DEADLINE);

        assertTrue(deadlineDescriptor.isAnyFieldEdited());
        assertEquals(Optional.empty(), deadlineDescriptor.getName());
        assertEquals(Optional.of(DEADLINE), deadlineDescriptor.getDeadline());
    }

    @Test
    public void constructor_originalChanged_preservesCopiedFields() {
        EditMissionDescriptor original = new EditMissionDescriptor();
        original.setName("Runes");
        original.setDeadline(DEADLINE);
        EditMissionDescriptor copy = new EditMissionDescriptor(original);

        original.setName("Curves");
        original.setDeadline(DEADLINE.plusDays(1));

        assertEquals(Optional.of("Runes"), copy.getName());
        assertEquals(Optional.of(DEADLINE), copy.getDeadline());
    }

    @Test
    public void constructor_emptyDescriptorCopy_preservesAbsentFields() {
        EditMissionDescriptor original = new EditMissionDescriptor();
        EditMissionDescriptor copy = new EditMissionDescriptor(original);

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertFalse(copy.isAnyFieldEdited());
        assertEquals(Optional.empty(), copy.getName());
        assertEquals(Optional.empty(), copy.getDeadline());
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new EditMissionDescriptor(null));
    }

    @Test
    public void setFields_nullValues_preservesExistingFields() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("Runes");
        descriptor.setDeadline(DEADLINE);

        assertThrows(NullPointerException.class, () -> descriptor.setName(null));
        assertEquals(Optional.of("Runes"), descriptor.getName());
        assertEquals(Optional.of(DEADLINE), descriptor.getDeadline());

        assertThrows(NullPointerException.class, () -> descriptor.setDeadline(null));
        assertEquals(Optional.of("Runes"), descriptor.getName());
        assertEquals(Optional.of(DEADLINE), descriptor.getDeadline());
    }

    @Test
    public void equals_sameAndDifferentFields_comparesBothReplacementFields() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("Runes");
        descriptor.setDeadline(DEADLINE);
        EditMissionDescriptor copy = new EditMissionDescriptor(descriptor);

        assertEquals(descriptor, descriptor);
        assertEquals(descriptor, copy);
        assertEquals(copy, descriptor);
        assertEquals(descriptor.hashCode(), copy.hashCode());
        assertNotEquals(descriptor, null);
        assertNotEquals(descriptor, "Runes");
        assertNotEquals(descriptor, new EditMissionDescriptor());

        copy.setDeadline(DEADLINE.plusDays(1));
        assertNotEquals(descriptor, copy);

        copy.setDeadline(DEADLINE);
        copy.setName("Curves");
        assertNotEquals(descriptor, copy);
    }

    @Test
    public void toString_differentReplacementFields_distinguishesRequestedEdits() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("Runes");
        descriptor.setDeadline(DEADLINE);
        String description = descriptor.toString();

        EditMissionDescriptor differentName = new EditMissionDescriptor(descriptor);
        differentName.setName("Curves");
        assertNotEquals(description, differentName.toString());

        EditMissionDescriptor differentDeadline = new EditMissionDescriptor(descriptor);
        differentDeadline.setDeadline(DEADLINE.plusDays(1));
        assertNotEquals(description, differentDeadline.toString());

        assertNotEquals(description, new EditMissionDescriptor().toString());
    }
}
