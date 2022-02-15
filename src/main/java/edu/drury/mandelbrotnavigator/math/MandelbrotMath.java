package edu.drury.mandelbrotnavigator.math;

public class MandelbrotMath {
	private static final double LIMIT = 4.0;

	/** Prevent creating instances. */
	private MandelbrotMath() {}

	public static int getMandelbrotValue(double a, double b, int cycles, double limit) {
		limit *= limit;
		double x = a;
		double y = b;

		for (int i = 0; i < cycles; i++) {
			double px = x;
			x = px * px - y * y + a;
			y = (px + px) * y + b;
			if (x * x + y * y > limit) {
				return i;
			}
		}

		return -1;
	}
}
