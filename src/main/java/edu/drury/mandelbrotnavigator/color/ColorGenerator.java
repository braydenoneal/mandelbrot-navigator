package edu.drury.mandelbrotnavigator.color;

import java.awt.*;

public class ColorGenerator {
	public static final int NUM_PALETTES = 4;
	public static final Color[] DEFAULT = {
			new Color(0, 0, 143),
			new Color(180, 180, 0),
			new Color(255, 255, 0),
			new Color(255, 0, 144),
			new Color(0, 229, 30)
	};
	public static final Color[] FIRE = {
			new Color(0, 0, 0),
			new Color(255, 0, 0),
			new Color(255, 255, 0),
			new Color(255, 255, 255),
			new Color(255, 255, 0),
			new Color(255, 0, 0),
			new Color(0, 0, 0)
	};
	public static final Color[] RGB = {
			new Color(255, 0, 0),
			new Color(255, 127, 0),
			new Color(255, 255, 0),
			new Color(0, 255, 0),
			new Color(0, 255, 225),
			new Color(0, 0, 255),
			new Color(191, 0, 255),
			new Color(255, 0, 0)
	};
	public static final Color[] GOLD = {
			new Color(59, 14, 2),
			new Color(225, 119, 12),
			new Color(255, 240, 139)
	};
	private Color[] PALETTE;

	public ColorGenerator(Color[] palette) {
		this.PALETTE = palette;
	}

	public int[] getColor(int value, int cycles) {
		int paletteSectionLength = cycles / NUM_PALETTES;
		int paletteValue = value - value / paletteSectionLength * paletteSectionLength;
		int colorsSectionLength = paletteSectionLength / (PALETTE.length - 1);

		for (int i = 0; i < PALETTE.length - 1; i++) {
			int min = colorsSectionLength * i;
			int max = colorsSectionLength * (i + 1);

			if (paletteValue >= min && paletteValue < max) {
				int colorSectionValue = paletteValue - paletteValue / colorsSectionLength * colorsSectionLength;
				double ratio = (double) (colorSectionValue) / colorsSectionLength;

				Color c1 = PALETTE[i];
				Color c2 = PALETTE[i + 1];

				return new int[] {
						(int) Math.round(ratio * (c2.getRed() - c1.getRed()) + c1.getRed()),
						(int) Math.round(ratio * (c2.getGreen() - c1.getGreen()) + c1.getGreen()),
						(int) Math.round(ratio * (c2.getBlue() - c1.getBlue()) + c1.getBlue())
				};
			}
		}

		return new int[] {0, 0, 0};
	}

	public void setPalette(Color[] PALETTE) {
		this.PALETTE = PALETTE;
	}
}
