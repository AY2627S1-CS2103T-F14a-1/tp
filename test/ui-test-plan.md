# UI Test Plan

## Studio-record summary

| ID | Steps | Expected result |
| --- | --- | --- |
| STUDIO-SUMMARY-001 | View a student with records for weeks 3, 4, and 13. | The summary lists every week from 3 to 13. Recorded weeks show `Attended` and their effort score; weeks without a record show `Did not attend` and effort score `0`. |
| STUDIO-PERSISTENCE-001 | Add studio records with different effort scores, exit the application, restart it, and view the same student. | Each recorded week and its corresponding effort score are restored unchanged. |
