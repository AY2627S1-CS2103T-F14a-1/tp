---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AvengerHub User Guide

AvengerHub is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AvengerHub can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AvengerHub.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all students.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd student shown in the current list.

   * `clear` : Deletes all students.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Prefixed parameters can be in any order unless a command states otherwise. An `INDEX`, when required, comes first.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a student: `add`

Adds a student to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... `

<box type="tip" seamless>

**Tip:** A student can have any number of tags, including zero.
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all students: `list`

Shows a list of all students in the address book.

Format: `list`

### Editing a student: `edit`

Edits an existing student in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the student at the specified `INDEX`. The index refers to the index number shown in the displayed student list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the student's existing tags are removed; adding tags is not cumulative.
* To remove all of a student's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st student to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd student to be `Betsy Crower` and clears all existing tags.

### Locating students by name: `find`

Finds students whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search considers only student names.
* Matching is case-insensitive.
* Partial matches count: `ale` matches `Alex`.
* Students matching at least one keyword are returned (an OR search).
* Keyword order does not matter.
* Repeated keywords do not change the results.

Examples:
* `find ale` returns `Alex Yeoh` and `Alexander Tan`.
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a student: `delete`

Deletes the specified student from the address book.

Format: `delete INDEX`

* Deletes the student at the specified `INDEX`.
* The index refers to the index number shown in the displayed student list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd student in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st student in the results of the `find` command.

### Adding a mission submission record: `addmission`

Records that a student has submitted a mission for a tutorial week.

Format: `addmission INDEX w/WEEK`

* `INDEX` identifies a student in the **currently displayed list**, including results from `find`. It must be a positive integer that corresponds to a displayed student.
* `WEEK` must be an integer from **3 to 13**, inclusive. AvengerHub uses these week numbers because the tutorial schedule it supports starts in week 3 and ends in week 13. Use the actual week number: the first tutorial week is `3`, not `1`.
* Weeks outside this range, such as `1`, `2`, or `14`, are rejected with an error message. Existing submission records remain unchanged.
* Both arguments are required. Put `INDEX` before `w/WEEK`, and specify `w/` exactly once. Extra arguments are rejected.
* Enter `INDEX` and `WEEK` as integers without signs, decimal points, or leading zeros. For example, use `3`, not `+3`, `3.0`, or `03`.
* The command word and `w/` prefix are case-insensitive: `ADDMISSION 1 W/3` is also accepted.
* Each student can have one submission record per week. Adding an existing record leaves the records unchanged and displays a message explaining that the submission is already recorded.
* Records for other weeks and students are preserved. The current list filter stays active.

Examples:

* `list` followed by `addmission 1 w/3` records a week 3 submission for the first student displayed.
* `find Betsy` followed by `addmission 1 w/13` records a week 13 submission for the first student in the search results, if any.

For example, if the first student is Amy Bee and her week 3 submission is not yet recorded, `addmission 1 w/3` displays:

```text
Added Amy Bee’s mission submission for tutorial week 3.
```

### Deleting a mission submission record: `delmission`

Removes a student's mission submission record for a tutorial week. The student remains in the address book.

Format: `delmission INDEX w/WEEK`

* `INDEX` identifies a student in the **currently displayed list**, including results from `find`. It must be a positive integer that corresponds to a displayed student.
* `WEEK` must be an integer from **3 to 13**, inclusive. AvengerHub uses these week numbers because the tutorial schedule it supports starts in week 3 and ends in week 13. Use the actual week number: the first tutorial week is `3`, not `1`.
* Weeks outside this range, such as `1`, `2`, or `14`, are rejected with an error message. Existing submission records remain unchanged.
* Both arguments are required. Put `INDEX` before `w/WEEK`, and specify `w/` exactly once. Extra arguments are rejected.
* Enter `INDEX` and `WEEK` as integers without signs, decimal points, or leading zeros.
* The command word and `w/` prefix are case-insensitive: `DELMISSION 1 W/3` is also accepted.
* If the submission was initially not recorded, the records remain unchanged and a message explains that no changes were made.
* Only the specified week's record is removed. Other weeks, student details, and the current list filter are preserved.
* Deleting the last submission leaves the student with no recorded mission submissions.

Examples:

* `list` followed by `delmission 1 w/3` removes the first student's week 3 submission record, if recorded.
* `find Betsy` followed by `delmission 1 w/13` removes the week 13 submission record for the first student in the search results, if both the student and record exist.

For example, if the first student is Amy Bee and her week 3 submission is recorded, `delmission 1 w/3` displays:

```text
Removed Amy Bee’s mission submission for tutorial week 3.
```

<box type="tip" seamless>

To correct a submission recorded for the wrong week, use `delmission` to remove the incorrect record, then `addmission` to record the correct week.
</box>

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AvengerHub automatically saves data after every command. You do not need to save manually.

Mission submission additions and deletions are saved automatically and retained when you reopen the application. Invalid mission commands leave existing records unchanged.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... ` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Add mission submission** | `addmission INDEX w/WEEK`<br> e.g., `addmission 1 w/3`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Delete mission submission** | `delmission INDEX w/WEEK`<br> e.g., `delmission 1 w/3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Help**   | `help`
