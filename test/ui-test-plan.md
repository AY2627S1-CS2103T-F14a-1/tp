# Remark command UI test plan

Use a fresh address book containing at least two people. Check the result message,
the displayed person card, and `data/addressbook.json` after each state change.

1. Enter `remark 1 r/Likes to swim`. Expect an added-remark message, and the
   first card and saved record to show `Likes to swim`.
2. Enter `remark 0 r/Invalid`. Expect an invalid command format message. Run
   `list` and confirm the first person's remark is still `Likes to swim`.
3. Enter `remark 1 r/Changed`. Expect the first remark to become `Changed`.
4. Enter `remark 999 r/Invalid`. Expect an invalid displayed index message.
   Run `list` and confirm the first remark is still `Changed`.
5. Enter `remark 1 r/`. Expect a removed-remark message and no remark on the
   first card. Confirm the saved record has an empty remark.
6. Enter `remark 1`. Expect a usage error. Run `list` to confirm no change.
7. Enter `remark 1 r/One r/Two`. Expect a duplicate-prefix error. Run `list`
   to confirm no change.
8. Enter `remark 2 r/Persists`, then exit and restart the application. Expect
   the second person's card to show `Persists`.
