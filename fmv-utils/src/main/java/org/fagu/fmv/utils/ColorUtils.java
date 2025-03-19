package org.fagu.fmv.utils;

import java.awt.Color;


/**
 * @author f.agu
 */
public class ColorUtils {

	private ColorUtils() {}

	public static Color parse(String source) {
		if(source == null || source.isBlank()) {
			return null;
		}
		if(source.startsWith("#")) {
			String toDecode = source;
			switch(toDecode.length()) {
				case 4:
					toDecode = new StringBuilder(9).append('#')
							.append(toDecode.charAt(1)).append(toDecode.charAt(1))
							.append(toDecode.charAt(2)).append(toDecode.charAt(2))
							.append(toDecode.charAt(3)).append(toDecode.charAt(3))
							.toString();
				case 7:
					try {
						return Color.decode(toDecode);
					} catch(NumberFormatException e) {
						return null;
					}
				case 9: // contains alpha
					try {
						long l = Long.decode(toDecode);
						return new Color(
								(int)((l >> 24) & 0xFF),
								(int)((l >> 16) & 0xFF),
								(int)((l >> 8) & 0xFF),
								(int)(l & 0xFF));
					} catch(NumberFormatException e) {
						return null;
					}
				default:
					return null;
			}
		}
		Color color = Color.getColor(source);
		if(color == null) {
			color = getColorByName(source);
		}
		return color;
	}

	public static String toHex(Color color) {
		if(color.getAlpha() == 255) {
			return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		}
		return String.format("#%02x%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	/**
	 * If value is :
	 * <ul>
	 * <li>< 2 : almost equivalent</li>
	 * <li>< 5 : near</li>
	 * <li>> 10 : different</li>
	 * </ul>
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static double deltaE(Color c1, Color c2) {
		double[] lab1 = rgbToLab(c1);
		double[] lab2 = rgbToLab(c2);

		double deltaL = lab1[0] - lab2[0];
		double deltaA = lab1[1] - lab2[1];
		double deltaB = lab1[2] - lab2[2];

		return Math.sqrt(deltaL * deltaL + deltaA * deltaA + deltaB * deltaB);
	}

	// ********************************************

	private static Color getColorByName(String name) {
		try {
			return (Color)Color.class.getField(name.toUpperCase()).get(null);
		} catch(Exception e) {
			return null;
		}
	}

	private static double[] rgbToLab(Color color) {
		// Étape 1 : Convertir RGB [0,255] en XYZ
		double r = color.getRed() / 255.0;
		double g = color.getGreen() / 255.0;
		double b = color.getBlue() / 255.0;

		// Appliquer correction gamma
		r = (r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : (r / 12.92);
		g = (g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : (g / 12.92);
		b = (b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : (b / 12.92);

		// Conversion en espace XYZ avec D65 standard illuminant
		double x = (r * 0.4124564 + g * 0.3575761 + b * 0.1804375) / 0.95047;
		double y = (r * 0.2126729 + g * 0.7151522 + b * 0.0721750);
		double z = (r * 0.0193339 + g * 0.1191920 + b * 0.9503041) / 1.08883;

		// Étape 2 : Convertir XYZ en LAB
		x = (x > 0.008856) ? Math.pow(x, 1.0 / 3.0) : (7.787 * x) + (16.0 / 116.0);
		y = (y > 0.008856) ? Math.pow(y, 1.0 / 3.0) : (7.787 * y) + (16.0 / 116.0);
		z = (z > 0.008856) ? Math.pow(z, 1.0 / 3.0) : (7.787 * z) + (16.0 / 116.0);

		double l = (116 * y) - 16;
		double a = 500 * (x - y);
		double bValue = 200 * (y - z);

		return new double[] {l, a, bValue};
	}
}
