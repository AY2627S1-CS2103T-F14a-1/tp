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

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user
issues a valid `addstudent` command.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component" width="1500"/>

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

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delstudent id/1` Command" width="1000"/>

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

<puml src="diagrams/ModelClassDiagram.puml" width="1650" />

The `Model` component,

* stores address book data: students in a `UniqueStudentList` and shared missions in a `MissionCatalogue`.
* maintains a filtered student list, exposed as an unmodifiable `ObservableList<Student>` for the UI to observe.
* stores user preferences in `UserPrefs`, exposed as `ReadOnlyUserPrefs`.
* does not depend on the other components.
* maintains the following records for each student:
  * `RegisteredStudioRecords` contains an immutable map from studio weeks 0 through 13 to `StudioRecord` objects.
  * `RegisteredNotes` contains at most 100 notes.
  * `RegisteredMasteryChecks` contains numbered appointments. Its registration map is unmodifiable, while appointment objects retain mutable completion status.
  * `MissionSubmissions` contains zero or more submitted mission IDs.
* represents conceptual ownership with composition diamonds. Immutable values may be reused internally when records are replaced, and submission IDs reference shared mission definitions rather than owning them.
* uses `1` multiplicities to count per-student containers, not the records inside them.

<box type="info" seamless>

**Note:** The diagram below illustrates a possible alternative for managing tags. Currently, each `Student` stores its own set of tags, and `AddressBook` has no `UniqueTagList`. In the alternative, `AddressBook` would store one `Tag` object per unique tag in a central `UniqueTagList`, and students would reference those shared tags. Student details, including registered notes, are shown to illustrate the surrounding model; only tag ownership differs from the current design. The mission catalogue is omitted for clarity.

<puml src="diagrams/BetterModelClassDiagram.puml" width="1400" height="500" />
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

<puml src="diagrams/LoadAdaptedStudentActivityDiagram.puml" width="1800" />
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

### Mission implementation

Mission commands share a common parser-command-model flow:

* `AddressBookParser` routes each input to the matching parser, which validates
  its arguments and creates a command.
* The command applies the requested operation through `Model` and returns a
  `CommandResult`.
* Catalogue commands add, edit, or delete missions through the model.
  `MissionCatalogue` enforces unique mission IDs and names when adding or
  restoring saved data, and validates the complete catalogue before replacing
  its state. Mission IDs are supplied by the user and remain stable when a
  mission is edited, so student submission records continue to refer to the
  same mission. Deleting a mission also removes its submission records from
  students.
* Submission commands resolve the mission and the selected student from the
  currently displayed student list. They add or remove the mission ID in that
  student's submissions and update the student through `Model#setStudent(...)`
  when the submission state changes.
* After a mission command succeeds, `LogicManager` saves the current address
  book before returning the result to the UI. Successful no-change mark or
  unmark requests are saved as well. The saved data includes the mission
  catalogue and each student's submitted mission IDs. Parsing and
  command-validation failures exit before saving; if saving fails,
  `LogicManager` reports a `CommandException`, but does not roll back the
  in-memory change. On loading, storage validates the catalogue and requires
  saved submission IDs to refer to existing missions.

The following sequence diagram illustrates representative mission flows for
adding a mission. Call arguments are
abbreviated as `...` for readability.

<puml src="diagrams/AddMissionSequenceDiagram.puml" width="1000" />

### Tracking mastery checks for a student

Each `MasteryCheck` attributed to a `Student` is stored in `RegisteredMasteryChecks` using a hashmap of the mastery check number to the associated `MasteryCheck` object. It is up to `RegisteredMasteryChecks` to handle operations on mastery checks via the input number.

This implementation guarantees that mastery checks under the same student will have unique numbers to identify them by. Note that since hashmaps allow overwriting of old values associated with a key, the developer should check whether there is an existing mastery check with a number before updating the internal hashmap with a `put`.

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

* serves as tutor for the National University of Singapore's CS1101S course
* has a need to manage students across one or more tutorial groups assigned to them
* has a need to keep track of administrative information relevant to conducting and managing their tutorials
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Manage and access information about students under their tutelage, supporting the organization and coordination of their assigned tutorial groups.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …                          | I want to …                                                                   | So that I can…                                                          |
|----------|---------------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| `* * *`  | TA                              | add a new student to a studio group                                           | start tracking them from day one at the start of the semester           |
| `* * *`  | TA                              | delete a student from a studio group                                          | keep my records accurate with adjustments to the student roster         |
| `* * *`  | TA                              | search for a student by name                                                  | quickly find them without scrolling through the whole list              |
| `* * *`  | TA                              | edit a student's information                                                  | keep my records accurate when their information changes                 |
| `* * *`  | TA                              | add a note for a given student of mine                                        | tailor my teaching to their specific need                               |
| `* * *`  | TA                              | delete a note for a given student of mine                                     | keep my notes for each student accurate over time                       |
| `* * *`  | TA                              | edit a note for a given student of mine                                       | make direct changes to a note that is still relevant                    |
| `* * *`  | TA                              | add a mastery check appointment for a given student                           | stay aware of irregular appointment times with students                 |
| `* * *`  | TA                              | delete a mastery check appointment for a given student                        | remove an appointment with inaccurate timing                            |
| `* * *`  | TA                              | mark a mastery check appointment for a given student                          | easily see my students' progress in mastery checks                      |
| `* * *`  | TA                              | unmark a mastery check appointment for a given student                        | keep the mastery check record accurate if it was mistakenly marked      |
| `* * *`  | TA with multiple studio groups | see a combined view of all students across my groups                          | get a holistic view of my overall workload                              |
| `* * *`  | TA                              | add shared missions                                                           | make new missions available for students to complete                    |
| `* * *`  | TA                              | delete shared missions                                                        | remove missions that students no longer need to complete                |
| `* * *`  | TA                              | mark a student's mission submission                                           | see who has submitted each mission                                      |
| `* * *`  | TA                              | unmark a student's mission submission                                         | keep the mission submission record accurate if it was mistakenly marked |
| `* * *`  | TA with multiple studio groups | see which studio group a student belongs to                                   | avoid mixing up students across studio groups                           |
| `* * *`  | TA                              | add a student's record for a weekly studio                                    | keep track of their weekly performance in one place                     |
| `* * *`  | TA                              | delete a student's record for a weekly studio                                 | keep the studio record accurate if it was mistakenly added              |
| `* * *`  | TA                              | edit a student's record for a weekly studio                                   | edit the student's effort score directly if I changed my mind           |
| `* * *`  | TA                              | list submission statuses for a selected mission and filter overdue records    | identify students who may need a submission reminder                    |
| `* *`    | TA                              | view a summary of a student's studio records, participation, and submissions  | understand their overall engagement without checking multiple records   |
| `*`      | TA with multiple studio groups | view a graph of mission submissions across tutorial groups                   | compare submission progress between my groups                           |
| `* `     | TA                              | record a student's mark for a given assessment                                | keep their performance data alongside other student records             |

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
4. AvengerHub displays the selected student's profile details.

   Use case ends.

**Extensions**

* 2a. The displayed student list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub displays an error message without changing stored
      records or the current student filter.

      Use case resumes at step 3.



**Use case: Add studio record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows a list of students.
3. Tutor requests to add a week's studio record for a specific student in the list.
4. AvengerHub adds the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. The selected student's studio record is already registered for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Find students**

**MSS**

1. Tutor provides search criteria for student names or studio groups.
2. AvengerHub displays the matching students.

   Use case ends.

**Extensions**

* 1a. The supplied criteria are invalid or incomplete.

    * 1a1. AvengerHub explains the error.

      Use case resumes at step 1.

* 2a. No students match the criteria.

    * 2a1. AvengerHub displays an empty student list.

      Use case ends.

**Use case: Delete studio record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a week's studio record for a specific student in the list.
4. AvengerHub deletes the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. The selected student's studio record is already absent for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Edit studio record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows a list of students.
3. Tutor requests to edit a week's studio record for a specific student in the list.
4. AvengerHub edits the studio record for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. The selected student's studio record does not exist for the specified week.

    * 3b1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

* 3c. The selected student's studio record exists and has the same details as the tutor's intended edit.

    * 3c1. AvengerHub informs the tutor and leaves the records unchanged.

      Use case ends.

**Use case: Add note**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows a list of students.
3. Tutor requests to add a note for a specific student in the list.
4. AvengerHub adds the note for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes at step 3.

* 3b. The selected student cannot support any more notes.

    * 3b1. AvengerHub informs the tutor and leaves the note records unchanged.

      Use case ends.

* 3c. The selected student has a note with potentially the same content.

    * 3c1. AvengerHub warns the tutor. 
  
    Use case resumes at step 4.

**Use case: Delete note**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows a list of students.
3. Tutor requests to delete a note for a specific student in the list.
4. AvengerHub deletes the note for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes at step 3.

* 3b. The note does not exist for the student.

    * 3b1. AvengerHub informs the tutor and leaves the note records unchanged.

      Use case ends.

**Use case: Edit note**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows a list of students.
3. Tutor requests to edit a note for a specific student in the list.
4. AvengerHub edits the note for the student.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the note records unchanged.

      Use case resumes at step 3.

* 3b. The note does not exist for the student.

    * 3b1. AvengerHub informs the tutor and leaves the note records unchanged.

      Use case ends.

* 3c. The selected student has a note with potentially the same content.

    * 3c1. AvengerHub warns the tutor. 
  
      Use case resumes at step 4.

**Use case: Add mastery check**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to add a mastery check appointment for a specific student in the list and provides the required details.
4. AvengerHub creates the mastery check with the supplied number and date-time, saves it, and confirms success with those details.

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. A mastery check with the same number already exists for the student.

    * 3b1. AvengerHub informs the tutor that a mastery check already exists with that number and leaves the records unchanged.

      Use case ends.

**Use case: Delete mastery check**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a mastery check appointment for a specific student in the list and provides the required details.
4. AvengerHub removes the mastery check with the supplied number from that student, saves the updated records, and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. A mastery check with the supplied number does not exist for the student.

    * 3b1. AvengerHub informs the tutor that there is no mastery check with that number and leaves the records unchanged.

      Use case ends.

**Use case: Mark mastery check**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to mark a mastery check appointment as completed for a specific student in the list and provides the required details.
4. AvengerHub marks the mastery check with the supplied number as completed for that student, saves the updated records, and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. A mastery check with the supplied number does not exist for the student.

    * 3b1. AvengerHub informs the tutor that there is no mastery check with that number and leaves the records unchanged.

      Use case ends.

* 3c. A mastery check with the supplied number for the student is already marked complete.

    * 3c1. AvengerHub informs the tutor that the mastery check with that number is already marked complete and leaves the records unchanged.

      Use case ends.

**Use case: Unmark mastery check**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to mark a mastery check appointment as incomplete for a specific student in the list and provides the required details.
4. AvengerHub marks the mastery check with the supplied number as incomplete for that student, saves the updated records, and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. A mastery check with the supplied number does not exist for the student.

    * 3b1. AvengerHub informs the tutor that there is no mastery check with that number and leaves the records unchanged.

      Use case ends.

* 3c. A mastery check with the supplied number for the student is already incomplete.

    * 3c1. AvengerHub informs the tutor that the mastery check with that number is already incomplete and leaves the records unchanged.

      Use case ends.

**Use case: List mastery checks**

**MSS**

1. Tutor requests to display all students or a filtered set of students.
2. AvengerHub displays the requested student list.
3. Tutor enters `listmc`.
4. AvengerHub displays all mastery checks after the current date and time
   for the students currently shown.
5. AvengerHub orders the mastery checks chronologically.

   Use case ends.

**Extensions**

* 4a. There are no upcoming mastery checks for the students currently shown.
    * 4a1. AvengerHub informs the Tutor that there are no upcoming mastery checks.

      Use case ends.

**Use case: Add mission**

**MSS**

1. Tutor requests to add a shared mission and provides the required details.
2. AvengerHub creates the mission with the supplied unique ID, saves it, and confirms success with that ID.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 1.

* 1b. A mission with the same name or supplied ID already exists.

    * 1b1. AvengerHub informs the tutor that the mission name or ID is already in use and leaves the records unchanged.

      Use case resumes at step 1.

**Use case: Delete mission**

**MSS**

1. Tutor requests to delete a shared mission and provides the required details.
2. AvengerHub removes the mission and its associated submission records from every student,
   saves the updated records, and confirms success.

   Use case ends.

**Extensions**

* 1a. The tutor provides invalid or incomplete details.

    * 1a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 1.

* 1b. No mission with the specified ID exists.

    * 1b1. AvengerHub informs the tutor that the mission does not exist and leaves the records unchanged.

      Use case resumes at step 1.

**Use case: Edit mission**

**MSS**

1. Tutor specifies a mission and provides the details to update.
2. AvengerHub updates the mission and confirms success.

   Use case ends.

**Extensions**

* 1a. The supplied details are invalid or incomplete.

    * 1a1. AvengerHub explains the error.

      Use case resumes at step 1.

* 1b. The specified mission does not exist.

    * 1b1. AvengerHub informs the tutor that the mission does not exist.

      Use case resumes at step 1.

* 1c. The supplied name duplicates another mission's name.

    * 1c1. AvengerHub informs the tutor that the mission name already exists.

      Use case resumes at step 1.

* 2a. The supplied details are unchanged.

    * 2a1. AvengerHub informs the tutor that no changes were made.

      Use case ends.

**Use case: Add mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to add a mission submission record for a specific student.
4. AvengerHub adds the selected student’s mission submission record for the specified mission and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. A mission submission record already exists for the selected student and mission.

    * 3b1. AvengerHub informs the tutor that the submission has already been recorded and leaves the records unchanged.

      Use case ends.

**Use case: Delete mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a mission submission record for a specific student.
4. AvengerHub removes the selected student’s mission submission record for the specified mission and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes at step 3.

* 3b. No mission submission record exists for the selected student and mission.

    * 3b1. AvengerHub informs the tutor that no submission is recorded and leaves the records unchanged.

      Use case ends.

**Use case: List missions and submission statuses**

**MSS**

1. Tutor requests mission summaries or submission statuses, optionally
   specifying a mission and submission status.
2. AvengerHub displays the requested information for the current student list.

   Use case ends.

**Extensions**

* 1a. The supplied details are invalid or incomplete.

    * 1a1. AvengerHub explains the error.

      Use case resumes at step 1.

* 1b. The specified mission does not exist.

    * 1b1. AvengerHub informs the tutor that the mission does not exist.

      Use case resumes at step 1.

* 2a. No missions are configured.

    * 2a1. AvengerHub informs the tutor that no missions are configured.

      Use case ends.

* 2b. Submission statuses are requested, but the current student list is empty.

    * 2b1. AvengerHub informs the tutor that no students are displayed.

      Use case ends.

* 2c. No submission statuses match the requested criteria.

    * 2c1. AvengerHub informs the tutor that no matching submissions were found.

      Use case ends.

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

* **CS1101S**: The National University of Singapore's Programming Methodology course.
* **Mainstream OS**: Windows, Linux, Unix, or macOS.
* **Private student detail**: A student detail that is not meant to be shared with others.
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
  * We expect the application to be stored in a SSD. Loading times may be worse with HDDs
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

### Student records

Run this workflow in a fresh working directory. Start the app and enter
`clear`.

1. Enter `addstudent n/Amy Bee mtc/A0123456D p/98765432 e/amy@example.com g/1A`,
   then `list`.
   Expected: The command reports that Amy Bee was added. The student list
   contains one student in studio group 1A.

1. Enter `addstudent n/Someone Else mtc/A0123456D p/98765432 e/amy@example.com g/1A`,
   then `list`.
   Expected: The command reports that a student with the same matriculation number already exists. The list
   still contains only Amy Bee.

1. Enter `editstudent id/1 p/91234567 e/amybee@example.com`.
   Expected: The command reports that Amy Bee was edited. The profile shows
   phone number `91234567` and email address `amybee@example.com`.

1. Enter `editstudent id/1`.
   Expected: The command reports that at least one field is required. Amy
   Bee's edited phone number and email address remain unchanged.

1. Enter `delstudent id/1`.
   Expected: The command reports that Amy Bee was deleted. The student list
   is empty.

1. Enter `delstudent id/1`.
   Expected: The command reports that the index is invalid. The student list
   remains empty.


### Finding students

Run this workflow independently in a disposable working directory.
Launch the app, enter `clear`, then add these students in order:

```text
addstudent n/Alex Lee mtc/A0123456B p/98765432 e/alex.lee@example.com g/1A
addstudent n/Alex Lim mtc/A0123457C p/98765433 e/alex.lim@example.com g/1B
addstudent n/Brian Tan mtc/A0123458D p/98765434 e/brian.tan@example.com g/1B
```

1. Enter `find aLe`.
   Expected: Alex Lee and Alex Lim appear, confirming case-insensitive
   partial-name matching.

1. Enter `find alex,`.
   Expected: Invalid-command feedback appears. Alex Lee and Alex Lim
   remain displayed.

1. Enter `find lee alex`.
   Expected: Only Alex Lee appears. All space-separated keywords must
   match, regardless of their order.

1. Enter `find g/`.
   Expected: Studio-group validation feedback appears. Alex Lee remains
   displayed.

1. Enter `find alex, brian g/1B`.
   Expected: Alex Lim and Brian Tan appear. Either name alternative may
   match, but the studio group must also match. This search replaces
   the previous search.

1. Enter `find g/1b`.
   Expected: Studio-group validation feedback appears because group
   values are case-sensitive. Alex Lim and Brian Tan remain displayed.

1. Enter `find g/1A g/1B`.
   Expected: All three students appear because either studio group
   may match.

1. Enter `find`.
   Expected: Invalid-command feedback appears. All three students
   remain displayed.

1. Enter `find alex alex`.
   Expected: No students appear because neither Alex has two separate
   occurrences of the keyword.

1. Enter `find nobody`.
   Expected: No students appear.

1. Enter `list`.
   Expected: All three students reappear in their original order,
   with their details unchanged.

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

### Studio records

Run this workflow in a fresh working directory. Start the app, enter `clear`,
then create a student:

```text
addstudent n/Amy Bee mtc/A0123456D p/98765432 e/amy@example.com g/1A
```

1. Enter `addstudiorecord id/1 w/3 ef/30`.
   Expected: The command reports that Amy Bee's studio record for week 3 was
   added. The profile shows week 3 as attended with effort score 30.

1. Enter `addstudiorecord id/1 w/3 ef/20`.
   Expected: The command reports that the week 3 record already exists and
   makes no changes. The profile still shows effort score 30 for week 3.

1. Enter `editstudiorecord id/1 w/3 ef/40`.
   Expected: The command reports that the record was edited. The profile shows
   week 3 as attended with effort score 40.

1. Enter `editstudiorecord id/1 w/3 ef/40`.
   Expected: The command reports that the week 3 record is already the same
   and makes no changes. The profile still shows effort score 40.

1. Enter `editstudiorecord id/1 w/4 ef/40`.
   Expected: The command reports that no week 4 record exists and makes no
   changes. Week 4 remains absent with effort score 0.

1. Enter `delstudiorecord id/1 w/3`.
   Expected: The command reports that the week 3 record was removed. The
   profile shows week 3 as absent with effort score 0.

1. Enter `delstudiorecord id/1 w/3`.
   Expected: The command reports that week 3 is already not recorded and
   makes no changes. The profile still shows week 3 as absent with effort
   score 0.

1. Enter `addstudiorecord id/1 w/14 ef/30`, followed by
   `addstudiorecord id/1 w/3 ef/-1`.
   Expected: Both commands show validation feedback and leave all studio
   records unchanged.

### Notes

Run this workflow independently in a fresh working directory. Start the app,
enter `clear`, then create a student:

```text
addstudent n/Amy Bee mtc/A0123456D p/98765432 e/amy@example.com g/1A
```

1. Enter `addnote id/1 nt/Needs help with recursion`.
   Expected: The command reports that the note was added. The profile lists
   `Needs help with recursion` as note 1.

1. Enter `editnote id/1 ni/1 nt/Understands recursion`.
   Expected: The command reports that note 1 was edited. The profile lists
   `Understands recursion` as note 1.

1. Enter `editnote id/1 ni/2 nt/Should not be added`.
   Expected: The command reports that note 2 does not exist and makes no
   changes. Note 1 remains `Understands recursion`.

1. Enter `delnote id/1 ni/1`.
   Expected: The command reports that note 1 was deleted. The profile no
   longer lists the note.

1. Enter `delnote id/1 ni/1`.
   Expected: The command reports that note 1 does not exist and makes no
   changes. The profile still contains no notes.

1. Enter `addnote id/1 nt/`, followed by `addnote id/2 nt/Valid note`.
   Expected: Both commands show validation feedback and leave the note list
   unchanged.

1. Enter `addnote id/1 nt/First note`, then `addnote id/1 nt/First note`.
   Expected: Both notes are added, and the second command also warns that Amy
   Bee has matching note text. The profile lists two notes with the same text.

1. Add notes 3 through 100, then attempt to add a 101st note. For each note
   from 3 through 100, enter `addnote id/1 nt/Note <number>`, replacing
   `<number>` with the note number. Then enter `addnote id/1 nt/Note 101`:

   ```text
   addnote id/1 nt/Note 3
   ...
   addnote id/1 nt/Note 100
   addnote id/1 nt/Note 101
   ```

   Expected: The first 98 commands create notes 3 through 100. The final
   command reports that Amy Bee already has the maximum of 100 notes and makes
   no changes. The profile still lists exactly 100 notes.

### Mastery checks

Run this workflow independently in a fresh working directory. Start the app,
enter `clear`, then create a student:

```text
addstudent n/Amy Bee mtc/A0123456D p/98765432 e/amy@example.com g/1A
```

1. Enter `addmc id/1 num/1 dt/2026-10-20|12:30`.
   Expected: The command reports that the mastery check was added. The profile lists a mastery check numbered 1 with appointment time `20 october 2026, 12:30` as "Not Completed".
2. Enter `markmc id/1 num/1`.
   Expected: The command reports that the mastery check has been marked complete. The profile shows the same mastery check as in [1] has been marked as "Completed".
3. Enter `unmarkmc id/1 num/1`.
   Expected: The command reports that the mastery check has been marked incomplete. The profile shows the same mastery check as in [1] and [2] has been marked as "Not completed".
4. Enter `delmc id/1 num/1`.
   Expected: The command reports that the mastery check was removed. The profile shows "No mastery checks registered."

Additional testing:

**Commands executed with no changes made**
1. Add an arbitrary mastery check, then add another mastery check with the same number.
2. Add an arbitrary mastery check, then unmark it.
3. Add an arbitrary mastery check, mark it, then mark it again.
4. Delete a mastery check with a number that has not been recorded.

**Error handling**
1. Add an arbitrary mastery check with a wrongly-formatted date-time, eg. `dt/2026-10-30T14:30`. Expected: The command is rejected for invalid datetime formatting.
2. Add an arbitrary mastery check with an impossible date, eg. `dt/2026-13-39|12:30`. Expected: The command is rejected for invalid date value.
3. Add an arbitrary mastery check with an impossible time, eg. `dt/2026-11-03|25:00`. Expected: The command is rejected for invalid time value.
4. Mark a mastery check with a number that has not been recorded. Expected: The command is rejected for attempting to modify a mastery check that does not exist.
5. Unmark a mastery check with a number that has not been recorded. Expected: Same as in [4].

### Saving data and clearing records

1. Add a note, a mastery check, a studio record, a mission, a mission submission, close and relaunch
   the app from the same working directory. Enter `view id/1`.
   Expected: The saved students, missions, studio records, notes, mastery
   checks, and submissions are restored.

1. In a disposable working directory, enter `clear`, then `list` and
   `listmissions m/M01`.
   Expected: The student list is empty and the mission lookup reports that
   M01 does not exist.
