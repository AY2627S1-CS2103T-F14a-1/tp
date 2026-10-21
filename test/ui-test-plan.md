# UI Test Plan

## Studio parsing regression

Studio argument parsing is most precisely checked at the parser level. Run the
complete `WeeklyTaskParserTest`, `ParserUtilTest`,
`AddStudioRecordCommandParserTest`, and `DeleteStudioRecordCommandParserTest`
JUnit classes. Verify that missing required prefixes show command usage,
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
| STUDIO-SUMMARY-001 | View a student with records for weeks 3, 4, and 13, including an effort score of 0 for number 4. | The summary lists every number from 3 to 13 under `Week`, `Attendance`, and `Effort`. Recorded weeks show `Present` and their saved effort score, including 0; weeks without a record show `Absent` and effort score `0`. |
| STUDIO-LABEL-001 | View the student list after adding a studio record. | The student card prefixes the weekly record summary with `Studio records:` and does not expose a Java class name. |
| STUDIO-LAYOUT-001 | View a student with a number 13 effort score of 2147483647. Resize the window from narrow to wide and back. | All three columns and their headers remain inside the studio table. Effort values remain readable, wrapping when necessary, without horizontal scrolling. JUnit also checks every cell at table widths of 240 and 900 pixels. |
| STUDIO-SWITCH-001 | View a student with number 3 effort 50, then view another student without studio records, then view the first student again. | The second student shows every number as `Absent` with effort `0`. Returning to the first student restores `Present` and effort `50` for number 3, without duplicated rows. |
| STUDIO-PERSISTENCE-001 | Add studio records with different effort scores, exit the application, inspect the saved student record, restart the application, and view the same student. | The saved JSON uses the `registeredStudioRecords` property. Each recorded number and its corresponding effort score are restored unchanged. |

## Studio commands

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-ADD-001 | Run `addstudiorecord 1 w/3 ef/50`, then view student 1. | Week 3 is attended with effort score 50. Other weeks and students remain unchanged. |
| STUDIO-ADD-002 | Run `addstudiorecord 1 w/4 ef/-1`, then view student 1. | Invalid-effort feedback appears; number 4 remains absent. |
| STUDIO-ADD-003 | Run `addstudiorecord 1 w/13 ef/0`, then view student 1. | Week 13 is attended with effort score 0. |
| STUDIO-ADD-004 | Run `addstudiorecord 1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; number 3 retains effort score 50. |
| STUDIO-DELETE-001 | Run `delstudiorecord 1 w/3`, then view student 1. | Week 3 is absent with effort score 0; number 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord 1 w/14`, then view student 1. | Invalid-number feedback appears; records remain unchanged. |
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
| MC-ADD-001 | Start with two students. Run `addmc 1 num/1 dt/18-08\|14:30`, then view student 1. | Feedback identifies appointment 1 at `Aug 18, 14:30`. The grid has Number, Appointment time, and Status columns and shows Not completed. Student 2 is unchanged. |
| MC-ADD-002 | Run `addmc 1 num/2 dt/31-04\|14:30`, then view student 1. | Invalid-datetime feedback; appointment 1 and saved data are unchanged. |
| MC-ADD-003 | Run `addmc 1 num/2 dt/19-08\|00:05`, exit, restart, and view student 1. | Numbers 1 and 2 appear in ascending order with the saved times. |
| MC-ADD-004 | Run `addmc 1 num/1 dt/20-08\|15:00`, then view student 1. | Duplicate-number feedback; number 1 retains its original time and status. |
| MC-STATE-001 | Edit a student, add/delete a studio record, mark/unmark a mission, and delete the mission. Restart and view the student. | Mastery-check numbers, appointment times, and completion statuses remain unchanged. |
| MC-VIEW-001 | View a student with appointments, then a student without appointments, then the first student again. | The empty student shows the headers and No mastery checks registered. Returning restores only the correct rows, without duplicates. |
| MC-STORAGE-001 | Load a fixture containing completed and incomplete mastery checks, save, and reload. Separately attempt to load malformed number, datetime, and status records. | Valid times, including seconds and fractional seconds, and completion flags survive reloading. Malformed files produce a loading error and remain unchanged. |

## Delete mastery check regression

JUnit follows the studio-record deletion suites and checks command state, parser errors,
case-insensitive dispatch, and real-file persistence after accepted and rejected commands.
The number-validation and usage-message cases also expose current copy-paste defects.

| ID | Steps | Expected result |
| --- | --- | --- |
| MC-DELETE-001 | Start with two students and student 1 having appointments 1 and 2. Run `delmc 1 num/1`, then view both students. | Appointment 1 is removed; appointment 2 retains its time and status. Other student fields and student 2 are unchanged. |
| MC-DELETE-002 | Run `delmc 1 num/0`, then view student 1 and inspect saved data. | Invalid-number feedback; appointments and saved data remain unchanged. |
| MC-DELETE-003 | Restart and run `delmc 1 num/1`. | Not-found feedback; saved data and remaining appointments are unchanged. |
| MC-DELETE-004 | Run `delmc 3 num/2` when only two students exist, then view both students. | Invalid-index feedback; both students and saved data are unchanged. |
| MC-DELETE-005 | Run `delmc 1 num/2`, restart, and view student 1. | No appointments remain; the empty-table message appears. Other records remain intact. |
| MC-DELETE-006 | Register numbers 14 and 2147483647, then delete each by number. | Every positive mastery check number can be deleted; studio-week limits do not apply. |
| MC-DELETE-007 | Run `delmc` and `delmc 1`. | Invalid-format feedback shows the deletion command usage, including `delmc` and `num/`. |

## Feedback scrolling

| ID | Steps | Expected result |
| --- | --- | --- |
| FEEDBACK-001 | Run `help`, then enter an unknown command. | Help feedback and unknown-command feedback remain readable. |
| FEEDBACK-002 | Run `view 1`, then `list`. | The student profile remains usable; ordinary command feedback appears normally. |
| FEEDBACK-003 | After submission listing is implemented, generate enough results to exceed the feedback area's height. Scroll to the bottom, then run another command. | Every result is reachable by scrolling. The next feedback starts at the top. |
