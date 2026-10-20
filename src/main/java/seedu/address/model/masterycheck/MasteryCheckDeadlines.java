package seedu.address.model.masterycheck;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Owns global deadlines for mastery checks
 */
public class MasteryCheckDeadlines {
    private final Map<Integer, LocalDateTime> deadlines;

    public MasteryCheckDeadlines() {
        deadlines = Collections.emptyMap();
    }
}
