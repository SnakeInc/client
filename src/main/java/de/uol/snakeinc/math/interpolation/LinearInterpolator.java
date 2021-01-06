package de.uol.snakeinc.math.interpolation;

/**
 * @author Sebastian Diers
 */
public class LinearInterpolator {

    private double pos1;
    private double pos2;

    public LinearInterpolator(double pos1, double pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public double getInterpolation(double slide) {
        if (slide < 0.0D) {
            return pos1;
        } else {
            return slide > 1.0D ? pos2 : pos1 + (pos2 - pos1) * slide;
        }
    }
}

