---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AvengerHub Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org)._

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `view id/1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts: `CommandBox`, `ResultDisplay`, `StudentListPanel`, `StudentDetailsPanel`, `StatusBarFooter`, and `HelpWindow`. These classes inherit from `UiPart`, which loads their JavaFX layouts.

`MainWindow` connects the student list, command feedback, and student details. Executing `view id/INDEX` displays the requested student's profile and mission statuses in `StudentDetailsPanel`, using the index in the currently displayed student list.

`ResultDisplay` presents command feedback, including submission and overdue reports, in a scrollable area. `listmissions` and `listoverdue` produce textual feedback without replacing the current student filter. `viewmission` replaces the displayed student list with its query results and displays the selected mission's submission statuses in `ResultDisplay`. Student details use model objects and the mission catalogue supplied through the logic component.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on model classes because it displays students, mission definitions, and their recorded statuses.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delstudent id/1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delstudent id/1` Command" />

<box type="info" seamless>

**Note:** The following can be generalized for all sequence diagrams in the developer guide. The diagrams show successful paths through Logic, omitting validation branches and internal model operations. The parser destroy markers (X) indicate that the temporary parsers are no longer needed after parsing, rather than explicit destruction in Java. PlantUML may continue drawing a parser lifeline below its destroy marker. The common saving step described below is also omitted from the diagrams.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteStudentCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteStudentCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a student).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddStudentCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddStudentCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddStudentCommandParser` and `DeleteStudentCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

The studio-record parsers share argument-structure validation through `ArgumentStructureValidator` and value parsing through `ParserUtil`. They use the same `parseWeek(...)` helper, while the add and edit parsers also use `parseEffortScore(...)`. The following simplified class diagram shows the classes used by `AddStudioRecordCommandParser`:

<puml src="diagrams/AddStudioRecordParserClasses.puml" width="600"/>

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="850" />

The `Model` component,

* stores the address book data, including all `Student` objects (contained in a `UniqueStudentList`) and shared `Mission` objects (contained in a `MissionCatalogue`).
* stores each student's contact details, studio group, tags, studio records,
  notes, mastery check appointments, and mission submissions. Studio records
  track attendance and effort scores by week, while `MissionSubmissions`
  records the IDs of submitted missions. A mission without a recorded
  submission is treated as not submitted.
* stores each mission's user-supplied ID, name, and deadline. The mission catalogue validates IDs and does not allocate them.
* maintains a _filtered_ view of the students, such as search results. It exposes this view as an unmodifiable `ObservableList<Student>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user's preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components).

Each student has one `RegisteredStudioRecords` object containing an immutable
map from studio weeks 0 through 13 to `StudioRecord` objects. Each recorded
week has one record containing a non-negative effort score. The week is the map
key; the presence of a record indicates attendance, even when its effort score
is zero. Weeks without a record are displayed as absent with an effort score
of zero. Each student also has one `RegisteredNotes` object containing notes
keyed by IDs from 1 through 10, one `RegisteredMasteryChecks` object
containing numbered mastery check appointments, and one `MissionSubmissions`
object containing zero or more submitted mission IDs. The `1` multiplicities
count these containers, not the records inside them.

Composition diamonds show conceptual ownership of the values that form a student record; immutable values may be reused internally when records are replaced. Submission IDs reference shared missions rather than owning them.

<box type="info" seamless>

**Note:** The diagram below illustrates a possible alternative for managing tags. Currently, each `Student` stores its own set of tags, and `AddressBook` has no `UniqueTagList`. In the alternative, `AddressBook` would store one `Tag` object per unique tag in a central `UniqueTagList`, and students would reference those shared tags. Student details, including registered notes, are shown to illustrate the surrounding model; only tag ownership differs from the current design. The mission catalogue is omitted for clarity.

<puml src="diagrams/BetterModelClassDiagram.puml" width="800" />
</box>

### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

The component a developer is most likely to interact with is `JsonAdaptedStudent`, which governs the actual conversion of student data between JSON and `Model`. Hence, when adding a new data field to `Student`, its JSON storage format and the validation when loading must be specified.

We illustrate the involvement of `JsonAdaptedStudent` in both load and save scenarios as such:

<puml src="diagrams/LoadAdaptedStudentActivityDiagram.puml" width="800" />
<puml src="diagrams/SaveAdaptedStudentActivityDiagram.puml" width="600" />

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Adding a mission

For `addmission m/MISSION_ID n/NAME d/DEADLINE`, `AddressBookParser` delegates to `AddMissionCommandParser`, which validates exactly one mission ID, name, and deadline and creates an `AddMissionCommand`. `MissionParserUtil#parseMissionId(...)` validates the ID with `MissionId`: it must start with `M`, contain a positive integer of at least two digits with no unnecessary leading zeros, and its numeric value must not exceed `9223372036854775807` (`Long.MAX_VALUE`). IDs with larger numeric values receive the mission-ID constraint message. `MissionParserUtil#parseMissionName(...)` removes surrounding whitespace and collapses repeated whitespace in the name, while preserving case and punctuation. The deadline must be a real date in `YYYY-MM-DD` format; past dates are accepted. Missing, repeated, blank, or invalid fields and unexpected preamble text cause a `ParseException`.

When executed, `AddMissionCommand` checks for an existing ID and calls `Model#hasMissionName(...)` to reject duplicate names, ignoring case and whitespace differences. It then calls `Model#addMission(...)`, which delegates through `ModelManager` and `AddressBook` to `MissionCatalogue`. The catalogue validates uniqueness and stores the supplied ID; it never renumbers missions. The command returns a `CommandResult` containing the added mission.

`MissionCatalogue` also enforces unique IDs and names when adding, editing, and restoring saved data,
so callers cannot bypass this rule by accessing the model directly. Restoration validates the
complete replacement before changing catalogue state. Mission IDs are supplied by the user and
stored with each mission; there is no allocation counter. Deletion leaves all remaining IDs unchanged
and removes the deleted ID from every student's submissions.

Mission names share `Mission.normalizeName(...)` for whitespace normalization. Delete and submission-list
parsers both use `MissionParserUtil#parseMissionId(...)`. Marking, unmarking, and mission deletion use
`Student#withMissionSubmissions(...)` to preserve all other student fields. Common command feedback is
defined in `Messages`.

The sequence diagram below illustrates the successful interaction flow using `execute("addmission m/M01 n/Rune Reading d/2026-10-20")`. Here, `missionId` is the parsed `MissionId`, `deadline` is the parsed `LocalDate` value for `2026-10-20`, and `feedback` is the formatted success message.

<puml src="diagrams/AddMissionSequenceDiagram.puml" width="1000" />

### Deleting a mission

For `delmission m/M03`, `AddressBookParser` delegates to `DeleteMissionCommandParser`. The parser requires exactly one nonblank `m/` value and rejects unexpected preamble text. `MissionParserUtil#parseMissionId(...)` trims and validates the ID, and `MissionId` normalizes it to uppercase. Thus, `m/m03` and `m/M03` identify the same mission; the ID is not a displayed list position.

When executed, `DeleteMissionCommand` calls `Model#findMission(...)` and rejects a nonexistent mission before making changes. It then calls `Model#deleteMission(...)`. Within the model, `AddressBook` prepares replacement student records without the deleted mission's submission ID and removes the mission from the catalogue. This includes students hidden by the current filter and preserves their other details and submissions. Remaining IDs do not change; a deleted ID may later be explicitly reused. The command returns a `CommandResult` identifying the deleted mission.

Deleting a mission removes its submission records from every student. Recreating it uses the ID supplied by the user, which may be the deleted ID, and starts with no submissions recorded. Previous submission records are not restored.

The sequence diagram below illustrates the successful interaction flow using `execute("delmission m/M03")`. Here, `missionId` is the parsed `MissionId` value `M03`, and `feedback` is the formatted success message. The model is shown as a single participant to keep the focus on the Logic component.

<puml src="diagrams/DeleteMissionSequenceDiagram.puml" width="1000" />

### Marking and unmarking mission submissions

For `markmission id/INDEX m/MISSION_ID` or `unmarkmission id/INDEX m/MISSION_ID`, `AddressBookParser` delegates to `MarkMissionCommandParser` or `UnmarkMissionCommandParser`. Both use `ArgumentStructureValidator` to require exactly one `id/` and `m/` value, `ParserUtil#parseIndex(...)` to validate a positive student index, and `MissionParserUtil#parseMissionId(...)` to validate the case-insensitive mission ID. They then create the corresponding command. Invalid or extra arguments cause a `ParseException` that includes command usage. A positive index that does not identify a student in the current displayed list is rejected during execution with a `CommandException`; this error also includes the corresponding command usage. A well-formed ID for a mission that does not exist is rejected during execution without changing records.

When executed, the command calls `Model#findMission(...)` and retrieves the selected student from `Model#getFilteredStudentList()`. Marking uses `MissionSubmissions#withSubmission(...)`; unmarking uses `MissionSubmissions#withoutSubmission(...)`. Each produces updated immutable submission data, which the command places in a replacement `Student` before calling `Model#setStudent(...)`. Other student details and the current filter are preserved, and the observable student list notifies the UI of the replacement.

Marking an already recorded submission or unmarking an absent submission returns a no-change `CommandResult` without replacing the student. A missing mission or out-of-range displayed index causes a `CommandException` before any update. The shared mission itself is unchanged by either command.

The sequence diagram below illustrates `execute("markmission id/1 m/M03")` when the mission and student exist and the submission is not yet recorded. Here, `targetIndex` is the parsed one-based index 1, `missionId` is the parsed `MissionId` value `M03`, and `feedback` is the formatted success message. `studentToUpdate` is the selected student and `updatedStudent` is its replacement.

<puml src="diagrams/MissionSubmissionSequenceDiagram.puml" width="1000" />

The corresponding unmarking sequence uses `execute("unmarkmission id/1 m/M03")` when the submission is recorded. The parameter names have the same meanings as above; the replacement student has `M03` removed from its submissions.

<puml src="diagrams/UnmarkMissionSubmissionSequenceDiagram.puml" width="1000" />

### Saving mission changes

After any of these commands returns successfully, `LogicManager` calls `Storage.saveAddressBook` with the current address book before returning the result to the UI. The saved data contains the mission catalogue and each student's submitted mission IDs. This preserves additions, deletions, and submission changes across restarts. Successful no-change mark/unmark requests also trigger saving.

Parsing and command-validation failures exit before the saving step. If saving itself fails, `LogicManager` reports a `CommandException`; the in-memory change has already occurred and is not rolled back. On loading, storage validates the catalogue and requires saved submission IDs to refer to existing missions.

### Viewing mission submission statuses

For `viewmission m/MISSION_ID [s/overdue]`, `AddressBookParser` delegates to `ViewMissionCommandParser`. The parser reuses `ArgumentStructureValidator#validateStructureWithOptionals(...)` to require exactly one mission prefix and allow at most one status prefix.
`MissionParserUtil#parseMissionId(...)` validates the mission ID. When supplied, the status value must be `overdue`, ignoring case. Missing required prefixes, duplicate prefixes, unexpected preamble text, invalid mission IDs, and invalid status values cause a `ParseException`.

When executed, `ViewMissionCommand` first calls `Model#findMission(...)`. A nonexistent mission causes a `CommandException` before the displayed student list changes. The command then obtains the current local date using `LocalDate.now(clock)` and calls `Mission#isPastDeadline(...)`.

Without a status filter, the command calls `Model#updateFilteredStudentList(...)` with `PREDICATE_SHOW_ALL_STUDENTS`. With `s/overdue`, it installs a predicate that accepts a student only when the mission deadline has passed and `MissionSubmissions#hasSubmission(...)` returns false for that mission.

`ModelManager` maintains a `FilteredList<Student>` backed by the complete address-book student list. Replacing its predicate evaluates the query across all students, including students hidden by an earlier search.
The UI observes the same filtered list and updates automatically.

The command formats rows from `Model#getFilteredStudentList()` after installing the predicate. Row numbers therefore match the displayed student indexes. Existing `view`, `markmission`, and `unmarkmission` commands continue to resolve indexes through `Command#getStudentAtDisplayedIndex(...)`.

Submission status is derived as follows:

* A recorded submission is `Submitted`, regardless of the deadline.
* An absent submission is `Overdue` only when today's local date is strictly after the deadline.
* Otherwise, an absent submission is `Not submitted`.

A mission due today is not overdue. No overdue flag or submission timestamp is stored. The command uses `Clock.systemDefaultZone()` during normal operation and accepts an injected `Clock` through a package-private constructor for deterministic tests.

The installed predicate retains the deadline result calculated for that execution. Rerunning `viewmission` refreshes the query after the date or mission deadline changes. Student replacements still cause the filtered list to reevaluate the affected records: marking a displayed overdue student removes that student from the results, and remaining indexes shift.

Feedback includes the mission ID, description, deadline, result count, and numbered status rows. An empty result clears the displayed list and includes an explanatory message. Feedback also explains that results reflect manually maintained AvengerHub records; an absent record does not confirm that a student made no submission.

The query changes only the displayed student filter. It does not modify student records, mission definitions, or recorded submissions.
--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* serves as tutor for CS1101S tutorial(s)
* has a need to manage students across one or more tutorial groups assigned to them
* has a need to keep track of administrative information relevant to conducting and managing their tutorials
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage and access information about students under their tutelage, supporting the organization and coordination of their assigned tutorial groups.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                         | I want to …                                                              | So that I can…                                                                       |
|----------|--------------------------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `* * *`  | TA                             | add a new student to a studio group                                      | start tracking them from day one at the start of the semester                        |
| `* * *`  | TA                             | delete a student from a studio group                                     | keep my records accurate if a student drops the module or when a new semester begins |
| `* * *`  | TA                             | search for a student by name                                             | quickly find them without scrolling through the whole list                           |
| `* * *`  | TA                             | edit a student's information                                             | keep my records accurate when their information changes                              |
| `* * *`  | TA with multiple studio groups | see a combined view of all students across my groups                     | get a holistic view of my overall workload                                           |
| `* * *`  | TA                             | mark a student as present or absent for a studio                  | maintain an accurate studio record                                                   |
| `* * *`  | TA                             | add or delete shared missions                                           | manage the missions students need to complete                                        |
| `* * *`  | TA                             | mark or unmark a student's mission submission                            | see who has submitted each mission                                                   |
| `* * *`  | TA                             | record a student's mark for a given assessment                           | keep their performance data alongside other student records                          |
| `* * *`  | TA with multiple studio groups | see which studio group a student belongs to                              | avoid mixing up students across studio groups                                        |
| `* *`    | TA                             | view a summary of a student's studio records, participation, and submissions | understand their overall engagement without checking multiple records                |
| `* * *`  | TA                             | list submission statuses for a selected mission and filter overdue records | identify students who may need a submission reminder |

The user stories above represent the main intended functionality and selected future enhancements of the application. Other user stories may be considered as the project develops.

### Use cases

(For all use cases below, the **System** is the `AvengerHub` and the **Actor** is the `tutor`, unless specified otherwise)

**Use case: Add student record**

**MSS**

1. Tutor requests to add a student record and provides the required details.
2. AvengerHub adds the student record and confirms success.

   Use case ends.

**Extensions**

* 1a. The supplied details are invalid or incomplete.

    * 1a1. AvengerHub explains the error.

      Use case resumes at step 1.

* 1b. The student record duplicates an existing record.

    * 1b1. AvengerHub informs the tutor that the student record already exists.

      Use case resumes at step 1.


**Use case: Edit student record**

**MSS**

1. Tutor requests to view or search student records.
2. AvengerHub displays the corresponding student records.
3. Tutor selects a student record and supplies the details to update.
4. AvengerHub updates the student record and confirms success.

   Use case ends.

**Extensions**

* 2a. No student records are displayed.

  Use case ends.

* 3a. The student selection or supplied details are invalid or incomplete.

    * 3a1. AvengerHub explains the error.

      Use case resumes at step 3.

* 3b. The update would duplicate another student record.

    * 3b1. AvengerHub informs the tutor that the update would create a duplicate.

      Use case resumes at step 3.

**Use case: View student profile**

**MSS**

1. Tutor requests to display all students or a filtered set of students.
2. AvengerHub displays the requested student list.
3. Tutor enters `view id/INDEX`, using the student's index in the displayed list.
4. AvengerHub displays the selected student's profile, including contact
   details, studio group, tags, and mission submission statuses.

   Use case ends.

**Extensions**

* 2a. The displayed student list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub displays an error message without changing stored
      records or the current student filter.

      Use case resumes at step 3.

* 4a. No missions are configured.

    * 4a1. AvengerHub displays the student's profile with
      `No missions configured.` in the mission section.

      Use case ends.

Viewing a profile does not modify student details, mission definitions,
submission records, or the current student filter.

**Use case: Delete student record**

**MSS**

1. Tutor requests to view or search student records.
2. AvengerHub displays the corresponding student records.
3. Tutor selects a student record to delete.
4. AvengerHub deletes the student record and confirms success.

   Use case ends.

**Extensions**

* 2a. No student records are displayed.

  Use case ends.

* 3a. The student selection is invalid or incomplete.

    * 3a1. AvengerHub explains the error.

      Use case resumes at step 3.

**Use case: Mark studio record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to mark a specific student in the list as present for a specified tutorial week.
4. AvengerHub marks the selected student as present for the specified week and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. The selected student is already marked present for the specified week.

    * 3b1. AvengerHub informs the tutor that the student is already marked present and leaves the records unchanged.

      Use case ends.

**Use case: Unmark studio record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to mark a specific student in the list as absent for a specified tutorial week.
4. AvengerHub marks the selected student as absent for the specified week and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. The selected student is already absent for the specified week.

    * 3b1. AvengerHub informs the tutor that the student is already absent and leaves the records unchanged.

      Use case ends.

**Use case: Add mission**

**MSS**

1. Tutor requests to add a shared mission and provides the required details.
2. AvengerHub creates the mission with the supplied unique ID, saves it, and confirms success with that ID.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 1.

* 1b. A mission with the same name already exists.

    * 1b1. AvengerHub informs the tutor that the mission already exists and leaves the records unchanged.

      Use case resumes from step 1.

**Use case: Delete mission**

**MSS**

1. Tutor requests to delete a shared mission and provides the required details.
2. AvengerHub removes the mission and its associated submission records from every student,
   saves the updated records, and confirms success.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 1.

* 1b. No mission with the specified ID exists.

    * 1b1. AvengerHub informs the tutor that the mission does not exist and leaves the records unchanged.

      Use case resumes from step 1.

**Use case: Edit mission**

**MSS**

1. Tutor requests to edit a shared mission and provides the required details.
2. AvengerHub updates the mission, saves the changes, and confirms success.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details, including no
  replacement fields.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 1.

* 1b. No mission with the specified ID exists.

    * 1b1. AvengerHub informs the tutor that the mission does not exist and
      leaves the records unchanged.

      Use case resumes from step 1.

* 1c. The replacement name matches another mission's name, ignoring case
  and whitespace differences.

    * 1c1. AvengerHub informs the tutor that the mission name already exists
      and leaves the records unchanged.

      Use case resumes from step 1.

* 2a. The supplied values do not change the mission's name or deadline.

    * 2a1. AvengerHub informs the tutor that no changes were made.

      Use case ends.

**Use case: Add mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to add a mission submission record for a specific student in the list for a specified mission ID.
4. AvengerHub adds the selected student’s mission submission record for the specified mission and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. A mission submission record already exists for the selected student and mission.

    * 3b1. AvengerHub informs the tutor that the submission has already been recorded and leaves the records unchanged.

      Use case ends.

**Use case: Delete mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a mission submission record for a specific student in the list for a specified mission ID.
4. AvengerHub removes the selected student’s mission submission record for the specified mission and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. No mission submission record exists for the selected student and mission.

    * 3b1. AvengerHub informs the tutor that no submission is recorded and leaves the records unchanged.

      Use case ends.
      
Only the selected student's submission record changes. Other student data and
mission definitions remain unchanged. The current student filter remains
active, so its displayed results may change if they depend on that submission.

**Use case: List mission submissions**

**MSS**

1. Tutor requests to display all students or a filtered set of students.
2. AvengerHub displays the requested student list.
3. Tutor requests to list submissions for a specified mission ID.
4. AvengerHub displays the students in the current list who have a recorded
   submission for that mission, retaining their displayed student indexes.

   Use case ends.

**Extensions**

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records and current
      student filter unchanged.

      Use case resumes from step 3.

* 3b. No mission with the specified ID exists.

    * 3b1. AvengerHub informs the tutor that the mission does not exist and
      leaves the records and current student filter unchanged.

      Use case resumes from step 3.

* 4a. No students in the current list have a recorded submission for the mission,
  including when the current student list is empty.

    * 4a1. AvengerHub reports that no submissions are recorded for the mission
      within the current student list.

      Use case ends.

Student records, mission definitions, submission records, and the current
student filter remain unchanged throughout this use case.

**Use case: List overdue mission submissions**

**MSS**

1. Tutor requests to display all students or a filtered set of students.
2. AvengerHub displays the requested student list.
3. Tutor requests to list overdue mission submissions.
4. AvengerHub displays each missing submission whose mission deadline is
   before today's local date, restricted to students in the current list.
   Each result identifies the student using their displayed index and
   includes the mission's ID, name, and deadline.

   Use case ends.

**Extensions**

* 4a. No overdue submissions exist within the current student list.

    * 4a1. AvengerHub informs the tutor that no overdue submissions exist
      within the current student list.

      Use case ends.

* 4b. The current student list is empty.

    * 4b1. AvengerHub informs the tutor that no overdue submissions exist
      within the current student list.

      Use case ends.

* 4c. No missions are configured.

    * 4c1. AvengerHub informs the tutor that no overdue submissions exist
      within the current student list.

      Use case ends.

Deadlines are inclusive: missions due today are not overdue. The current
date follows the computer's local date and time zone. Recorded submissions
are excluded, regardless of when they were recorded.

**Use case: View mission submission statuses**

**MSS**

1. Tutor requests submission statuses for a mission, optionally
   selecting the overdue filter.
2. AvengerHub evaluates the query across all students and replaces the
   displayed student list with the matching students.
3. AvengerHub displays the mission ID, description, deadline, result count,
   and each matching student's recorded submission status.

   Use case ends.

**Extensions**

* 1a. The command contains missing, duplicate, or invalid arguments.

    * 1a1. AvengerHub explains the invalid input and displays command usage.
      Records and the displayed student list remain unchanged.

      Use case ends.

* 1b. The specified mission does not exist.

    * 1b1. AvengerHub reports that the mission does not exist.
      Records and the displayed student list remain unchanged.

      Use case ends.

* 2a. No students match the query.

    * 2a1. AvengerHub clears the displayed student list and displays the
      mission details, result count zero, and an empty-result explanation.

      Use case ends.

Without a status filter, all students are included. With the overdue
filter, only students without a recorded submission whose mission deadline
is before today's local date are included. Submitted students are never
included in overdue results.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2. Should start within 5 seconds on an _average university student's desktop_.
3. Should behave consistently when launched from IDE, command line, or by double-clicking the jar file.
4. Should be able to hold up to 1000 students without noticeable sluggishness (no more than 2 seconds delay) in performance for typical usage.
5. A user with _above average typing speed_ for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
6. GUI should be _usable_ from screen resolutions 1280x720 and higher and screen scales of 100%, 125%, and 150%.
7. No additional installer should be required for the software.
8. Should be _usable_ without Internet access.
9. Character encoding for data saved to disk should be UTF-8.
10. Commands and error messages should use consistent terminology and clearly identify the invalid field or input.
11. Dates, times, and identifiers should be displayed in a consistent and unambiguous format.
12. All commands should be typed into a single interface.
13. Records should be preserved after closing and reopening the application.
14. Should retain previously saved data even if an operation fails.
15. If a stored record is invalid, the system logs the loading error and starts with an empty address book. The saved file remains unchanged until a command executes successfully.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private student detail**: A student detail that is not meant to be shared with others
* **AvengerHub**: The name of the application.
* **Tutor**: The TAs (teaching assistants) in the CS1101S course that handle the tutorial sessions. Also known as "Avengers" by the course.
* **Mission**: The CS1101S term for homework assignments. A mission submission usually happens via the course website.
* **Studio group**: The CS1101S term for tutorial groups.
* **Studio effort**: The score given by an Avenger to students for each studio.
* **Mastery check**: A CS1101S assessment where the tutor conducts an oral interview with a pair of students to ensure they have understood the course content. The date and time of the assessment is decided jointly by the students and the tutor.
* **Record(s)**: The list of all student information. This is saved on the disk and exists in memory as well when the application is running.
* **Storage**: The file containing the list of student information that is saved to disk.
* **Corrupted**: Any data read from Storage that does not adhere to our expected format will be considered corrupted.
* **Operation**: The entire sequence of events resulting from a discrete user action.
* **Usable**: All functional parts of the application are accessible and work as intended.
* **Average university student's desktop**: We define relevant hardware specifications as such:
  * CPU: Intel Core i5/Ryzen 5/M-series Apple chip equivalent
  * RAM: 8GB
  * GPU: Integrated graphics
  * We expect the application to be stored in a SSD. Loading times may be worse with HDDs.
* **Above average typing speed**: Sustained >=80 WPM with a sequence of random words.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Use the following workflows to check the main user-facing behavior. Start a
complete run in a fresh working directory and follow each workflow's steps in
order, since later checks may rely on earlier commands. Use disposable
directories for the clearing and corrupted-file checks. Enter commands in the
GUI command box and press Enter.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Copy the JAR file into a new, empty folder and launch it from that folder.
   Expected: The GUI opens with the sample students and missions. No
   `data/addressbook.json` file is written during startup.

1. Enter `help`, then try the Help menu and `F1`.
   Expected: Each action opens the help window. Enter `exit`.
   Expected: The application closes.

1. Resize and move the window, close the app with `exit`, then relaunch it
   from the same folder.
   Expected: The window size and position are restored.

### Student records and filtering

1. Enter `list`, then `view id/1`.
   Expected: All sample students are listed and the first student's profile
   appears, including contact details and the student activity sections.

1. Enter `find alex, david`, then `view id/2`.
   Expected: Alex and David match the alternatives; the profile for the
   second search result is shown. Enter `find g/1A g/1B`.
   Expected: Students from either group are shown.

1. Enter `find alex lee, david g/1A`.
   Expected: The name alternatives and group filter are combined. Enter
   `find alex,` and then `find ,alex`.
   Expected: Each invalid search displays an error and does not replace the
   previous student list.

1. Add a student with
   `addstudent n/Casey Test p/98765432 e/casey@example.com g/2B t/new`.
   Expected: The student is added. Enter
   `addstudent n/Casey Test p/98765432 e/casey@example.com g/2B`.
   Expected: The duplicate student is rejected. Enter
   `edit id/1 p/91234567 g/2B t/updated`, then `view id/1`.
   Expected: The selected student's phone, group, and tags are updated.
   Enter `edit id/0 p/91234567`.
   Expected: An error is shown and no student is changed.

1. With multiple students shown, enter `delstudent id/1`, then `delstudent id/0`.
   Expected: The first command removes the first displayed student; the second
   displays an error and removes nobody. Enter `delstudent id/x` and
   `delstudent id/999`.
   Expected: Both invalid selections are rejected without changing the list.

### Student activity and profile

1. Enter `list`, then add a studio record with
   `addstudiorecord id/1 w/0 ef/0`. Enter
   `addstudiorecord id/1 w/13 ef/30`.
   Expected: Weeks 0 and 13 are recorded, including the zero-effort record.
   Enter `addstudiorecord id/1 w/13 ef/99`.
   Expected: The existing week 13 record is unchanged.

1. Enter `editstudiorecord id/1 w/13 ef/40`, then
   `delstudiorecord id/1 w/0`.
   Expected: Week 13 shows effort 40 and week 0 is absent.
   Enter `editstudiorecord id/1 w/0 ef/10` and
   `addstudiorecord id/1 w/14 ef/10`.
   Expected: The commands are rejected; no studio record is changed.

1. Enter `addnote id/1 ni/1 nt/Follow up`, then
   `addnote id/1 ni/1 nt/Replacement`.
   Expected: The first note is recorded and the duplicate ID leaves it
   unchanged. Enter `editnote id/1 ni/1 nt/Call soon`, then
   `delnote id/1 ni/1`, then `view id/1`.
   Expected: The note is edited and then removed; the profile reports that
   there are no notes.

1. Enter `addnote id/1 ni/11 nt/Invalid` and
   `addnote id/1 ni/2 nt/Bad-text!`.
   Expected: Both invalid notes are rejected. Enter `view id/1`.
   Expected: No invalid note appears.

1. Register a mastery check using
   `addmc id/1 num/1 dt/29-10|13:30`, then enter
   `markmc id/1 num/1`, `unmarkmc id/1 num/1`, and `view id/1`.
   Expected: The appointment is shown with the expected time; its status
   changes to Completed and then Not completed.

1. Enter `addmc id/1 num/1 dt/30-10|10:00` and
   `markmc id/1 num/999`.
   Expected: The duplicate appointment retains its original time, and the
   unknown number is rejected. Enter `delmc id/1 num/1`, then `view id/1`.
   Expected: The appointment is removed.

### Mission catalogue and submission reports

1. Enter `addmission m/M10 n/Manual Test d/2026-11-30`, followed by
   `addmission m/M11 n/manual   test d/2026-11-30`.
   Expected: The first mission is added with ID M10; the second is rejected
   because mission names are unique ignoring case and whitespace.
   Enter `editmission m/M10 n/Manual Test Updated d/2026-12-01`.
   Expected: The mission details change without changing its ID.

1. Enter `markmission id/1 m/M10`, then `listmissions m/M10`.
   Expected: The selected student is listed as submitted with the displayed
   student-list index. Enter `unmarkmission id/1 m/M10`, followed by
   `listmissions m/M10`.
   Expected: The student no longer appears in the submission report.

1. Enter `markmission id/1 m/UNKNOWN` and `listmissions m/UNKNOWN`.
   Expected: Both commands report that the mission ID is invalid and do not
   change records. Enter `find g/1A`, then `listmissions m/M01`.
   Expected: Only students in the current filtered list are considered, and
   result indexes still correspond to that displayed list.

1. Enter `addmission m/M20 n/Overdue Manual Test d/2000-01-01`, then
   `listoverdue`.
   Expected: M20 is listed as overdue for each student in the current list
   without a recorded submission. Record one listed pair with
   `markmission id/1 m/M20`, then run `listoverdue` again.
   Expected: That student-M20 pair is no longer reported.

1. Enter `delmission m/M10`, then `view id/1`.
   Expected: M10 is removed from the shared catalogue and its submission
   records are removed from every student. Enter
   `addmission m/M10 n/Manual Test Restored d/2026-12-01`.
   Expected: The ID can be reused, but prior submissions are not restored.

### Saving data and clearing records

1. Add a note, a mastery check, a mission, a mission submission, close and relaunch
   the app from the same working directory. Enter `view id/1`.
   Expected: The saved students, missions, studio records, notes, mastery
   checks, and submissions are restored.

1. In a disposable folder, launch the app without `data/addressbook.json`.
   Expected: Sample students and missions are loaded, and startup leaves the
   data file absent. Run `list`, then check for the file.
   Expected: The successful command saves the current address book.

1. Back up `data/addressbook.json`, replace its contents with invalid JSON,
   and relaunch the app.
   Expected: A loading error is logged, the app starts with an empty address
   book, and the invalid file remains unchanged at startup. Run `list`.
   Expected: The command succeeds and saves the current (empty) address book
   over the invalid file. Restore the backup if the previous data is needed.

1. In a disposable working directory, enter `clear`, then `list` and
   `listmissions m/M01`.
   Expected: The student list is empty and the mission lookup reports that
   M01 does not exist. Relaunch the app and enter
   `addmission m/M01 n/Cleared Catalogue Test d/2026-12-31`.
   Expected: The mission can be added, confirming that the cleared catalogue
   remains empty after restart.
