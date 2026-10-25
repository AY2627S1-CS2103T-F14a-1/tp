# UI Test Plan

## Notes details panel

Use two students: student 1 has note IDs 5 and 2, added in that order, while
student 2 has no notes.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-VIEW-001 | Run `view id/1`. | A Notes table appears with `ID` and `Note` headers. Notes 2 and 5 appear in ascending ID order with their full text. |
| NOTE-VIEW-002 | Run `view id/2`. | The Notes table retains its headers and shows `No notes recorded.` across both columns. No notes from student 1 remain visible. |
| NOTE-VIEW-003 | Run `view id/1` again. | Notes 2 and 5 reappear once each, without duplicated or stale rows. |
| NOTE-VIEW-004 | Resize the window from narrow to wide and back while viewing student 1. | Note text wraps within the Notes table and no horizontal scrollbar appears. |

## Edit-note command regression

Start with two students. Give student 1 note IDs 3 and 4, and leave student 2
unchanged. After every rejected command, run `view id/1` and confirm that both
notes and the saved data are unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-EDIT-001 | Run `editnote id/1 ni/3 nt/Updated lecture notes`, then view both students. | Success feedback identifies student 1 and note ID 3. Student 1's ID 3 has the new text, ID 4 is unchanged, and student 2 is unchanged. |
| NOTE-EDIT-002 | Run `editnote id/1 ni/3 nt/Updated lecture notes` again, then view student 1. | Same-note feedback appears. Both note texts remain unchanged. |
| NOTE-EDIT-003 | Run `editnote id/1 ni/2 nt/Unexpected`, then view student 1. | Missing-note feedback appears. IDs 3 and 4 remain unchanged. |
| NOTE-EDIT-004 | Run `editnote id/1 ni/0 nt/Invalid`, `editnote id/1 ni/3 nt/Invalid!`, and `editnote id/1 ni/3`, viewing student 1 after each command. | The invalid-ID, invalid-text, and missing-`nt/` messages appear respectively. No note changes. |
| NOTE-EDIT-005 | Run `editnote id/3 ni/3 nt/Unexpected`, then view both students. | Invalid-index feedback appears; neither student changes. |
| NOTE-EDIT-006 | Run `editnote id/1 ni/3 ni/4 nt/Unexpected`, then view student 1. | Duplicate-`ni/` feedback appears. Both note texts and saved data remain unchanged. |
| NOTE-EDIT-007 | Run `editnote id/1 ni/3 nt/Persisted update`, restart, and view student 1. | ID 3 retains `Persisted update`; ID 4 and student 2 remain unchanged. |

## Add-note command regression

Start with two students and no notes for student 1. After every rejected
command, run `view id/1` and confirm that the note list and saved data are
unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-ADD-001 | Run `addnote id/1 ni/3 nt/Review lecture notes`, then view student 1. | Success feedback names student 1 and note ID 3. The note list contains `3: Review lecture notes`; student 2 is unchanged. |
| NOTE-ADD-002 | Run `addnote id/1 ni/3 nt/Different text`, then view student 1. | Duplicate-ID feedback appears. ID 3 still contains `Review lecture notes`. |
| NOTE-ADD-003 | Run `addnote id/1 ni/0 nt/Invalid`, then `addnote id/1 ni/4 nt/Invalid!`, viewing student 1 after each command. | The invalid ID and invalid-text messages appear respectively. ID 4 is not added and ID 3 remains unchanged. |
| NOTE-ADD-004 | Run `addnote id/1 ni/4`, then `addnote id/1 ni/4 nt/Valid ni/5`, viewing student 1 after each command. | The first command reports missing `nt/`; the second reports duplicate `ni/`. No note is added. |
| NOTE-ADD-005 | Run `addnote id/3 ni/4 nt/Invalid index`, then view both students. | Invalid-index feedback appears; neither student changes. |
| NOTE-ADD-006 | Restart after NOTE-ADD-001 and view student 1. | Note ID 3 and its text are restored unchanged. |

## Delete-note command regression

Start with two students. Give student 1 note IDs 3 and 4, and leave student 2 unchanged.
After every rejected command, run `view id/1` and confirm that both notes and saved data are unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-DELETE-001 | Run `delnote id/1 ni/3`, then view both students. | Success feedback identifies student 1 and note ID 3. Student 1 retains only note ID 4; student 2 is unchanged. |
| NOTE-DELETE-002 | Run `delnote id/1 ni/3` again, then view student 1. | Missing-note feedback appears. Note ID 4 remains unchanged. |
| NOTE-DELETE-003 | Run `delnote id/1 ni/0`, `delnote id/1`, and `delnote 1 ni/4`, viewing student 1 after each command. | The invalid-ID, missing-`ni/`, and invalid-format messages appear respectively. Neither note changes. |
| NOTE-DELETE-004 | Run `delnote id/3 ni/4`, then view both students. | Invalid-index feedback appears; no student changes. |
| NOTE-DELETE-005 | Run `delnote id/1 ni/4`, restart, and view student 1. | The Notes table shows the empty-state message. The deletion persists across restart. |
| NOTE-DELETE-006 | Run `delnote id/1 ni/3 ni/4`, then view student 1. | Duplicate-`ni/` feedback appears. Both notes and the saved data remain unchanged. |

## Mission regression sequence

Use a temporary data directory with two students and an empty catalogue. After each rejected
command, run `view 1` and compare the catalogue and submission statuses with the previous state.
Inspect the saved file before running that verification command to ensure rejection did not save.

| ID | Steps | Expected result |
| --- | --- | --- |
| MISSION-001 | Run `addmission n/`, then `addmission n/Runes`. | Request a name using `n/NAME`, then a deadline in `YYYY-MM-DD` using `d/DEADLINE`; no mission is added. |
| MISSION-002 | Copy the example from addmission usage, then view student 1. | Creates M01, Rune Reading, due 2026-10-20, not submitted. |
| MISSION-003 | Run `addmission n/ RUNE   READING d/2026-10-21`. | Duplicate feedback; M01 and the next ID remain unchanged. |
| MISSION-004 | Run `addmission n/Curves d/2026-10-21`, then `editmission m/M02 n/Rune Reading`. | M02 is added; the duplicate edit is rejected. |
| MISSION-005 | Run `markmission 1 m/m01`, repeat it, then `unmarkmission 1 m/M99`. | First marks M01, repeat reports no change, unknown ID reports `Mission M99 does not exist.`; student 2 remains unchanged. |
| MISSION-006 | Filter to student 1; run `listmissions m/M01` and `listoverdue`. | Submitted list uses displayed index 1. Overdue excludes M01 and includes only missing missions due before the local date. Hidden students are excluded. |
| MISSION-007 | Run `editmission m/M01 n/New Runes d/2026-10-22`, then restart and view student 1. | ID and submission survive; name and deadline are updated and persisted. |
| MISSION-008 | Run `unmarkmission 1 m/M01`, then an invalid date edit `editmission m/M01 d/2026-02-30`; restart. | M01 stays not submitted; invalid edit leaves its name/deadline unchanged. |
| MISSION-009 | Mark M01 for both students; filter to one student; delete M01. Restart, run `list`, and inspect both profiles. Add New Runes again. | Mission and submissions are removed from both students. Recreated mission uses M03 and has no submissions. |
| MISSION-010 | Back up data. In an isolated fixture, give two missions different IDs but equal names ignoring case/spaces. Start the app, inspect file, then run `list`. | Empty-address-book fallback; startup preserves invalid file, successful list saves the empty book. |
| MISSION-011 | In an isolated valid fixture set `nextMissionNumber` to 9223372036854775807. Run `addmission n/Limit d/2026-10-20`. | `No further mission IDs can be allocated.`; memory and file remain unchanged. |

## Studio parsing regression

Studio argument parsing is most precisely checked at the parser level. Run the
complete `ParserUtilTest`,
`AddStudioRecordCommandParserTest`, and `DeleteStudioRecordCommandParserTest`
JUnit classes. Verify that missing required prefixes show their prefix-specific messages,
duplicate prefixes are rejected, weeks outside 0 to 13 are rejected, and
effort scores accept only unsigned integers from 0 to 2147483647. Then run the
positive and negative studio command cases below to verify the same behavior
through the UI without changing student state after invalid input.

## Integer validation regression

Integer validation is not exposed independently through the UI. Run the JUnit
cases named `isNonNegativeUnsignedInteger_*` in `StringUtilTest` to verify that
the utility accepts zero and positive integers while rejecting null, signed,
malformed, whitespace-containing, and overflowing values.

## Constructor validation regression

Null studio-record values cannot be entered through the UI. Run the JUnit case
`RegisteredStudioRecordsTest.constructor_nullStudioRecord_throwsNullPointerException`
to verify that the constructor rejects them. Use the studio command and
persistence cases below to check that valid records still behave normally.

## Equality and hashing regression

Hash codes are not exposed through the UI. Run the `hashCode_*` JUnit cases in
`StudioRecordTest` and `RegisteredStudioRecordsTest` to verify equal values have equal
hash codes, including empty records, boundary scores, and different insertion
orders. Hash-set cases interleave successful lookups with unequal-score and
unequal-number lookups, then verify equal values are not inserted twice.
Run the complete UI plan below to check existing studio behavior.

## Studio-record summary

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-SUMMARY-001 | View a student with records for weeks 3, 4, and 13, including an effort score of 0 for week 4. | The summary lists every week from 3 to 13 under `Week`, `Attendance`, and `Effort`. Recorded weeks show `Present` and their saved effort score, including 0; weeks without a record show `Absent` and effort score `0`. |
| STUDIO-LABEL-001 | View the student list after adding a studio record. | The student card prefixes the weekly record summary with `Studio records:` and does not expose a Java class name. |
| STUDIO-LAYOUT-001 | View a student with a week 13 effort score of 2147483647. Resize the window from narrow to wide and back. | All three columns and their headers remain inside the studio table. Effort values remain readable, wrapping when necessary, without horizontal scrolling. JUnit also checks every cell at table widths of 240 and 900 pixels. |
| STUDIO-SWITCH-001 | View a student with week 3 effort 50, then view another student without studio records, then view the first student again. | The second student shows every week as `Absent` with effort `0`. Returning to the first student restores `Present` and effort `50` for week 3, without duplicated rows. |
| STUDIO-PERSISTENCE-001 | Add studio records with different effort scores, exit the application, inspect the saved student record, restart the application, and view the same student. | The saved JSON uses the `registeredStudioRecords` property. Each recorded week and its corresponding effort score are restored unchanged. |

## Studio commands

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-ADD-001 | Run `addstudiorecord 1 w/3 ef/50`, then view student 1. | Week 3 is attended with effort score 50. Other weeks and students remain unchanged. |
| STUDIO-ADD-002 | Run `addstudiorecord 1 w/4 ef/-1`, then view student 1. | Invalid-effort feedback appears; week 4 remains absent. |
| STUDIO-ADD-003 | Run `addstudiorecord 1 w/13 ef/0`, then view student 1. | Week 13 is attended with effort score 0. |
| STUDIO-ADD-004 | Run `addstudiorecord 1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; number 3 retains effort score 50. |
| STUDIO-EDIT-001 | Run `editstudiorecord 1 w/3 ef/75`, then view student 1. | Success feedback appears; week 3 remains attended with effort score 75, while week 13 remains attended with effort score 0. |
| STUDIO-EDIT-002 | Run `editstudiorecord 1 w/3 ef/75`, then view student 1. | Already-the-same feedback appears; week 3 retains effort score 75. |
| STUDIO-EDIT-003 | Run `editstudiorecord 1 w/4 ef/20`, then view student 1. | Not-recorded feedback appears; week 4 remains absent. |
| STUDIO-DELETE-001 | Run `delstudiorecord 1 w/3`, then view student 1. | Week 3 is absent with effort score 0; week 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord 1 w/14`, then view student 1. | Invalid-number feedback appears; records remain unchanged. |
| STUDIO-ADD-001 | Run `addstudiorecord id/1 w/3 ef/50`, then view student 1. | Week 3 is attended with effort score 50. Other weeks and students remain unchanged. |
| STUDIO-ADD-002 | Run `addstudiorecord id/1 w/4 ef/-1`, then view student 1. | Invalid-effort feedback appears; week 4 remains absent. |
| STUDIO-ADD-003 | Run `addstudiorecord id/1 w/13 ef/0`, then view student 1. | Week 13 is attended with effort score 0. |
| STUDIO-ADD-004 | Run `addstudiorecord id/1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; number 3 retains effort score 50. |
| STUDIO-DELETE-001 | Run `delstudiorecord id/1 w/3`, then view student 1. | Week 3 is absent with effort score 0; number 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord id/1 w/14`, then view student 1. | Invalid-number feedback appears; records remain unchanged. |
| STUDIO-SEQUENCE-001 | Start with two students, student 1 having no studio records. Run STUDIO-ADD-001, STUDIO-ADD-002, and STUDIO-ADD-003 in order, viewing student 1 after each command. Restart, then run STUDIO-ADD-004, STUDIO-DELETE-001, and STUDIO-DELETE-002, viewing student 1 after each command. Restart and view both students. | Each command produces the feedback and state above. The duplicate retains number 3 effort 50 despite requesting 100. After the final restart, only number 13 is recorded with effort 0; number 3 remains absent. Other student fields and student 2 remain unchanged throughout. The matching LogicManagerTest sequence checks exact feedback, in-memory state, reloaded state after every command, and unchanged saved JSON after rejected and duplicate commands. |

## Mastery-check datetime parsing regression

Run the `parseMasteryCheckDateTime_*` cases in `ParserUtilTest` for parser-level coverage.
The following sequence uses the current-year DD-MM|HH:MM input format.
After each rejected command, view the student and verify existing appointments are unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| MC-DATETIME-001 | Add an appointment using `28-02\|00:00`; in a leap year also use `29-02\|00:00`. | The appointment is accepted at midnight in the current year; February 29 is accepted only in leap years. |
| MC-DATETIME-002 | Attempt another appointment using `30-02\|12:30` or `31-04\|12:30`, then view the student. | Invalid-datetime feedback; existing appointments remain unchanged. |
| MC-DATETIME-003 | Add another appointment using `31-12\|23:59`. | The appointment is accepted at 23:59. |
| MC-DATETIME-004 | Attempt appointments with `24:00`, `23:60`, a missing or repeated separator, or seconds; view the student after each attempt. | Invalid-datetime feedback; existing appointments remain unchanged. |
| MC-DATETIME-005 | Add an appointment with surrounding whitespace around `20-10\|09:05`. | Surrounding whitespace is trimmed and the appointment is accepted. |

## Mastery-check registration and details regression

The mastery-check JUnit suites cover model validation, defensive map copying, completion
transitions, command parsing and execution, student equality, JSON validation, real-file
round trips, and preservation by other commands. JavaFX cases run on the JavaFX thread.

| ID | Steps | Expected result |
| --- | --- | --- |
| MC-ADD-001 | Start with two students. Run `addmc id/1 num/1 dt/18-08\|14:30`, then view student 1. | Feedback identifies appointment 1 at `Aug 18, 14:30`. The grid has Number, Appointment time, and Status columns and shows Not completed. Student 2 is unchanged. |
| MC-ADD-002 | Run `addmc id/1 num/2 dt/31-04\|14:30`, then view student 1. | Invalid-datetime feedback; appointment 1 and saved data are unchanged. |
| MC-ADD-003 | Run `addmc id/1 num/2 dt/19-08\|00:05`, exit, restart, and view student 1. | Numbers 1 and 2 appear in ascending order with the saved times. |
| MC-ADD-004 | Run `addmc id/1 num/1 dt/20-08\|15:00`, then view student 1. | Duplicate-number feedback; number 1 retains its original time and status. |
| MC-STATE-001 | Edit a student, add/delete a studio record, mark/unmark a mission, and delete the mission. Restart and view the student. | Mastery-check numbers, appointment times, and completion statuses remain unchanged. |
| MC-VIEW-001 | View a student with appointments, then a student without appointments, then the first student again. | The empty student shows the headers and No mastery checks registered. Returning restores only the correct rows, without duplicates. |
| MC-STORAGE-001 | Load a fixture containing completed and incomplete mastery checks, save, and reload. Separately attempt to load malformed number, datetime, and status records. | Valid times, including seconds and fractional seconds, and completion flags survive reloading. Malformed files produce a loading error and remain unchanged. |

## Delete mastery check regression

JUnit follows the studio-record deletion suites and checks command state, parser errors,
case-insensitive dispatch, and real-file persistence after accepted and rejected commands.
The number-validation and usage-message cases also expose current copy-paste defects.

| ID | Steps | Expected result |
| --- | --- | --- |
| MC-DELETE-001 | Start with two students and student 1 having appointments 1 and 2. Run `delmc id/1 num/1`, then view both students. | Appointment 1 is removed; appointment 2 retains its time and status. Other student fields and student 2 are unchanged. |
| MC-DELETE-002 | Run `delmc id/1 num/0`, then view student 1 and inspect saved data. | Invalid-number feedback; appointments and saved data remain unchanged. |
| MC-DELETE-003 | Restart and run `delmc id/1 num/1`. | Not-found feedback; saved data and remaining appointments are unchanged. |
| MC-DELETE-004 | Run `delmc id/3 num/2` when only two students exist, then view both students. | Invalid-index feedback; both students and saved data are unchanged. |
| MC-DELETE-005 | Run `delmc id/1 num/2`, restart, and view student 1. | No appointments remain; the empty-table message appears. Other records remain intact. |
| MC-DELETE-006 | Register numbers 14 and 2147483647, then delete each by number. | Every positive mastery check number can be deleted; studio-week limits do not apply. |
| MC-DELETE-007 | Run `delmc`, then `delmc id/1`, viewing the student after each attempt. | The first command lists missing `id/` and `num/` messages in that order; the second lists only missing `num/`. Student state and saved data remain unchanged. |
| STUDIO-ADD-004 | Run `addstudiorecord id/1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; number 3 retains effort score 50. |
| STUDIO-DELETE-001 | Run `delstudiorecord id/1 w/3`, then view student 1. | Week 3 is absent with effort score 0; number 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord id/1 w/14`, then view student 1. | Invalid-number feedback appears; records remain unchanged. |
| STUDIO-SEQUENCE-001 | Start with two students, student 1 having no studio records. Run STUDIO-ADD-001, STUDIO-ADD-002, and STUDIO-ADD-003 in order, viewing student 1 after each command. Restart, then run STUDIO-ADD-004, STUDIO-DELETE-001, and STUDIO-DELETE-002, viewing student 1 after each command. Restart and view both students. | Each command produces the feedback and state above. The duplicate retains number 3 effort 50 despite requesting 100. After the final restart, only number 13 is recorded with effort 0; number 3 remains absent. Other student fields and student 2 remain unchanged throughout. The matching LogicManagerTest sequence checks exact feedback, in-memory state, reloaded state after every command, and unchanged saved JSON after rejected and duplicate commands. |

## Prefix structure regression

Run `PrefixTest`, `ArgumentMultimapTest`, `ArgumentStructureValidatorTest`,
`RequiredPrefixCommandParserTest`, and `ParserUtilTest` with the full Gradle suite.

| ID | Steps | Expected result |
| --- | --- | --- |
| PREFIX-001 | With two students, run `addstudiorecord id/1 w/3 ef/50`, then `view id/1`. | Week 3 records effort 50. |
| PREFIX-002 | Run `editstudiorecord id/1 w/3`, then `view id/1`. | Missing `ef/` message; effort remains 50. |
| PREFIX-003 | Run `editstudiorecord ef/75 w/3 id/1`, then `view id/1`. | Week 3 now records effort 75. |
| PREFIX-004 | Run `editstudiorecord id/1`, then `view id/1`. | Missing `w/` and `ef/` messages appear on separate lines in that order; effort remains 75. |
| PREFIX-005 | Run `editstudiorecord id/1 w/3 ef/75 ef/100`, then `view id/1`. | Duplicate `ef/` message; effort remains 75. |
| PREFIX-006 | Run `editstudiorecord id/1 w/3 ef/`, then `view id/1`. | Invalid-effort message; effort remains 75. |
| PREFIX-007 | Run `editstudiorecord 1 w/3 ef/100`, then `view id/1`. | Invalid-command-format message rejects the preamble; effort remains 75. |
| PREFIX-008 | Restart and run `view id/1`, then view student 2. | Student 1 retains week 3 effort 75; student 2 is unchanged. |

## Mission parser removal and edit/prefix branch regression

Run the full Gradle suite and `coverage` with Java 25 and assertions enabled.
Verify 100% JaCoCo branch coverage for `EditCommandParser` and `Prefix`.
`PrefixTest` exercises null-backed hashing and every equality path. The private
edit-tag assertion is tested through reflection because tokenizer-produced tag
collections are never null; this internal invariant has no UI input equivalent.

| ID | Steps | Expected result |
| --- | --- | --- |
| EDIT-PREFIX-001 | Run `edit id/1 n/Updated Name`, then `view id/1`. | The first student has the new name. |
| EDIT-PREFIX-002 | Run `edit unexpected id/1 n/Rejected Name`, then `view id/1`. | Invalid-format feedback; the first student retains Updated Name. |
| EDIT-PREFIX-003 | Run `edit id/1 t/`, then `view id/1`. | Tags are cleared; other fields are unchanged. |
| EDIT-PREFIX-004 | Run `edit id/1 id/2 n/Rejected Name`, then view both students. | Duplicate-index feedback; both students remain unchanged. |
| MISSION-EDIT-001 | Create a mission with `addmission n/Runes d/2026-10-20`, then edit its returned ID using `editmission m/ID n/New Runes`. | Editing succeeds through ParserUtil without MissionParser; the mission keeps its ID and deadline. |

## Missing-prefix usage suffix regression

Missing-prefix feedback now ends with one newline followed by the calling command's
complete usage text. Existing missing-prefix cases must also verify this suffix;
duplicate-prefix and value-validation messages also end with the relevant command usage exactly once.

| ID | Steps | Expected result |
| --- | --- | --- |
| MISSING-USAGE-001 | Run `addmc id/1`, then `view id/1`. | Missing number and timing messages appear in prefix order, followed by the complete addmc usage exactly once. Student records remain unchanged. |
| MISSING-USAGE-002 | Run `addmc id/1 num/1 dt/27-09\|23:59`, then `view id/1`. | Valid syntax is accepted; the record is added or existing-record feedback appears. |
| MISSING-USAGE-003 | Run `addmc id/1 num/1`, then `view id/1`. | The missing timing message is followed by addmc usage exactly once; existing records remain unchanged. |

## Optional-prefix structure regression

Run `ArgumentStructureValidatorTest` for required-prefix checks, optional absence,
nonrepeatable duplicates, repeatable tags, empty values, and validation priority.
The shared validator returns problem-only messages; command parsers append usage.

| ID | Steps | Expected result |
| --- | --- | --- |
| OPTIONAL-001 | Run `edit id/1 n/Updated Name`, then `view id/1`. | Name changes; omitted phone, email, studio group, and tags remain unchanged. |
| OPTIONAL-002 | Run `edit n/Rejected Name`, then `view id/1`. | Missing-index feedback with edit usage; student state is unchanged. |
| OPTIONAL-003 | Run `edit id/1 t/first t/second`, then `view id/1`. | Both tags are retained. |
| OPTIONAL-004 | Run `edit id/1 p/91234567 p/98765432`, then `view id/1`. | Duplicate-phone feedback with edit usage; phone and other fields remain unchanged. |
| OPTIONAL-005 | Run `edit id/1 t/`, then `view id/1`. | Tags are cleared; other fields remain unchanged. |

## Command parser error usage regression

Run all parser tests and the full Gradle suite. Exact error assertions must include
one newline and the complete command usage after the problem message, while direct
ParserUtil and ArgumentMultimap tests continue to expect problem-only messages.

| ID | Steps | Expected result |
| --- | --- | --- |
| ERROR-USAGE-001 | Run `edit id/1 p/invalid`, then `view id/1`. | Phone validation feedback ends with edit usage exactly once; student fields are unchanged. |
| ERROR-USAGE-002 | Run `edit id/1 p/91234567`, then `view id/1`. | Phone updates successfully; other fields remain unchanged. |
| ERROR-USAGE-003 | Run `edit id/1 p/91234567 p/98765432`, then `view id/1`. | Duplicate-phone feedback ends with edit usage exactly once; the phone remains 91234567. |

Preamble-error regression: commands using the shared structure validator must
show `Invalid command format!` followed by their usage exactly once, without a
literal `%1$s` placeholder. Keep the intentional invalid-preamble cases above.


## Mastery-check number error regression

Run the complete ParserUtilTest and all four mastery-check command parser test suites.
Non-positive integers (0, -1, and -2147483648) must report MESSAGE_CONSTRAINTS.
Blank, malformed, and overflowing values must report MESSAGE_NON_NUMBER.
Command parser errors must append the corresponding command usage.

| ID | Steps | Expected result |
| --- | --- | --- |
| MC-NUMBER-001 | Register appointment 1 using `addmc id/1 num/1 dt/18-08|14:30`, then view the student. | Appointment 1 is registered and incomplete. |
| MC-NUMBER-002 | Run each of addmc, delmc, markmc, and unmarkmc with `id/1 num/0` and `id/1 num/-1`; include a valid dt/ argument for addmc. Inspect saved data and view the student after each attempt. | Each error identifies a non-positive integer and includes its command usage. Records and saved data are unchanged. |
| MC-NUMBER-003 | Run `markmc id/1 num/1`, then view the student. | Appointment 1 is completed. |
| MC-NUMBER-004 | Repeat the four-command rejection sequence with blank num/, abc, 1.5, and 2147483648. Inspect saved data and view the student after each attempt. | Each error uses the non-integer message and includes its command usage. Appointment 1 remains completed; other records and saved data are unchanged. |
| MC-NUMBER-005 | Run `unmarkmc id/1 num/1`, then view the student. | Appointment 1 returns to incomplete with its original time. |
