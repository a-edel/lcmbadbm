package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {
    /**
     * BOUNDARY CONDITIONS.
     * RANGE: tests that randInt(0, 1) returns a number in the correct range, which is between 0 and 1, inclusive.
     */
    @Test
    void randIntRange() {
        int iterations = 10_000;

        int randomNumber;

        for (int i = 0; i < iterations; i++) {
            randomNumber = Util.randInt(0, 1);

            assertTrue(randomNumber >= 0 && randomNumber <= 1);
        }
    }

    /**
     * PERFORMANCE. I am testing that randInt(0, 1) executes in less than 1 second.
     */
    @Test
    void randIntTime() {
        long startTime;
        long endTime;
        double elapsedTimeInSeconds;

        int iterations = 10_000;
        for (int i = 0; i < iterations; i++)
        {
            startTime = System.currentTimeMillis();
            Util.randInt(0, 1);
            endTime = System.currentTimeMillis();
            elapsedTimeInSeconds = (endTime - startTime) / 1000.0; // Convert to seconds

            assertTrue(elapsedTimeInSeconds < 1);
        }
    }

    /**
     * ERROR. This tests that an IllegalArgumentException is thrown by randInt() when the min parameter is greater than
     * the max parameter.
     */
    @Test
    void randIntError() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> Util.randInt(1, 0));
        assertEquals("bound must be positive", exception.getMessage());
    }
}
