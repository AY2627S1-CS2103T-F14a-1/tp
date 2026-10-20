# UI Test Plan

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
unequal-week lookups, then verify equal values are not inserted twice.
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
| STUDIO-ADD-004 | Run `addstudiorecord 1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; week 3 retains effort score 50. |
| STUDIO-DELETE-001 | Run `delstudiorecord 1 w/3`, then view student 1. | Week 3 is absent with effort score 0; week 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord 1 w/14`, then view student 1. | Invalid-week feedback appears; records remain unchanged. |
| STUDIO-SEQUENCE-001 | Start with two students, student 1 having no studio records. Run STUDIO-ADD-001, STUDIO-ADD-002, and STUDIO-ADD-003 in order, viewing student 1 after each command. Restart, then run STUDIO-ADD-004, STUDIO-DELETE-001, and STUDIO-DELETE-002, viewing student 1 after each command. Restart and view both students. | Each command produces the feedback and state above. The duplicate retains week 3 effort 50 despite requesting 100. After the final restart, only week 13 is recorded with effort 0; week 3 remains absent. Other student fields and student 2 remain unchanged throughout. The matching LogicManagerTest sequence checks exact feedback, in-memory state, reloaded state after every command, and unchanged saved JSON after rejected and duplicate commands. |

## Feedback scrolling

| ID | Steps | Expected result |
| --- | --- | --- |
| FEEDBACK-001 | Run `help`, then enter an unknown command. | Help feedback and unknown-command feedback remain readable. |
| FEEDBACK-002 | Run `view 1`, then `list`. | The student profile remains usable; ordinary command feedback appears normally. |
| FEEDBACK-003 | After submission listing is implemented, generate enough results to exceed the feedback area's height. Scroll to the bottom, then run another command. | Every result is reachable by scrolling. The next feedback starts at the top. |