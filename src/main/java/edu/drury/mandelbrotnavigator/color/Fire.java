package edu.drury.mandelbrotnavigator.color;

import java.awt.*;

public class Fire {
	private static final int NUM_PALETTES = 4;
	private static final Color[] FIRE = {
			new Color(0, 0, 0),
			new Color(255, 0, 0),
			new Color(255, 255, 0),
			new Color(255, 255, 255),
			new Color(255, 255, 0),
			new Color(255, 0, 0),
			new Color(0, 0, 0)
	};
	private static final Color[] RGB = {
			new Color(255, 0, 0),
			new Color(255, 127, 0),
			new Color(255, 255, 0),
			new Color(0, 255, 0),
			new Color(0, 255, 225),
			new Color(0, 0, 255),
			new Color(191, 0, 255),
			new Color(255, 0, 0)
	};
	private static final Color[] DEFAULT = {
			new Color(0, 0, 143),
			new Color(180, 180, 0),
			new Color(255, 255, 0),
			new Color(255, 0, 144),
			new Color(0, 229, 30)
	};
	private static final Color[] COLORS = DEFAULT;

	/** Prevent creating instances. */
	private Fire() {}

	public static int[] getColor(int value, int cycles) {
		int paletteSectionLength = cycles / NUM_PALETTES;
		int paletteValue = value - value / paletteSectionLength * paletteSectionLength;
		int colorsSectionLength = paletteSectionLength / (COLORS.length - 1);

		for (int i = 0; i < COLORS.length - 1; i++) {
			int min = colorsSectionLength * i;
			int max = colorsSectionLength * (i + 1);

			if (paletteValue >= min && paletteValue < max) {
				int colorSectionValue = paletteValue - paletteValue / colorsSectionLength * colorsSectionLength;
				double ratio = (double) (colorSectionValue) / colorsSectionLength;

				Color c1 = COLORS[i];
				Color c2 = COLORS[i + 1];

				return new int[] {
						(int) Math.round(ratio * (c2.getRed() - c1.getRed()) + c1.getRed()),
						(int) Math.round(ratio * (c2.getGreen() - c1.getGreen()) + c1.getGreen()),
						(int) Math.round(ratio * (c2.getBlue() - c1.getBlue()) + c1.getBlue())
				};
			}
		}

		return new int[] {0, 0, 0};
	}
}
