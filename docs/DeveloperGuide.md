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

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delstudent 1`.

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

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `StudentListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Student` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delstudent 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delstudent 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteStudentCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
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

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Student` objects (which are contained in a `UniqueStudentList` object).
* stores the `Student` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Student>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Student` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Student` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-F14a-1/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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
| `* * *`  | TA                             | mark a student as present or absent for a studio                  | maintain an accurate attendance record                                               |
| `* * *`  | TA                             | mark whether a student submitted a given mission                         | track completion without cross-checking another system                               |
| `* * *`  | TA                             | record a student's mark for a given assessment                           | keep their performance data alongside other student records                          |
| `* * *`  | TA with multiple studio groups | see which studio group a student belongs to                              | avoid mixing up students across studio groups                                        |
| `* *`    | TA                             | view a summary of a student's attendance, participation, and submissions | understand their overall engagement without checking multiple records                |

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


**Use case: View student profile**

**MSS**

1. Tutor requests to view or search student records.
2. AvengerHub displays the corresponding student records.
3. Tutor selects a student record to view.
4. AvengerHub displays the student's details, attendance records,
   and mission submission records.

   Use case ends.

**Extensions**

* 2a. No student records are displayed.

  Use case ends.

* 3a. The student selection is invalid or incomplete.

    * 3a1. AvengerHub explains the error.

      Use case resumes at step 3.

* 4a. Some student data is unavailable or corrupted.

    * 4a1. AvengerHub displays the available details.

      Use case ends.


**Use case: Mark attendance record**

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

**Use case: Unmark attendance record**

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

**Use case: Add mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to add a mission submission record for a specific student in the list for a specified tutorial week.
4. AvengerHub adds the selected student’s mission submission record for the specified week and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. A mission submission record already exists for the selected student and week.

    * 3b1. AvengerHub informs the tutor that the submission has already been recorded and leaves the records unchanged.

      Use case ends.

**Use case: Delete mission submission record**

**MSS**

1. Tutor requests to list students.
2. AvengerHub shows the list of students.
3. Tutor requests to delete a mission submission record for a specific student in the list for a specified tutorial week.
4. AvengerHub removes the selected student’s mission submission record for the specified week and confirms success.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The tutor provides invalid or incomplete details.

    * 3a1. AvengerHub shows an error message and leaves the records unchanged.

      Use case resumes from step 3.

* 3b. No mission submission record exists for the selected student and week.

    * 3b1. AvengerHub informs the tutor that no submission is recorded and leaves the records unchanged.

      Use case ends.

*{More to be added}*

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
15. If one stored record does not adhere to the defined format, the system should report the problem while loading all other valid records.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private student detail**: A student detail that is not meant to be shared with others
* **AvengerHub**: The name of the application.
* **Tutor**: The TAs (teaching assistants) in the CS1101S course that handle the tutorial sessions. Also known as "Avengers" by the course.
* **Mission**: The CS1101S term for homework assignments. A mission submission usually happens via the course website.
* **Studio group**: The CS1101S term for tutorial groups.
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

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample students. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command, with multiple students in the list.

   1. Test case: `delstudent 1`<br>
      Expected: The first student is deleted from the list. The status message shows the deleted student's details.

   1. Test case: `delstudent 0`<br>
      Expected: No student is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delstudent`, `delstudent x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
