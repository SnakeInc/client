package de.uol.snakeinc.math.interpolation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class LinearInterpolatorTest {

    @Test
    public void testGetInterpolationLow() {
        LinearInterpolator interpolator = new LinearInterpolator(1.0, 1.2);
        assertEquals(1.0, interpolator.getInterpolation(-1.0));
    }

    @Test
    public void testGetInterpolation() {
        LinearInterpolator interpolator = new LinearInterpolator(1.0, 1.2);
        assertEquals(1.1, interpolator.getInterpolation(0.5));
        assertEquals(1.2, interpolator.getInterpolation(1.0));
        assertEquals(1.05, interpolator.getInterpolation(0.25));
    }

}
