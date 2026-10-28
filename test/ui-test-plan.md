# UI Test Plan

## Notes details panel

Use two students. Add `First note` and then `Second note` to student 1, while
student 2 has no notes.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-VIEW-001 | Run `view id/1`. | A Notes table appears with `ID` and `Note` headers. Index 1 shows `First note` and index 2 shows `Second note`. |
| NOTE-VIEW-002 | Run `view id/2`. | The Notes table retains its headers and shows `No notes recorded.` across both columns. No notes from student 1 remain visible. |
| NOTE-VIEW-003 | Run `view id/1` again. | Both notes reappear once each in insertion order, without duplicated or stale rows. |
| NOTE-VIEW-004 | Resize the window from narrow to wide and back while viewing student 1. | Note text wraps within the Notes table and no horizontal scrollbar appears. |

## Edit-note command regression

Start with two students. Give student 1 two notes at indexes 1 and 2, and leave
student 2 unchanged. After every rejected command, run `view id/1` and confirm that both
notes and the saved data are unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-EDIT-001 | Run `editnote id/1 ni/1 nt/Updated lecture notes`, then view both students. | Success feedback identifies student 1 and note index 1. Index 1 has the new text, index 2 is unchanged, and student 2 is unchanged. |
| NOTE-EDIT-002 | Run `editnote id/1 ni/1 nt/Updated lecture notes` again, then view student 1. | Same-note feedback appears. Both note texts remain unchanged. |
| NOTE-EDIT-003 | Run `editnote id/1 ni/3 nt/Unexpected`, then view student 1. | Missing-note feedback appears. Indexes 1 and 2 remain unchanged. |
| NOTE-EDIT-004 | Run `editnote id/1 ni/0 nt/Invalid`, `editnote id/1 ni/1 nt/Invalid!`, and `editnote id/1 ni/1`, viewing student 1 after each command. | The invalid-index, invalid-text, and missing-`nt/` messages appear respectively. No note changes. |
| NOTE-EDIT-005 | Run `editnote id/3 ni/1 nt/Unexpected`, then view both students. | Invalid-student-index feedback appears; neither student changes. |
| NOTE-EDIT-006 | Run `editnote id/1 ni/1 ni/2 nt/Unexpected`, then view student 1. | Duplicate-`ni/` feedback appears. Both note texts and saved data remain unchanged. |
| NOTE-EDIT-007 | Run `editnote id/1 ni/1 nt/Persisted update`, restart, and view student 1. | Index 1 retains `Persisted update`; index 2 and student 2 remain unchanged. |
| NOTE-STORAGE-001 | Add two notes in order, exit the application, inspect the saved student record, restart, and view the same student. | The saved JSON stores `registeredNotes` as an ordered list of note-text strings. Both notes reappear in the same order with unchanged text. |

## Add-note command regression

Start with two students and no notes for student 1. After every rejected
command, run `view id/1` and confirm that the note list and saved data are
unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-ADD-001 | Run `addnote id/1 nt/Review lecture notes`, then view student 1. | Success feedback names student 1. The note list contains `1: Review lecture notes`; student 2 is unchanged. |
| NOTE-ADD-002 | Run `addnote id/1 nt/Prepare consultation`, then view student 1. | The second note is appended at index 2; index 1 remains unchanged. |
| NOTE-ADD-003 | Run `addnote id/1 nt/Invalid!`, then view student 1. | Invalid-text feedback appears. The two existing notes remain unchanged. |
| NOTE-ADD-004 | Run `addnote id/1`, then `addnote id/1 nt/Valid nt/Other`, viewing student 1 after each command. | The missing-`nt/` and duplicate-`nt/` messages appear respectively. No note is added. |
| NOTE-ADD-005 | Run `addnote id/3 nt/Invalid index`, then view both students. | Invalid-student-index feedback appears; neither student changes. |
| NOTE-ADD-006 | Restart after NOTE-ADD-002 and view student 1. | Both notes and their insertion order are restored unchanged. |
| NOTE-ADD-007 | Add notes until student 1 has 10, then run `addnote id/1 nt/Overflow` and view student 1. | `Amy Bee already has the maximum of 10 notes. No changes made.` appears. The original 10 notes and saved data remain unchanged. |

## Delete-note command regression

Start with two students. Give student 1 notes at indexes 1 and 2, and leave student 2 unchanged.
After every rejected command, run `view id/1` and confirm that both notes and saved data are unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| NOTE-DELETE-001 | Run `delnote id/1 ni/1`, then view both students. | Success feedback identifies student 1 and note index 1. The former index-2 note shifts to index 1; student 2 is unchanged. |
| NOTE-DELETE-002 | Run `delnote id/1 ni/2`, then view student 1. | Missing-note feedback appears. The remaining note at index 1 is unchanged. |
| NOTE-DELETE-003 | Run `delnote id/1 ni/0`, `delnote id/1`, and `delnote 1 ni/1`, viewing student 1 after each command. | The invalid-index, missing-`ni/`, and invalid-format messages appear respectively. The remaining note does not change. |
| NOTE-DELETE-004 | Run `delnote id/3 ni/1`, then view both students. | Invalid-student-index feedback appears; no student changes. |
| NOTE-DELETE-005 | Run `delnote id/1 ni/1`, restart, and view student 1. | The Notes table shows the empty-state message. The deletion persists across restart. |
| NOTE-DELETE-006 | Restore two notes, run `delnote id/1 ni/1 ni/2`, then view student 1. | Duplicate-`ni/` feedback appears. Both notes and the saved data remain unchanged. |

## Mission regression sequence

Use a temporary data directory with two students and an empty catalogue. After each rejected
command, run `view 1` and compare the catalogue and submission statuses with the previous state.
Inspect the saved file before running that verification command to ensure rejection did not save.

| ID | Steps | Expected result |
| --- | --- | --- |
| MISSION-001 | Run `addmission`, `addmission m/M01`, and `addmission m/M01 n/Runes`. | Reports the missing prefixes using their `CliSyntax` messages; no mission is added. |
| MISSION-002 | Run `addmission m/M01 n/Rune Reading d/2026-10-20`, then view student 1. | Creates M01, Rune Reading, due 2026-10-20, not submitted. |
| MISSION-003 | Run `addmission m/M02 n/ RUNE   READING d/2026-10-21`. | Duplicate-name feedback; M01 and saved state remain unchanged. |
| MISSION-004 | Run `addmission m/M02 n/Curves d/2026-10-21`, then `editmission m/M02 n/Rune Reading`. | M02 is added; the duplicate edit is rejected. |
| MISSION-012 | Attempt `addmission m/M01 n/Curves d/2026-10-21`, then `addmission m/M1 n/Curves d/2026-10-21` and `addmission m/M00 n/Curves d/2026-10-21`. | Duplicate ID feedback for M01; malformed IDs are rejected with the ID format constraint. Catalogue and saved data remain unchanged. |
| MISSION-013 | Add `addmission m/M10 n/Curves d/2026-10-21`, restart, and inspect the catalogue. | M10 is stored and restored unchanged. |
| MISSION-005 | Run `markmission 1 m/m01`, repeat it, then `unmarkmission 1 m/M99`. | First marks M01, repeat reports no change, unknown ID reports `Mission M99 does not exist.`; student 2 remains unchanged. |
| MISSION-006 | Filter to student 1; run `listmissions m/M01` and `listoverdue`. | Submitted list uses displayed index 1. Overdue excludes M01 and includes only missing missions due before the local date. Hidden students are excluded. |
| MISSION-007 | Run `editmission m/M01 n/New Runes d/2026-10-22`, then restart and view student 1. | ID and submission survive; name and deadline are updated and persisted. |
| MISSION-008 | Run `unmarkmission 1 m/M01`, then an invalid date edit `editmission m/M01 d/2026-02-30`; restart. | M01 stays not submitted; invalid edit leaves its name/deadline unchanged. |
| MISSION-009 | Mark M01 for both students; add M10; filter to one student; delete M01. Restart, run `list`, and inspect both profiles. | M10 retains its ID, and M01 plus its submissions are removed from both students. |
| MISSION-010 | Back up data. In an isolated fixture, give two missions different IDs but equal names ignoring case/spaces. Start the app, inspect file, then run `list`. | Empty-address-book fallback; startup preserves invalid file, successful list saves the empty book. |
| MISSION-011 | Add M10, delete it, then add `addmission m/M10 n/Limit d/2026-10-20`. | The deleted ID can be explicitly reused; the supplied ID is persisted unchanged. |

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
| STUDIO-SUMMARY-001 | View a student with records for weeks 3, 4, and 13, including an effort score of 0 for week 4. | The summary lists every week from 3 to 13 under `Week`, `Attendance`, and `Effort Score`. Recorded weeks show `Attended` and their saved effort score, including 0; weeks without a record show `Not attended` and effort score `0`. |
| STUDIO-LABEL-001 | View the student list after adding a studio record. | The student card prefixes the weekly record summary with `Studio records:` and does not expose a Java class name. |
| STUDIO-LAYOUT-001 | View a student with a week 13 effort score of 2147483647. Resize the window from narrow to wide and back. | All three columns and their headers remain inside the studio table. Effort values remain readable, wrapping when necessary, without horizontal scrolling. JUnit also checks every cell at table widths of 240 and 900 pixels. |
| STUDIO-SWITCH-001 | View a student with week 3 effort 50, then view another student without studio records, then view the first student again. | The second student shows every week as `Not attended` with effort `0`. Returning to the first student restores `Attended` and effort `50` for week 3, without duplicated rows. |
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
| PREFIX-009 | With two students, run `addstudent N/Alice Example P/12345678 E/alice@example.com G/1A`, then list students. | Uppercase prefixes are recognized and the student is added with the supplied values. |

## Mission parser removal and edit/prefix branch regression

Run the full Gradle suite and `coverage` with Java 25 and assertions enabled.
Verify 100% JaCoCo branch coverage for `EditCommandParser` and `Prefix`.
`PrefixTest` exercises null-backed hashing and equality, including comparisons
between null and non-null prefix values. The private
edit-tag assertion is tested through reflection because tokenizer-produced tag
collections are never null; this internal invariant has no UI input equivalent.

| ID | Steps | Expected result |
| --- | --- | --- |
| EDIT-PREFIX-001 | Run `edit id/1 n/Updated Name`, then `view id/1`. | The first student has the new name. |
| EDIT-PREFIX-002 | Run `edit unexpected id/1 n/Rejected Name`, then `view id/1`. | Invalid-format feedback; the first student retains Updated Name. |
| EDIT-PREFIX-003 | Run `edit id/1 t/`, then `view id/1`. | Tags are cleared; other fields are unchanged. |
| EDIT-PREFIX-004 | Run `edit id/1 id/2 n/Rejected Name`, then view both students. | Duplicate-index feedback; both students remain unchanged. |
| MISSION-EDIT-001 | Create a mission with `addmission m/M01 n/Runes d/2026-10-20`, then edit it using `editmission m/M01 n/New Runes`. | Editing succeeds; the mission keeps its ID and deadline. |

## Mission parser value regression

Run the corresponding `ParserUtilTest` cases. These parser-level checks have no
direct UI equivalent.

| ID | Steps | Expected result |
| --- | --- | --- |
| MISSION-PARSE-001 | Parse a mission name containing leading/trailing and repeated whitespace, punctuation, and mixed case. | Surrounding whitespace is removed, internal whitespace collapses to single spaces, and punctuation/case are preserved. |
| MISSION-PARSE-002 | Parse an empty or whitespace-only mission name, then a valid name. | Blank names produce the mission-name `ParseException`; valid parsing still succeeds. |
| MISSION-PARSE-003 | Parse a leap-day deadline `2024-02-29`, a valid regular date, and a date with surrounding whitespace. | Valid dates parse to the expected `LocalDate`; surrounding whitespace is trimmed. |
| MISSION-PARSE-004 | Parse malformed deadline formats and impossible calendar dates, then parse a valid date. | Invalid formats and impossible dates produce their respective `ParseException` messages; valid parsing still succeeds. |
| MISSION-PARSE-005 | Parse lowercase `m/m03` with surrounding whitespace. | The parser trims the input and returns canonical ID `M03`; `MissionId` owns case normalization. |
| MISSION-ID-BOUNDARY-001 | In an empty catalogue, run `addmission m/M9223372036854775807 n/Maximum ID d/2026-12-13`. | The mission is added successfully with ID `M9223372036854775807`. |
| MISSION-ID-BOUNDARY-002 | After the successful addition above, run `addmission m/M9223372036854775808 n/Overflow ID d/2026-12-13`, then `listmissions m/M9223372036854775807`. | The error says the mission number must not exceed `9223372036854775807` and includes command usage; the existing maximum-ID mission remains unchanged. |
| MISSION-DELETE-RECREATE-001 | With mission `M03` submitted by a student, delete `M03`, then recreate it using `m/M03` with a new name and deadline. | The recreated mission uses ID `M03` and has no previous submissions. |

## Mission submission index error usage

Start with at least two students and a configured mission. Record a submission
for one student before testing unmarking. After each rejected command, inspect
the student profile and confirm the submission state is unchanged.

| ID | Steps | Expected result |
| --- | --- | --- |
| MISSION-INDEX-USAGE-001 | Run `markmission id/1 m/M03`, then `view id/1`. | The first displayed student's M03 status is Submitted. |
| MISSION-INDEX-USAGE-002 | Run `markmission id/3 m/M03`, then `view id/1`. | Invalid displayed-index feedback is followed by a blank line and complete `markmission` usage; M03 remains Submitted for student 1 and no other student changes. |
| MISSION-INDEX-USAGE-003 | Run `unmarkmission id/1 m/M03`, then `view id/1`. | The first displayed student's M03 status is Not submitted. |
| MISSION-INDEX-USAGE-004 | Run `unmarkmission id/3 m/M03`, then `view id/1`. | Invalid displayed-index feedback is followed by a blank line and complete `unmarkmission` usage; M03 remains Not submitted for student 1 and no other student changes. |
| MISSION-INDEX-USAGE-005 | Run `find` so only one student is displayed, then run `markmission id/2 m/M03` and `unmarkmission id/2 m/M03`, inspecting the displayed student afterward. | Each execution-time index error includes the corresponding command usage; neither submission state nor the active student filter changes. |

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

Run all parser tests and the full Gradle suite. Every command error that includes
usage must place a blank line between the problem message and the complete usage
text. Direct ParserUtil and ArgumentMultimap tests continue to expect only the
problem message.

| ID | Steps | Expected result |
| --- | --- | --- |
| ERROR-USAGE-001 | Run `edit id/1 p/invalid`, then `view id/1`. | Phone validation feedback is followed by a blank line and edit usage exactly once; student fields are unchanged. |
| ERROR-USAGE-002 | Run `edit id/1 p/91234567`, then `view id/1`. | Phone updates successfully; other fields remain unchanged. |
| ERROR-USAGE-003 | Run `edit id/1 p/91234567 p/98765432`, then `view id/1`. | Duplicate-phone feedback is followed by a blank line and edit usage exactly once; the phone remains 91234567. |
| ERROR-USAGE-004 | Run `addmission m/M01 n/Runes d/2026-02-30`. | Date validation feedback is followed by a blank line and AddMission usage; no mission is added. |

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
