package edu.drury.mandelbrotnavigator.math;

public class MandelbrotMath {
	/** Prevent creating instances. */
	private MandelbrotMath() {}

	public static int getMandelbrotValue(double a, double b, int iterations) {
		double x = 0;
		double y = 0;

		for (int i = 0; i < iterations; i++) {
			double px = x;
			x = px * px - y * y + a;
			y = (px + px) * y + b;
			if (x * x + y * y > 4) {
				return i;
			}
		}

		return -1;
	}
}
