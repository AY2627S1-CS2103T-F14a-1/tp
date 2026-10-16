# UI Test Plan

## Constructor validation regression

Null studio-record values cannot be entered through the UI. Run the JUnit case
`StudioRecordsTest.constructor_nullStudioRecord_throwsNullPointerException`
to verify that the constructor rejects them. Use the studio command and
persistence cases below to check that valid records still behave normally.

## Studio-record summary

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-SUMMARY-001 | View a student with records for weeks 3, 4, and 13. | The summary lists every week from 3 to 13. Recorded weeks show `Attended` and their effort score; weeks without a record show `Did not attend` and effort score `0`. |
| STUDIO-PERSISTENCE-001 | Add studio records with different effort scores, exit the application, restart it, and view the same student. | Each recorded week and its corresponding effort score are restored unchanged. |

## Studio commands

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-ADD-001 | Run `addstudiorecord 1 w/3 ef/50`, then view student 1. | Week 3 is attended with effort score 50. Other weeks and students remain unchanged. |
| STUDIO-ADD-002 | Run `addstudiorecord 1 w/4 ef/-1`, then view student 1. | Invalid-effort feedback appears; week 4 remains absent. |
| STUDIO-ADD-003 | Run `addstudiorecord 1 w/13 ef/0`, then view student 1. | Week 13 is attended with effort score 0. |
| STUDIO-ADD-004 | Run `addstudiorecord 1 w/3 ef/100`, then view student 1. | Already-recorded feedback appears; week 3 retains effort score 50. |
| STUDIO-DELETE-001 | Run `delstudiorecord 1 w/3`, then view student 1. | Week 3 is absent with effort score 0; week 13 remains recorded. |
| STUDIO-DELETE-002 | Run `delstudiorecord 1 w/14`, then view student 1. | Invalid-week feedback appears; records remain unchanged. |
