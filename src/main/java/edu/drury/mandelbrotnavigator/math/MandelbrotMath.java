package edu.drury.mandelbrotnavigator.math;

public class MandelbrotMath {
	/** Prevent creating instances. */
	private MandelbrotMath() {}

	public static int getMandelbrotValue(double a, double b, int cycles, double limit) {
		double limitSquared = limit * limit;
		double x = a;
		double y = b;

		for (int i = 0; i < cycles; i++) {
			double px = x;
			x = px * px - y * y + a;
			y = (px + px) * y + b;
			if (x * x + y * y > limitSquared) {
				return i;
			}
		}

		return -1;
	}
}
