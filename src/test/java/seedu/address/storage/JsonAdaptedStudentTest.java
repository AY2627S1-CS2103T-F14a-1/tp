package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
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
            new StudioGroup("1A"), new MissionSubmissions(), new RegisteredStudioRecords());
    private static final Student DEFAULT_STUDENT = new Student(
            new Name("Amy Bee"), new Phone("85355255"), new Email("amy@gmail.com"), Set.of(),
            new StudioGroup("1A"), new MissionSubmissions(), new RegisteredStudioRecords());

    private static final String VALID_NAME = VALID_STUDENT.getName().toString();
    private static final String VALID_PHONE = VALID_STUDENT.getPhone().toString();
    private static final String VALID_EMAIL = VALID_STUDENT.getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = VALID_STUDENT.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final String VALID_STUDIO_GROUP = VALID_STUDENT.getStudioGroup().toString();
    private static final List<?> VALID_MISSION_SUBMISSIONS = List.of();
    private static final List<?> VALID_REGISTERED_STUDIO_RECORDS = List.of();

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_STUDENT);
        assertEquals(VALID_STUDENT, student.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(INVALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, INVALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, null, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, INVALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, null,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                invalidTags, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        assertThrows(IllegalValueException.class, student::toModelType);
    }

    @Test
    public void toModelType_invalidStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, INVALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = StudioGroup.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, null, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, StudioGroup.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_validMissionSubmissions_returnsStudent() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT, "M03", "M04", "M11", "M12", "M13");
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP,
                List.of("M03", "M04", "M11", "M12", "M13"), VALID_REGISTERED_STUDIO_RECORDS);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidMissionSubmissions_throwsIllegalValueException() {
        for (String invalidId : List.of("", "M00", "M1", "m01", "M001")) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_TAGS, VALID_STUDIO_GROUP, List.of("M05", invalidId), VALID_REGISTERED_STUDIO_RECORDS);

            assertThrows(IllegalValueException.class, MissionId.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_emptyMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_nullMissionSubmissionId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, Arrays.asList("M05", null), VALID_REGISTERED_STUDIO_RECORDS);

        assertThrows(IllegalValueException.class, MissionId.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_nullMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT);
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, null, VALID_REGISTERED_STUDIO_RECORDS);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_duplicateMissionSubmissionIds_returnsUniqueSubmissions() throws Exception {
        Student expected = withMissionSubmissions(VALID_STUDENT, "M05", "M04");
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of("M05", "M05", "M04"), VALID_REGISTERED_STUDIO_RECORDS);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_missingMissionSubmissionsInJson_returnsStudentWithoutSubmissions() throws Exception {
        String json = """
                {
                  "name": "Amy Bee",
                  "phone": "85355255",
                  "email": "amy@gmail.com",
                  "tags": [],
                  "studio group": "1A",
                  "registeredStudioRecords": [[3, 50]]
                }
                """;
        JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);
        Student expected = withStudioRecordsByWeek(DEFAULT_STUDENT,
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
                        List.of(12, 75), List.of(13, 100)));

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidStudioRecordWeek_throwsIllegalValueException() {
        for (int week : new int[] {-1, 0, 1, 2, 14}) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                    List.of(List.of(5, 50), List.of(week, 50)));

            assertThrows(IllegalValueException.class, RegisteredStudioRecords.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_invalidStudioRecordEffortScore_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(List.of(5, -1)));

        assertThrows(IllegalValueException.class, StudioRecord.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_emptyRegisteredStudioRecords_returnsStudentWithoutRecords() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of());
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, VALID_REGISTERED_STUDIO_RECORDS);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_nullStudioRecordWeek_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(Arrays.asList(null, 50)));

        assertThrows(IllegalValueException.class, RegisteredStudioRecords.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_nullRegisteredStudioRecords_returnsStudentWithoutRecords() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of());
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS, null);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_duplicateStudioRecordWeeks_retainsLastRecord() throws Exception {
        Student expected = withStudioRecordsByWeek(VALID_STUDENT, Map.of(
                4, new StudioRecord(25),
                5, new StudioRecord(100)));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_TAGS, VALID_STUDIO_GROUP, VALID_MISSION_SUBMISSIONS,
                List.of(List.of(5, 50), List.of(5, 100), List.of(4, 25)));

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
                    Arrays.asList(value));

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
                student.getStudioGroup(), new MissionSubmissions(submittedIds), student.getRegisteredStudioRecords());
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
                new RegisteredStudioRecords(studioRecordsByWeek));
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
}
