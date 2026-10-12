package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.mission.MissionId;

/** Tests the shared mission parser's normalization and null contract. */
public class MissionParserTest {

    @Test
    public void constructor_whitespace_parsesIndexAndMission() throws Exception {
        MissionParser parser = new MissionParser(" \t2 \tm/M105 ", "usage");
        assertEquals(2, parser.getIndex().getOneBased());
        assertEquals(new MissionId("M105"), parser.getMissionId());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MissionParser(null, "usage"));
        assertThrows(NullPointerException.class, () -> new MissionParser("1 m/M05", null));
    }
}
