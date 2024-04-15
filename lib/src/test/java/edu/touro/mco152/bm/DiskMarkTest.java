package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class DiskMarkTest {
    DiskMark diskMark = new DiskMark(DiskMark.MarkType.WRITE);

    /**
     * RIGHT. I am testing that toString() is right.
     */
    @Test
    public void toStringTest() {
        diskMark.setMarkNum(1);
        diskMark.setBwMbSec(100.0);
        diskMark.setCumAvg(50.0);

        String expected = "Mark(WRITE): 1 bwMbSec: 100 avg: 50";

        assertEquals(expected, diskMark.toString());
    }

    /**
     * CROSS-CHECK. Uses getMarkNum() to cross-check that setMarkNum() works as expected.
     */
    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -100, -50, -10, -5, 0, 5, 10, 50, 100, Integer.MAX_VALUE})
    void setMarkNum(int markNum) {
        diskMark.setMarkNum(markNum);

        assertEquals(markNum, diskMark.getMarkNum());
    }

    /**
     * BOUNDARY CONDITIONS.
     * CONFORMANCE: tests that getCumMin() returns a double.
     */
    @ParameterizedTest
    @ValueSource(doubles = {Integer.MIN_VALUE, -100.0, -50.0, -10.0, -5.0, 0.0, 5.0, 10.0, 50.0, 100.0, Integer.MAX_VALUE})
    void cumMinIsADouble(double cumMin) {
        diskMark.setCumMin(cumMin);

        assertTrue(Double.class.isInstance(diskMark.getCumMin()));
    }

    /**
     * BOUNDARY CONDITIONS.
     * EXISTENCE: tests that cumMax always exists, that the value should initially be 0 before being set.
     */
    @Test
    void cumMaxIsInitiallyZero() {
        assertEquals(0, diskMark.getCumMax());
    }
}
