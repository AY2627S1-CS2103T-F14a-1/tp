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

Notable uses of AI:

* ChatGPT Codex was used by @BrianLim35 to assist with writing test cases, guide documentation, and input validation.
* ChatGPT Codex was used by @zRayhhh to generate most tests and assist with documentation and code refactoring.
* ChatGPT Codex was used by @plutonaruto to help with writing test cases, review and identify any inconsistencies in documentation as well as suggest validation improvements.
* ChatGPT Codex was used by @nuggettowin to generate most non-GUI tests, and studio record and note representation in the UI.
* ChatGPT Codex was used by @EthanJoshLeong to generate test cases, to refine the CSS files used by the UI and improve the overall visual consistency of the UI.

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

`ResultDisplay` presents command feedback, including mission summaries
and submission-status reports. `listmissions` supports optional mission
and status filters without replacing the current student filter.
Student profiles use model objects and the mission catalogue supplied
through the logic component.

`ResultDisplay` calculates its preferred height from wrapped feedback and
visible warnings, including padding. JavaFX bindings update this height
when the text, width, or height limit changes.

`MainWindow` limits the results area, including its surrounding padding,
to 30% of the space shared with the student panels. Longer results scroll
within `ResultDisplay`, while shorter results release space back to the
student panels. New feedback resets the results scroll position.

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
* The `XYZComponentParserUtil` refers to the `parser.util` class that the `XYZCommandParser` relies on for input validation.
* Simpler `XYZCommandParser` classes, such as `DeleteStudentCommandParser`, do not require use of its component's parser util class (e.g., `StudentParserUtil`), as there are no component specific strings to parse.


### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="850" />

The `Model` component,

* stores the address book data: students in a `UniqueStudentList` and shared missions in a `MissionCatalogue`.
* maintains a filtered student list, exposed as an unmodifiable `ObservableList<Student>` for the UI to observe.
* stores user preferences in `UserPrefs`, exposed as `ReadOnlyUserPrefs`.
* does not depend on the other components.

Each student has one `RegisteredStudioRecords` object containing an immutable
map from studio weeks 0 through 13 to `StudioRecord` objects. Each recorded
week has one record containing a non-negative effort score. The week is the map
key; the presence of a record indicates attendance, even when its effort score
is zero. Weeks without a record are displayed as absent with an effort score
of zero. Each student also has one `RegisteredNotes` object containing at most
1,000 notes, one `RegisteredMasteryChecks` object containing numbered
appointments, and one `MissionSubmissions` object containing zero or more
submitted mission IDs. The notes list and mastery-check registration map are
unmodifiable; mastery-check appointment objects retain mutable completion
status. The `1` multiplicities count the per-student containers, not the
records inside them.

Composition diamonds show conceptual ownership of student records and mission
definitions; immutable values may be reused internally when records are
replaced. Submission IDs reference shared mission definitions rather than
owning them.

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

<puml src="diagrams/LoadAdaptedStudentActivityDiagram.puml" width="1000" />
<puml src="diagrams/SaveAdaptedStudentActivityDiagram.puml" width="750" />

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Parsing workflow

When adding a command, the developer must also create a parser for that command. This section covers the general structure for command parsers.

We describe here 3 command parsing archetypes of different complexity:
1. **Simple**: a command such as `list` that requires no arguments and hence no additional parsing;
2. **Standard**: a command such as `addmc` that requires a certain number of mandatory arguments;
3. **Complex**: a command such as `find` or `edit` that have optional arguments.

**Simple** commands are self-explanatory, so we omit them from discussion.

Both **Standard** and **Complex** commands generally follow the same structure:
1. Use the relevant `ArgumentStructureValidator` method to tokenize the user input line. This catches missing or malformed prefixes, as well as prefixes with empty values following them, such as in `edit id/`.
2. Extract values from the `ArgumentMultimap` returned in [1]:
* 2a. **Standard** command: all prefixes are expected to be present, so use `ArgumentMultimap#getRequiredValue` and pass the obtained value through the relevant `XYZParserUtil` method.
* 2b. **Complex** command: some prefixes may be optional, so first check whether the prefix is present before getting. Pass the obtained value through the relevant `XYZParserUtil` method.
3. Return a new `XYZCommand` with the relevant values.
4. Wrap the parsing in a `try-catch` block to append usage messages.

For reference, we recommend checking out the implementations:
* **Standard**: `AddMissionCommandParser`
* **Complex**: `EditStudentCommandParser`

Other commands like `FindCommand` do not use the `Prefix` system and hence may be implemented in different ways.

### Adding a mission

`AddressBookParser` passes the input to `AddMissionCommandParser`, which creates an `AddMissionCommand` from the parsed mission details.

When executed, `AddMissionCommand` checks that the mission ID and name are unused, calls `Model#addMission(...)`, and returns a success message in a `CommandResult`.

`MissionCatalogue` also enforces unique IDs and names when adding and restoring saved data,
so callers cannot bypass this rule by accessing the model directly. Restoration validates the
complete replacement before changing catalogue state. Mission IDs are supplied by the user and
stored with each mission.

The sequence diagram below illustrates the successful interaction flow for adding a mission. Call arguments are abbreviated as `...` for readability.

<puml src="diagrams/AddMissionSequenceDiagram.puml" width="1000" />

### Deleting a mission

`AddressBookParser` passes the input to `DeleteMissionCommandParser`, which creates a `DeleteMissionCommand` for the requested mission.

When executed, `DeleteMissionCommand` checks that the mission exists, calls `Model#deleteMission(...)`, and returns a `CommandResult` identifying the deleted mission. The model also removes that mission's submission records from all students.

The sequence diagram below illustrates the successful interaction flow for deleting a mission. Call arguments are abbreviated as `...` for readability.

<puml src="diagrams/DeleteMissionSequenceDiagram.puml" width="1000" />

### Marking and unmarking mission submissions

`AddressBookParser` delegates each input to its matching parser, which creates a `MarkMissionCommand` or `UnmarkMissionCommand`.

Each command checks that the mission exists, retrieves the displayed students, and selects the student at the requested index. Marking adds the mission ID to the student's submissions; unmarking removes it. If the submission state needs to change, the command updates the student through `Model#setStudent(...)`, then returns a `CommandResult`.

The sequence diagram below illustrates marking a mission submission when the mission and student exist and the submission is not yet recorded. Call arguments are abbreviated as `...` for readability.

<puml src="diagrams/MissionSubmissionSequenceDiagram.puml" width="1000" />

The corresponding unmarking sequence illustrates removing a recorded mission submission. Call arguments are abbreviated as `...` for readability.

<puml src="diagrams/UnmarkMissionSubmissionSequenceDiagram.puml" width="1000" />

### Editing a mission

For `editmission m/MISSION_ID [n/NAME] [d/DEADLINE]`,
`AddressBookParser` delegates to `EditMissionCommandParser`. The parser
requires one mission ID and at least one replacement field. It rejects
unexpected preamble text, repeated prefixes, blank values, and invalid
mission IDs or deadlines.

The parser stores validated replacement fields in an
`EditMissionDescriptor`. `EditMissionCommand` makes a defensive copy of
this descriptor so that subsequent changes to the original descriptor
cannot alter the command.

During execution, the command resolves the existing mission and constructs
a replacement retaining its ID and any unspecified fields. A replacement
name must not duplicate another mission's name, ignoring case and
whitespace differences. Keeping the mission's own name is allowed,
including changing only its letter case.

If the replacement equals the original mission, the command returns
no-change feedback. Otherwise, it calls `Model#setMission(...)`.
`MissionCatalogue` validates the replacement and preserves its position
in the catalogue. Student records, submission records, and the current
student filter remain unchanged.

**Design rationale:** Mission IDs remain stable because student submission
records refer to these IDs. Replacing the immutable mission allows its name
and deadline to change without rewriting submission records. The optional
descriptor fields distinguish omitted values from requested replacements.

The following diagram shows a successful name-only edit of M01 to a name
not used by another mission. Validation, formatting, logging, result
construction, and the common saving step are omitted. The model is shown
as a single participant.

<puml src="diagrams/EditMissionSequenceDiagram.puml" width="850" />

### Saving mission changes

After any of these commands returns successfully, `LogicManager` calls `Storage.saveAddressBook` with the current address book before returning the result to the UI. The saved data contains the mission catalogue and each student's submitted mission IDs. This preserves additions, deletions, and submission changes across restarts. Successful no-change mark/unmark requests also trigger saving.

Parsing and command-validation failures exit before the saving step. If saving itself fails, `LogicManager` reports a `CommandException`; the in-memory change has already occurred and is not rolled back. On loading, storage validates the catalogue and requires saved submission IDs to refer to existing missions.

### Finding students

`FindCommandParser` separates the name portion from repeated `g/`
studio-group filters. Name alternatives must appear before group filters.
It splits the name portion at commas and rejects empty alternatives,
including leading, trailing, and consecutive commas.

`NameContainsKeywordsPredicate` performs case-insensitive partial-name
matching. Every whitespace-separated keyword within an alternative must
match; any comma-separated alternative may satisfy the name condition.
Repeated identical keywords require separate, non-overlapping occurrences.
Different keywords are matched independently.

Studio-group filters use exact matches, with repeated groups combined
using OR. When both name alternatives and studio groups are supplied, the
parser combines the name and group predicates using AND.

`FindCommand#execute(...)` replaces the model's student-list predicate and
reports the number of matching students. Searching does not modify stored
student records, mission definitions, or submission records.

**Design rationale:** Spaces allow the tutor to narrow a search using
multiple parts of a name, while commas express alternatives in one
command. Predicate composition keeps the name and studio-group matching
rules separate. The filtered list also provides the displayed indexes
used by subsequent student commands.

The diagram shows `find alex, brian g/1A`. The parser builds a predicate
matching either name alternative within group 1A. Predicate construction
is summarized in a note; validation, formatting, result construction, and
the common saving step are omitted.

<puml src="diagrams/FindStudentsSequenceDiagram.puml" width="850" />

### Listing mission submissions

`ListMissionsCommandParser` accepts optional, nonrepeatable `m/` and `s/`
prefixes. Blank arguments produce `ListMissionCatalogueCommand`. Otherwise,
a mission ID selects one mission, and an optional status restricts results
to Submitted or Overdue. A status without a mission ID queries all missions.
Malformed mission IDs, unsupported statuses, duplicate prefixes, and
unexpected preamble text cause a `ParseException`.

`ListMissionsCommand` stores optional mission and status selections.
A specified mission is resolved before generating results; a nonexistent
mission causes a `CommandException`. Without a mission selection, the
command obtains all missions in ascending numeric ID order.

The command obtains the current local date once using an injected `Clock`.
`MissionSubmissionStatusEnum#from(...)` determines each student's status:

* A recorded submission is Submitted, regardless of the deadline.
* A missing submission is Overdue when the deadline is before the local date.
* Otherwise, a missing submission is Not submitted.

Missions due today are not overdue. Normal execution uses the computer's
local time zone; tests inject a fixed clock for deterministic boundary checks.

Both query forms share the same row-building logic and inspect only
`Model#getFilteredStudentList()`. The optional status filter selects
matching rows without changing the model's student filter.

Rows include displayed student indexes, names, studio groups, and statuses.
All-mission queries also include mission details and order rows by mission
ID, then displayed student order. A student may appear in multiple rows
while retaining the same displayed index.

Separate messages distinguish an empty mission catalogue, an empty student
list, and no matching statuses. Student records, mission definitions,
submission records, and the current student filter remain unchanged.
Command equality includes both the mission and status selections.

### Listing mission summaries

For bare `listmissions`, `ListMissionsCommandParser` creates a
`ListMissionCatalogueCommand`.

The command reads `Model#getMissionList()` and sorts the output by numeric
mission ID without changing stored catalogue order. Every mission appears
regardless of the current student filter.

For each mission, it counts recorded submissions within
`Model#getFilteredStudentList()` using
`MissionSubmissions#hasSubmission(...)`. It displays `Submitted: X/Y`,
where X is the submitted count and Y is the filtered student count.
Hidden students contribute to neither count. An empty filtered list
produces `0/0`.

The command obtains the local date once per execution from an injected
`Clock` and evaluates deadlines using `Mission#isPastDeadline(...)`.
`Messages#format(Mission, boolean)` adds `Deadline passed` inside the
due-date parentheses when the deadline is before that date. Missions due
today are not labelled. The label is independent of submission counts.

Normal execution uses `Clock.systemDefaultZone()`; tests inject a fixed
clock for deterministic deadline checks.

An empty catalogue returns `No missions configured.` Student records,
mission definitions, submission records, and the student filter remain
unchanged. Rerunning the command recalculates counts and deadline labels.

The query preserves the displayed student filter and does not modify
student records, mission definitions, or recorded submissions.

**Design rationale:** Statuses are derived from recorded submissions and
the current date rather than stored separately. This avoids stale overdue
flags when a deadline is edited or the date changes. Filtering report rows
instead of the student list preserves displayed indexes for subsequent
commands.

The following diagram shows `listmissions m/M01` for an existing mission.
The parser creates an unfiltered single-mission query. Status derivation
and row formatting are summarized in a note; parser construction,
validation, result construction, and the common saving step are omitted.

<puml src="diagrams/ListMissionStatusesSequenceDiagram.puml" width="850" />
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

* serves as tutor for the National University of Singapore's CS1101S course.
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
| `* * *`  | TA                             | delete a student from a studio group                                     | keep my records accurate with adjustments to the student roster |
| `* * *`  | TA                             | search for a student by name                                             | quickly find them without scrolling through the whole list                           |
| `* * *`  | TA                             | edit a student's information                                             | keep my records accurate when their information changes                              |
| `* * *`  | TA                             | add a note for a given student of mine                                    | tailor my teaching to their specific need                              |
| `* * *`  | TA                             | delete a note for a given student of mine                                    | keep my notes for each student accurate over time                              |
| `* * *`  | TA                             | edit a note for a given student of mine                                    | make direct changes to a note that is still relevant                              |
| `* * *`  | TA with multiple studio groups | see a combined view of all students across my groups                     | get a holistic view of my overall workload                                           |
| `* * *`  | TA                             | add or delete shared missions                                           | manage the missions students need to complete                                        |
| `* * *`  | TA                             | mark a student's mission submission                            | see who has submitted each mission                                                   |
| `* * *`  | TA                             | unmark a student's mission submission                            | keep the mission submission record accurate if it was mistakenly marked                        |
| `* * *`  | TA                             | record a student's mark for a given assessment                           | keep their performance data alongside other student records                          |
| `* * *`  | TA with multiple studio groups | see which studio group a student belongs to                              | avoid mixing up students across studio groups                                        |
| `* * *`  | TA                             | add a student's record for a weekly studio                           | keep track of their weekly performance in one place                          |
| `* * *`  | TA                             | delete a student's record for a weekly studio                           | keep the studio record accurate if it was mistakenly added                          |
| `* * *`  | TA                             | edit a student's record for a weekly studio                           | edit the student's effort score directly if I changed my mind                          |
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

**Use case: Add studio record**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows a list of students.
3. Tutor requests to add a week's studio record for a specific student in the list.
4. AvengerHub adds the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. The selected student's studio record is already registered for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Delete studio record**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a week's studio record for a specific student in the list.
4. AvengerHub deletes the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. The selected student's studio record is already absent for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Edit studio record**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows a list of students.
3. Tutor requests to edit a week's studio record for a specific student in the list.
4. AvengerHub edits the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. The selected student's studio record does not exist for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

* 3c. The selected student's studio record exists and has the same details as the tutor's intended edit.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Add note**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows a list of students.
3. Tutor requests to add a note for a specific student in the list.
4. AvengerHub adds the note for the student.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes from step 3.

* 3b. The selected student cannot support any more notes.

    * 3b1. AvengerHub informs the tutor and leaves the note records unchanged.

      Use case ends.

* 3c. The selected student already has a note with identical content.

    * 3c1. AvengerHub warns the tutor. Use case resumes at step 4.

**Use case: Delete note**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows a list of students.
3. Tutor requests to delete a note for a specific student in the list.
4. AvengerHub deletes the note for the student.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes from step 3.

**Use case: Edit note**

**MSS**

1. Tutor opens AvengerHub.
2. AvengerHub shows a list of students.
3. Tutor requests to edit a note for a specific student in the list.
4. AvengerHub edits the note for the student.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes from step 3.

* 3c. The selected student already has a note with identical content.

    * 3c1. AvengerHub warns the tutor. 
  
      Use case resumes at step 4.

**Use case: Add mission**

**MSS**

1. Tutor requests to add a shared mission and provides the required details.
2. AvengerHub creates the mission with the supplied unique ID, saves it, and confirms success with that ID.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 1.

* 1b. A mission with the same name or supplied ID already exists.

    * 1b1. AvengerHub informs the tutor that the mission name or ID is already in use and leaves the records unchanged.

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

**Use case: List all configured missions**

**MSS**

1. Tutor requests the mission catalogue.
2. AvengerHub displays all configured missions and their relevant details,
   including submission counts for the current student list.

   Use case ends.

**Extensions**

* 2a. No missions are configured.

    * 2a1. AvengerHub informs the tutor that no missions are configured.

      Use case ends.

The catalogue includes all configured missions regardless of the current
student filter. Student records, mission definitions, submission records,
and the current student filter remain unchanged.

**Use case: List specific mission submissions**

**MSS**

1. Tutor requests to display all students or a filtered set of students.
2. AvengerHub displays the requested student list.
3. Tutor requests submission statuses for a specified mission, optionally
   filtering by submitted or overdue status.
4. AvengerHub displays matching students and their submission statuses,
   using their indexes in the current student list.

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

* 4a. No submission statuses match the selected status filter.

    * 4a1. AvengerHub informs the tutor that no submissions match the
      selected status.

      Use case ends.

* 4b. The current student list is empty.

    * 4b1. AvengerHub informs the tutor that no students are in the
      current student list.

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

    * 4b1. AvengerHub informs the tutor that no students are in the
      current student list.

      Use case ends.

* 4c. No missions are configured.

    * 4c1. AvengerHub informs the tutor that no missions are configured.

      Use case ends.

Deadlines are inclusive: missions due today are not overdue. The current
date follows the computer's local date and time zone. Recorded submissions
are excluded, regardless of when they were recorded.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2. Should start within 10 seconds on an _average university student's desktop_.
3. Should behave consistently when launched from command line.
4. Should be able to hold up to 1000 students without noticeable sluggishness (no more than 3 seconds delay) in performance for typical usage.
5. A user with _above average typing speed_ for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
6. GUI should be _usable_ from screen resolutions 1280x720 and higher.
7. No additional installer should be required for the software.
8. Should be _usable_ without Internet access.
9. Character encoding for data saved to disk should be UTF-8.
10. All commands should be typed into a single interface.
11. Records should be preserved after closing and reopening the application.
12. Should retain previously saved data even if an operation fails.
13. If a stored record is invalid, the system logs the loading error and starts with an empty address book. The saved file remains unchanged until a command executes successfully.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private student detail**: A student detail that is not meant to be shared with others
* **Matriculation number**: The NUS-supplied number that uniquely identifies a student, starting with the letter A, followed by 7 digits, and ending with another uppercase letter from A-Z. Example: A0123456B.
* **AvengerHub**: The name of the application.
* **Tutor**: The TAs (teaching assistants) in the CS1101S course that handle the tutorial sessions. Also known as "Avengers" by the course.
* **Mission**: The CS1101S term for homework assignments. A mission submission usually happens via the course website.
* **Studio**: The CS1101S term for tutorials.
* **Studio group**: The CS1101S term for tutorial groups.
* **Studio week**: Equivalent to a tutorial week. They are numbered weeks of the term.
* **Studio effort**: The score given by an Avenger to students for each studio.
* **Studio record**: The record of a student's attendance and effort for a given studio week.
* **Mastery check/Mastery check appointment**: A CS1101S assessment where the tutor conducts an oral interview with a pair of students to ensure they have understood the course content. The date and time of the assessment is decided jointly by the students and the tutor.
* **Record(s)**: The list of all student information. This is saved on the disk and exists in memory as well when the application is running.
* **Mission catalogue**: The list of missions, visible and standard across all students.
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

To be added

### Student activity and profile

To be added

### Mission catalogue and submission reports

Run this workflow independently in a fresh working directory; it does not rely
on the preceding workflows. Start the app, enter `clear`, then create two
students and two missions:

```text
addstudent n/John Doe mtc/A0409127H p/98765432 e/john@example.com g/1A
addstudent n/Betsy Crowe mtc/A0123456D p/98765433 e/betsy@example.com g/1B
addmission m/M01 n/Runes d/2026-11-30
addmission m/M02 n/Curves d/2026-12-01
```

Enter `list` to confirm both students are present. This setup ensures the
`find g/1A` checks below have a matching student; that filter shows John as
index 1.

1. Enter `listmissions`.
   Expected: All configured missions appear with their IDs, names, and
   deadlines in ascending numeric ID order.
   Enter `find g/1A`, followed by `listmissions`.
   Expected: The same missions appear; submission counts reflect the
   group 1A filter.

1. Enter `listmissions m/`.
   Expected: Mission ID constraint feedback appears.
   Enter `listmissions` again.
   Expected: The catalogue and student filter remain unchanged.

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
   Expected: The mission no longer appears as submitted in the submission report.

1. Enter `markmission id/1 m/UNKNOWN` and `listmissions m/UNKNOWN`.
   Expected: Both commands report that the mission ID is invalid and do not
   change records. Enter `find g/1A`, then `listmissions m/M01`.
   Expected: Only students in the current filtered list are considered, and
   result indexes still correspond to that displayed list.

1. Enter `addmission m/M20 n/Overdue Manual Test d/2000-01-01`, then
   `listmissions s/overdue`.
   Expected: M20 is listed as overdue for each student in the current list
   without a recorded submission. Record one listed pair with
   `markmission id/1 m/M20`, then run `listmissions s/overdue` again.
   Expected: That student-M20 pair is no longer reported.

1. Enter `delmission m/M10`, then `view id/1`.
   Expected: M10 is removed from the shared catalogue and its submission
   records are removed from every student. Enter
   `addmission m/M10 n/Manual Test Restored d/2026-12-01`.
   Expected: The ID can be reused, but prior submissions are not restored.

### Saving data and clearing records

1. Add a note, a mastery check, a studio record, a mission, a mission submission, close and relaunch
   the app from the same working directory. Enter `view id/1`.
   Expected: The saved students, missions, studio records, notes, mastery
   checks, and submissions are restored.

1. In a disposable working directory, enter `clear`, then `list` and
   `listmissions m/M01`.
   Expected: The student list is empty and the mission lookup reports that
   M01 does not exist.
