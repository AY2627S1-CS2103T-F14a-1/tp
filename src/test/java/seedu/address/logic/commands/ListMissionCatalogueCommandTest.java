package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests catalogue feedback, numeric ordering, and preservation of model state.
 */
public class ListMissionCatalogueCommandTest {

    @Test
    public void execute_emptyCatalogue_reportsNoMissions() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(), model,
                "No missions configured.", expectedModel);
    }

    @Test
    public void execute_unsortedCatalogue_listsMissionsInNumericIdOrder() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M10"), "Curves", LocalDate.of(2026, 10, 21));
        model.addMission(new MissionId("M02"), "Runes", LocalDate.of(2026, 10, 20));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionCatalogueCommand(), model,
                "Configured missions:\n"
                        + "M02 · Runes (Due: 2026-10-20)\n"
                        + "M10 · Curves (Due: 2026-10-21)",
                expectedModel);
    }

    @Test
    public void execute_filteredStudents_listsCatalogueAndPreservesFilter() {
        Model model = new ModelManager();
        model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 20));
        Student john = new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.addStudent(new StudentBuilder().withName("Betsy Crowe").build());
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListMissionCatalogueCommand(), model,
                "Configured missions:\nM01 · Runes (Due: 2026-10-20)",
                expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionCatalogueCommand().execute(null));
    }
}
