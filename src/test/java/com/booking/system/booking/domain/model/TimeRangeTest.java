package com.booking.system.booking.domain.model;

import com.booking.system.booking.domain.exception.InvalidTimeRangeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeTest {

    private final LocalDateTime baseTime = LocalDateTime.of(2026, 6, 1, 10, 0);

    @Test
    @DisplayName("Should successfully create a TimeRange when start time is before end time")
    void createTimeRangeSuccess() {
        LocalDateTime start = baseTime;
        LocalDateTime end = baseTime.plusHours(1);

        TimeRange timeRange = new TimeRange(start, end);

        assertEquals(start, timeRange.start());
        assertEquals(end, timeRange.end());
    }

    @Test
    @DisplayName("Should throw InvalidTimeRangeException when start time is equal to end time")
    void createTimeRangeEqualTimes() {
        LocalDateTime time = baseTime;

        assertThrows(InvalidTimeRangeException.class, () -> new TimeRange(time, time));
    }

    @Test
    @DisplayName("Should throw InvalidTimeRangeException when start time is after end time")
    void createTimeRangeStartAfterEnd() {
        LocalDateTime start = baseTime.plusHours(1);
        LocalDateTime end = baseTime;

        assertThrows(InvalidTimeRangeException.class, () -> new TimeRange(start, end));
    }

    @Test
    @DisplayName("Should detect overlaps correctly for various time configurations")
    void testOverlapsWith() {
        TimeRange range = new TimeRange(baseTime, baseTime.plusHours(2)); // 10:00 to 12:00

        // Case 1: Fully overlaps (identical)
        TimeRange identical = new TimeRange(baseTime, baseTime.plusHours(2)); // 10:00 to 12:00
        assertTrue(range.overlapsWith(identical));

        // Case 2: Partial overlap - start matches, ends later
        TimeRange partialStart = new TimeRange(baseTime, baseTime.plusHours(3)); // 10:00 to 13:00
        assertTrue(range.overlapsWith(partialStart));

        // Case 3: Partial overlap - starts earlier, ends before base ends
        TimeRange partialEnd = new TimeRange(baseTime.minusHours(1), baseTime.plusHours(1)); // 09:00 to 11:00
        assertTrue(range.overlapsWith(partialEnd));

        // Case 4: Nested - inside base range
        TimeRange nested = new TimeRange(baseTime.plusMinutes(30), baseTime.plusHours(1)); // 10:30 to 11:00
        assertTrue(range.overlapsWith(nested));

        // Case 5: Adjacent - ends exactly when base starts (No overlap)
        TimeRange adjacentBefore = new TimeRange(baseTime.minusHours(1), baseTime); // 09:00 to 10:00
        assertFalse(range.overlapsWith(adjacentBefore));

        // Case 6: Adjacent - starts exactly when base ends (No overlap)
        TimeRange adjacentAfter = new TimeRange(baseTime.plusHours(2), baseTime.plusHours(3)); // 12:00 to 13:00
        assertFalse(range.overlapsWith(adjacentAfter));

        // Case 7: Completely separate - before base
        TimeRange separateBefore = new TimeRange(baseTime.minusHours(3), baseTime.minusHours(2)); // 07:00 to 08:00
        assertFalse(range.overlapsWith(separateBefore));

        // Case 8: Completely separate - after base
        TimeRange separateAfter = new TimeRange(baseTime.plusHours(4), baseTime.plusHours(5)); // 14:00 to 15:00
        assertFalse(range.overlapsWith(separateAfter));
    }
}
