package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.BENSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.student.Address;
import seedu.address.model.student.Email;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.testutil.StudentBuilder;

public class JsonAdaptedStudentTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_STUDIO_GROUP = "A1";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final String VALID_STUDIO_GROUP = BENSON.getStudioGroup().toString();

    @Test
    public void toModelType_validStudentDetails_returnsStudent() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(BENSON);
        assertEquals(BENSON, student.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, null,
                VALID_TAGS, VALID_STUDIO_GROUP, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                invalidTags, VALID_STUDIO_GROUP, List.of());
        assertThrows(IllegalValueException.class, student::toModelType);
    }

    @Test
    public void toModelType_invalidStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, INVALID_STUDIO_GROUP, List.of());
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_nullStudioGroup_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_TAGS, null, List.of());
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, StudioGroup.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    @Test
    public void toModelType_validMissionSubmissions_returnsStudent() throws Exception {
        Student expected = new StudentBuilder(BENSON).withMissionSubmissions(1, 4, 10).build();
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, List.of(1, 4, 10));

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidMissionSubmissions_throwsIllegalValueException() {
        for (int week : new int[] {-1, 0, 11}) {
            JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, List.of(2, week));

            assertThrows(IllegalValueException.class, MissionSubmissions.MESSAGE_CONSTRAINTS,
                    student::toModelType);
        }
    }

    @Test
    public void toModelType_emptyMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = new StudentBuilder(BENSON).withMissionSubmissions().build();
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, List.of());

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_nullWeek_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, Arrays.asList(2, null));

        assertThrows(IllegalValueException.class, MissionSubmissions.MESSAGE_CONSTRAINTS,
                student::toModelType);
    }

    @Test
    public void toModelType_nullMissionSubmissions_returnsStudentWithoutSubmissions() throws Exception {
        Student expected = new StudentBuilder(BENSON).withMissionSubmissions().build();
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, null);

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_duplicateWeeks_returnsUniqueSubmissions() throws Exception {
        Student expected = new StudentBuilder(BENSON).withMissionSubmissions(2, 4).build();
        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_TAGS, VALID_STUDIO_GROUP, List.of(2, 2, 4));

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_missingMissionSubmissionsInJson_returnsStudentWithoutSubmissions() throws Exception {
        String json = """
                {
                  "name": "Amy Bee",
                  "phone": "85355255",
                  "email": "amy@gmail.com",
                  "address": "123, Jurong West Ave 6, #08-111",
                  "tags": [],
                  "studioGroup": "1A"
                }
                """;
        JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);
        Student expected = new StudentBuilder().withMissionSubmissions().build();

        assertEquals(expected, student.toModelType());
    }

    @Test
    public void toModelType_invalidJsonWeekTypes_throwsIllegalValueException() throws Exception {
        String template = JsonUtil.toJsonString(
                new JsonAdaptedStudent(new StudentBuilder().withMissionSubmissions(3).build()));
        for (String value : List.of("3.9", "10.9", "3.0", "3e0", "\"03\"", "true", "null", "2147483648", "{}")) {
            String json = template.replaceFirst(
                    "\"missionSubmissions\"\\s*:\\s*\\[[^]]*]", "\"missionSubmissions\": [" + value + "]");
            JsonAdaptedStudent student = JsonUtil.fromJsonString(json, JsonAdaptedStudent.class);

            assertThrows(IllegalValueException.class, MissionSubmissions.MESSAGE_CONSTRAINTS, student::toModelType);
        }
    }
}
