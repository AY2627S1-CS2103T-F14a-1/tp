package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;

public class JsonAdaptedStudentTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_STUDIO_GROUP = "A1";

    private static final Student VALID_STUDENT = new Student(
            new Name("Benson Meier"), new Phone("98765432"), new Email("johnd@example.com"), Set.of(),
            new StudioGroup("1A"), new MissionSubmissions(), new RegisteredStudioRecords(),
            new RegisteredMasteryChecks(), new RegisteredNotes());

    private static final String VALID_NAME = VALID_STUDENT.getName().toString();
    private static final String VALID_PHONE = VALID_STUDENT.getPhone().toString();
    private static final String VALID_EMAIL = VALID_STUDENT.getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = VALID_STUDENT.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final String VALID_STUDIO_GROUP = VALID_STUDENT.getStudioGroup().toString();
    private static final List<?> VALID_MISSION_SUBMISSIONS = List.of();
    private static final List<?> VALID_REGISTERED_STUDIO_RECORDS = List.of();
    private static final List<?> VALID_MASTERY_CHECKS = List.of();
    private static final List<?> VALID_REGISTERED_NOTES = List.of();

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_STUDENT);
        assertEquals(VALID_STUDENT, student.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(INVALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, INVALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, null, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, INVALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, null,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                invalidTags, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        assertThrows(IllegalValueException.class, student::toModelType);
    }

    @Test
    public void toModelType_invalidStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, INVALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = StudioGroup.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, null, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, StudioGroup.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_validMissionSubmissions_returnsStudent() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT, "M03", "M04", "M11", "M12", "M13");
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP,
                List.of("M03", "M04", "M11", "M12", "M13"), VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidMissionSubmissions_throwsIllegalValueException() {
        for (String invalidId : List.of("", "M00", "M1", "M001")) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_TAGS, VALID_STUDIO_GROUP, List.of("M05", invalidId), VALID_REGISTERED_STUDIO_RECORDS,
                    VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

            assertThrows(IllegalValueException.class, MissionId.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_emptyMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_nullMissionSubmissionId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, Arrays.asList("M05", null), VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertThrows(IllegalValueException.class, MissionId.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_nullMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, null, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_duplicateMissionSubmissionIds_returnsUniqueSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT, "M05", "M04");
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of("M05", "M05", "M04"), VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_missingMissionSubmissionsInJson_returnsStudentWithoutSubmissions() throws Exception {
        String json = """
                {
                  "name": "Benson Meier",
                  "phone": "98765432",
                  "email": "johnd@example.com",
                  "tags": [],
                  "studio group": "1A",
                  "registeredStudioRecords": [[3, 50]]
                }
                """;
        JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);
        Student expected = withStudioRecordsByWeek(VALID_STUDENT,
                Map.of(3, new StudioRecord(50)));

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidJsonMissionSubmissionIdTypes_throwsIllegalValueException() throws Exception {
        String template = JsonUtil.toJsonString(
                new JsonAdaptedStudent(new StudentBuilder().withMissionSubmissions("M03").build()));
        for (String value : List.of("3.9", "13.9", "3.0", "3e0", "\"03\"", "true", "null", "2147483648", "{}")) {
            String json = template.replaceFirst(
                    "\"missionSubmissions\"\\s*:\\s*\\[[^]]*]", "\"missionSubmissions\": [" + value + "]");
            JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);

            assertThrows(IllegalValueException.class, MissionId.MESSAGE_CONSTRAINTS, student::toModelType);
        }
    }

    @Test
    public void toModelType_validRegisteredStudioRecords_returnsStudent() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of(
                3, new StudioRecord(10),
                4, new StudioRecord(25),
                11, new StudioRecord(50),
                12, new StudioRecord(75),
                13, new StudioRecord(100)));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(List.of(3, 10), List.of(4, 25), List.of(11, 50),
                        List.of(12, 75), List.of(13, 100)), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidStudioRecordWeek_throwsIllegalValueException() {
        for (int week : new int[] {-1, 14}) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                    List.of(List.of(5, 50), List.of(week, 50)), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

            assertThrows(IllegalValueException.class, RegisteredStudioRecords.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_invalidStudioRecordEffortScore_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(List.of(5, -1)), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertThrows(IllegalValueException.class, StudioRecord.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_emptyRegisteredStudioRecords_returnsStudentWithoutRecords() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of());
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_nullStudioRecordWeek_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(Arrays.asList(null, 50)), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertThrows(IllegalValueException.class, RegisteredStudioRecords.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_nullRegisteredStudioRecords_returnsStudentWithoutRecords() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of());
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, null,
                VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_duplicateStudioRecordWeeks_retainsLastRecord() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of(
                4, new StudioRecord(25),
                5, new StudioRecord(100)));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(List.of(5, 50), List.of(5, 100), List.of(4, 25)), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_missingRegisteredStudioRecordsInJson_returnsStudentWithoutRecords() throws Exception {
        String json = """
                {
                  "name": "Amy Bee",
                  "phone": "85355255",
                  "email": "amy@gmail.com",
                  "tags": [],
                  "studio group": "1A",
                  "missionSubmissions": ["M03"]
                }
                """;
        JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);
        Student expected = new StudentBuilder().withMissionSubmissions("M03").withStudioRecords().build();

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_malformedRegisteredStudioRecord_throwsIllegalValueException() {
        for (Object value : Arrays.asList(3, List.of(3), List.of(3, 50, 75), null)) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                    Arrays.asList(value), VALID_MASTERY_CHECKS, VALID_REGISTERED_NOTES);

            assertThrows(IllegalValueException.class, RegisteredStudioRecords.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_invalidJsonStudioRecordTypes_throwsIllegalValueException() throws Exception {
        for (String record : List.of("[3.9, 50]", "[\"03\", 50]", "[true, 50]", "[3, 50.5]",
                "[3, \"50\"]", "[3, true]", "[3, null]")) {
            String json = createJsonWithRegisteredStudioRecords(record);
            JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);

            assertThrows(IllegalValueException.class, student::toModelType);
        }
    }

    @Test
    public void jsonConversion_validRegisteredStudioRecords_preservesPropertyAndEffortScores() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of(
                3, new StudioRecord(25),
                13, new StudioRecord(100)));

        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(expected));
        JsonAdaptedStudent savedStudent = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);

        assertTrue(json.contains("\"registeredStudioRecords\""));
        assertFalse(json.contains("\"studioRecords\""));
        assertEquals(expected, savedStudent.toModelType());
    }

    /**
     * Returns a copy of the given student with the specified mission submissions.
     *
     * @param student Student whose other fields should be retained.
     * @param missionIds Submitted mission IDs to use in the copy.
     * @return A student containing the specified mission submissions.
     */
    private static Student withMissionSubmissions(Student student, String... missionIds) {
        Set<MissionId> submittedIds = Arrays.stream(missionIds).map(MissionId::new).collect(Collectors.toSet());
        return new Student(student.getName(), student.getPhone(), student.getEmail(), student.getTags(),
                student.getStudioGroup(), new MissionSubmissions(submittedIds), student.getRegisteredStudioRecords(),
                student.getRegisteredMasteryChecks(), student.getRegisteredNotes());
    }

    /**
     * Returns a copy of the given student with the specified studio records.
     *
     * @param student Student whose other fields should be retained.
     * @param studioRecordsByWeek Studio records to use in the copy.
     * @return A student containing the specified studio records.
     */
    private static Student withStudioRecordsByWeek(Student student,
                                                   Map<Integer, StudioRecord> studioRecordsByWeek) {
        return new Student(student.getName(), student.getPhone(), student.getEmail(), student.getTags(),
                student.getStudioGroup(), student.getMissionSubmissions(),
                new RegisteredStudioRecords(studioRecordsByWeek),
                student.getRegisteredMasteryChecks(), student.getRegisteredNotes());
    }

    /**
     * Returns valid student JSON containing the supplied studio record JSON value.
     *
     * @param studioRecordJson JSON representation of one studio record.
     * @return Student JSON containing the supplied studio record.
     */
    private static String createJsonWithRegisteredStudioRecords(String studioRecordJson) {
        return """
                {
                  "name": "Amy Bee",
                  "phone": "85355255",
                  "email": "amy@gmail.com",
                  "tags": [],
                  "studio group": "1A",
                  "missionSubmissions": [],
                  "registeredStudioRecords": [%s]
                }
                """.formatted(studioRecordJson);
    }

    @Test
    public void toModelType_validMasteryChecks_restoresNumbersTimesAndStatuses() throws Exception {
        MasteryCheck complete = new MasteryCheck(LocalDateTime.of(2026, 8, 19, 0, 0));
        complete.markComplete();
        Student expected = new StudentBuilder(VALID_STUDENT).withMasteryChecks(Map.of(
                1, new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30)), Integer.MAX_VALUE, complete)).build();
        JsonAdaptedStudent adapted = withSavedMasteryChecks(List.of(
                List.of(1, "2026-08-18T14:30", false), List.of(Integer.MAX_VALUE, "2026-08-19T00:00", true)));
        assertEquals(expected, adapted.toModelType());
        assertEquals(expected.getRegisteredMasteryChecks(), adapted.toModelRegisteredMasteryChecks());
    }

    @Test
    public void toModelType_emptyOrNullMasteryChecks_returnsEmptyRegistrations() throws Exception {
        assertEquals(VALID_STUDENT, withSavedMasteryChecks(List.of()).toModelType());
        assertEquals(VALID_STUDENT, withSavedMasteryChecks(null).toModelType());
    }

    @Test
    public void toModelType_missingMasteryChecksInJson_defaultsToEmptyRegistrations() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(VALID_STUDENT))
                .replaceFirst(",?\\s*\"registeredMasteryChecks\"\\s*:\\s*\\[\\s*]", "");
        assertEquals(VALID_STUDENT, JsonUtil.fromJsonString(json, JsonAdaptedStudent.class).toModelType());
    }

    @Test
    public void toModelType_duplicateMasteryCheckNumbers_retainsLastTimeAndStatus() throws Exception {
        MasteryCheck complete = new MasteryCheck(LocalDateTime.of(2026, 8, 19, 14, 30));
        complete.markComplete();
        Student expected = new StudentBuilder(VALID_STUDENT).withMasteryChecks(Map.of(1, complete,
                2, new MasteryCheck(LocalDateTime.of(2026, 8, 20, 14, 30)))).build();
        JsonAdaptedStudent adapted = withSavedMasteryChecks(List.of(List.of(1, "2026-08-18T14:30", false),
                List.of(2, "2026-08-20T14:30", false), List.of(1, "2026-08-19T14:30", true)));
        assertEquals(expected, adapted.toModelType());
    }

    @Test
    public void toModelType_malformedMasteryCheckRecords_throwsIllegalValueException() {
        for (Object record : Arrays.asList(null, 1, "appointment", Map.of(), List.of(), List.of(1),
                List.of(1, "2026-08-18T14:30"), List.of(1, "2026-08-18T14:30", false, "extra"))) {
            JsonAdaptedStudent adapted = withSavedMasteryChecks(Arrays.asList(record));
            assertThrows(IllegalValueException.class, adapted::toModelType);
        }
    }

    @Test
    public void toModelType_invalidMasteryCheckNumbers_throwsIllegalValueException() {
        for (Object number : Arrays.asList(null, 0, -1, Integer.MIN_VALUE, 2147483648L, 1.0, "1", true)) {
            JsonAdaptedStudent adapted = withSavedMasteryChecks(List.of(
                    Arrays.asList(number, "2026-08-18T14:30", false)));
            assertThrows(IllegalValueException.class, adapted::toModelType);
        }
    }

    @Test
    public void toModelType_invalidMasteryCheckDateTimes_throwsIllegalValueException() {
        for (Object time : Arrays.asList(null, 1, true, List.of(), "", "2026-02-30T14:30", "2026-08-18T24:00",
                "2026-08-18T14:60", "2026-08-18", "18-08|14:30", "2026-08-18T14:30Z")) {
            JsonAdaptedStudent adapted = withSavedMasteryChecks(List.of(Arrays.asList(1, time, false)));
            assertThrows(IllegalValueException.class, adapted::toModelType);
        }
    }

    @Test
    public void toModelType_invalidMasteryCheckStatus_throwsIllegalValueException() {
        for (Object status : Arrays.asList(null, "true", "false", 0, 1, List.of(), Map.of())) {
            JsonAdaptedStudent adapted = withSavedMasteryChecks(List.of(
                    Arrays.asList(1, "2026-08-18T14:30", status)));
            assertThrows(IllegalValueException.class, adapted::toModelType);
        }
    }

    @Test
    public void jsonConversion_registeredMasteryChecks_preservesSortedRecordsAndPrecision() throws Exception {
        LocalDateTime time = LocalDateTime.of(2026, 8, 18, 14, 30, 45, 123456789);
        MasteryCheck complete = new MasteryCheck(time);
        complete.markComplete();
        Student expected = new StudentBuilder(VALID_STUDENT).withMasteryChecks(Map.of(
                2, complete, 1, new MasteryCheck(time.plusDays(1)))).build();
        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(expected));
        Map<?, ?> document = JsonUtil.fromJsonString(json, Map.class);
        assertEquals(List.of(List.of(1, time.plusDays(1).toString(), false), List.of(2, time.toString(), true)),
                document.get("registeredMasteryChecks"));
        Student restored = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class).toModelType();
        assertEquals(expected, restored);
        assertEquals(time, restored.getRegisteredMasteryChecks().getMasteryCheckAppointments().get(2)
                .getAppointmentTime());
    }

    @Test
    public void jsonConversion_invalidMasteryCheckTypes_rejectsWithoutChangingSourceStudent() throws Exception {
        Student original = new StudentBuilder(VALID_STUDENT).withMasteryChecks(Map.of(
                1, new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30)))).build();
        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(original));
        for (String record : List.of("[1.0, \"2026-08-18T14:30\", false]",
                "[2147483648, \"2026-08-18T14:30\", false]", "[1, 123, false]",
                "[1, \"2026-08-18T14:30\", \"true\"]", "[1, \"2026-08-18T14:30\", null]")) {
            String invalidJson = json.replaceFirst("\"registeredMasteryChecks\"\\s*:\\s*\\[[\\s\\S]*?]\\s*]",
                    "\"registeredMasteryChecks\": [" + record + "]");
            JsonAdaptedStudent adapted = JsonUtil.fromJsonString(invalidJson, JsonAdaptedStudent.class);
            assertThrows(IllegalValueException.class, adapted::toModelType);
            assertEquals(json, JsonUtil.toJsonString(new JsonAdaptedStudent(original)));
        }
    }

    @Test
    public void toModelRegisteredNotes_validNotes_restoresOriginalOrder() throws Exception {
        RegisteredNotes expected = new RegisteredNotes(List.of(
                new Note("Review lecture"), new Note("Submit assignment")));

        assertEquals(expected, withSavedNotes(List.of("Review lecture", "Submit assignment"))
                .toModelRegisteredNotes());
    }

    @Test
    public void toModelRegisteredNotes_emptyOrNullNotes_returnsEmptyNotes() throws Exception {
        assertEquals(new RegisteredNotes(), withSavedNotes(List.of()).toModelRegisteredNotes());
        assertEquals(new RegisteredNotes(), withSavedNotes(null).toModelRegisteredNotes());
    }

    @Test
    public void toModelType_missingRegisteredNotesInJson_returnsEmptyNotes() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(VALID_STUDENT))
                .replaceFirst(",?\\s*\"registeredNotes\"\\s*:\\s*\\[\\s*]", "");

        assertEquals(VALID_STUDENT, JsonUtil.fromJsonString(json, JsonAdaptedStudent.class).toModelType());
    }

    @Test
    public void toModelRegisteredNotes_tooManyNotes_throwsIllegalValueException() {
        List<String> notes = Collections.nCopies(RegisteredNotes.MAX_NOTE_INDEX + 1, "Valid note");

        assertThrows(IllegalValueException.class, RegisteredNotes.MESSAGE_CONSTRAINTS,
                withSavedNotes(notes)::toModelRegisteredNotes);
    }

    @Test
    public void toModelRegisteredNotes_invalidNoteTexts_throwsIllegalValueException() {
        String overlongNote = "a".repeat(Note.MAX_NOTE_LENGTH + 1);
        for (Object noteText : Arrays.asList(null, "", " ", overlongNote,
                1, true, List.of(), Map.of())) {
            JsonAdaptedStudent adapted = withSavedNotes(Arrays.asList(noteText));

            assertThrows(IllegalValueException.class, Note.MESSAGE_CONSTRAINTS,
                    adapted::toModelRegisteredNotes);
        }
    }

    @Test
    public void jsonConversion_registeredNotes_preservesOrderAndText() throws Exception {
        RegisteredNotes notes = new RegisteredNotes(List.of(
                new Note("Review lecture"), new Note("Submit assignment")));
        Student expected = withRegisteredNotes(VALID_STUDENT, notes);

        String json = JsonUtil.toJsonString(new JsonAdaptedStudent(expected));
        Map<?, ?> document = JsonUtil.fromJsonString(json, Map.class);
        JsonAdaptedStudent restored = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);

        assertEquals(List.of("Review lecture", "Submit assignment"), document.get("registeredNotes"));
        assertEquals(expected, restored.toModelType());
    }

    /**
     * Returns a copy of the given student with the specified registered notes.
     *
     * @param student Student whose other fields should be retained.
     * @param registeredNotes Registered notes to use in the copy.
     * @return A student containing the specified registered notes.
     */
    private static Student withRegisteredNotes(Student student, RegisteredNotes registeredNotes) {
        return new Student(student.getName(), student.getPhone(), student.getEmail(), student.getTags(),
                student.getStudioGroup(), student.getMissionSubmissions(), student.getRegisteredStudioRecords(),
                student.getRegisteredMasteryChecks(), registeredNotes);
    }

    /**
     * Creates valid adapted student details with the supplied saved mastery check entries.
     *
     * @param appointments Raw mastery check records, or null to represent an absent list.
     * @return Adapted student containing the supplied records.
     */
    private static JsonAdaptedStudent withSavedMasteryChecks(List<?> appointments) {
        return new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_TAGS, VALID_STUDIO_GROUP,
                VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS, appointments, VALID_REGISTERED_NOTES);
    }

    /**
     * Creates valid adapted student details with the supplied saved note entries.
     *
     * @param notes Raw note records.
     * @return Adapted student containing the supplied note records.
     */
    private static JsonAdaptedStudent withSavedNotes(List<?> notes) {
        return new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_TAGS, VALID_STUDIO_GROUP,
                VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS, VALID_MASTERY_CHECKS, notes);
    }

}
