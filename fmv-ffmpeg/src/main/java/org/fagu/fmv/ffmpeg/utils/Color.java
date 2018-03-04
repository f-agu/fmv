package org.fagu.fmv.ffmpeg.utils;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author f.agu
 */
public class Color {

	private static final Map<String, Color> NAME_COLOR = new HashMap<>();

	/**
	 * [0x|#]RRGGBB[AA]
	 */
	private static final Map<String, Color> SEQUENCE_COLOR = new HashMap<>();

	private static final Pattern SEQ_PATTERN = Pattern.compile("(?:0x|#)?(?:([A-F0-9]{6})|([A-Z]+))@?(?:([A-F0-9]{2})|(1|(?:0(?:\\.[0-9]+)?)))?",
			Pattern.CASE_INSENSITIVE);

	public static final Color ALICEBLUE = new Color("AliceBlue", "F0F8FF");

	public static final Color ANTIQUEWHITE = new Color("AntiqueWhite", "FAEBD7");

	public static final Color AQUA = new Color("Aqua", "00FFFF");

	public static final Color AQUAMARINE = new Color("Aquamarine", "7FFFD4");

	public static final Color AZURE = new Color("Azure", "F0FFFF");

	public static final Color BEIGE = new Color("Beige", "F5F5DC");

	public static final Color BISQUE = new Color("Bisque", "FFE4C4");

	public static final Color BLACK = new Color("Black", "000000");

	public static final Color BLANCHEDALMOND = new Color("BlanchedAlmond", "FFEBCD");

	public static final Color BLUE = new Color("Blue", "0000FF");

	public static final Color BLUEVIOLET = new Color("BlueViolet", "8A2BE2");

	public static final Color BROWN = new Color("Brown", "A52A2A");

	public static final Color BURLYWOOD = new Color("BurlyWood", "DEB887");

	public static final Color CADETBLUE = new Color("CadetBlue", "5F9EA0");

	public static final Color CHARTREUSE = new Color("Chartreuse", "7FFF00");

	public static final Color CHOCOLATE = new Color("Chocolate", "D2691E");

	public static final Color CORAL = new Color("Coral", "FF7F50");

	public static final Color CORNFLOWERBLUE = new Color("CornflowerBlue", "6495ED");

	public static final Color CORNSILK = new Color("Cornsilk", "FFF8DC");

	public static final Color CRIMSON = new Color("Crimson", "DC143C");

	public static final Color CYAN = new Color("Cyan", "00FFFF");

	public static final Color DARKBLUE = new Color("DarkBlue", "00008B");

	public static final Color DARKCYAN = new Color("DarkCyan", "008B8B");

	public static final Color DARKGOLDENROD = new Color("DarkGoldenRod", "B8860B");

	public static final Color DARKGRAY = new Color("DarkGray", "A9A9A9");

	public static final Color DARKGREEN = new Color("DarkGreen", "006400");

	public static final Color DARKKHAKI = new Color("DarkKhaki", "BDB76B");

	public static final Color DARKMAGENTA = new Color("DarkMagenta", "8B008B");

	public static final Color DARKOLIVEGREEN = new Color("DarkOliveGreen", "556B2F");

	public static final Color DARKORANGE = new Color("Darkorange", "FF8C00");

	public static final Color DARKORCHID = new Color("DarkOrchid", "9932CC");

	public static final Color DARKRED = new Color("DarkRed", "8B0000");

	public static final Color DARKSALMON = new Color("DarkSalmon", "E9967A");

	public static final Color DARKSEAGREEN = new Color("DarkSeaGreen", "8FBC8F");

	public static final Color DARKSLATEBLUE = new Color("DarkSlateBlue", "483D8B");

	public static final Color DARKSLATEGRAY = new Color("DarkSlateGray", "2F4F4F");

	public static final Color DARKTURQUOISE = new Color("DarkTurquoise", "00CED1");

	public static final Color DARKVIOLET = new Color("DarkViolet", "9400D3");

	public static final Color DEEPPINK = new Color("DeepPink", "FF1493");

	public static final Color DEEPSKYBLUE = new Color("DeepSkyBlue", "00BFFF");

	public static final Color DIMGRAY = new Color("DimGray", "696969");

	public static final Color DODGERBLUE = new Color("DodgerBlue", "1E90FF");

	public static final Color FIREBRICK = new Color("FireBrick", "B22222");

	public static final Color FLORALWHITE = new Color("FloralWhite", "FFFAF0");

	public static final Color FORESTGREEN = new Color("ForestGreen", "228B22");

	public static final Color FUCHSIA = new Color("Fuchsia", "FF00FF");

	public static final Color GAINSBORO = new Color("Gainsboro", "DCDCDC");

	public static final Color GHOSTWHITE = new Color("GhostWhite", "F8F8FF");

	public static final Color GOLD = new Color("Gold", "FFD700");

	public static final Color GOLDENROD = new Color("GoldenRod", "DAA520");

	public static final Color GRAY = new Color("Gray", "808080");

	public static final Color GREEN = new Color("Green", "008000");

	public static final Color GREENYELLOW = new Color("GreenYellow", "ADFF2F");

	public static final Color HONEYDEW = new Color("HoneyDew", "F0FFF0");

	public static final Color HOTPINK = new Color("HotPink", "FF69B4");

	public static final Color INDIANRED = new Color("IndianRed", "CD5C5C");

	public static final Color INDIGO = new Color("Indigo", "4B0082");

	public static final Color IVORY = new Color("Ivory", "FFFFF0");

	public static final Color KHAKI = new Color("Khaki", "F0E68C");

	public static final Color LAVENDER = new Color("Lavender", "E6E6FA");

	public static final Color LAVENDERBLUSH = new Color("LavenderBlush", "FFF0F5");

	public static final Color LAWNGREEN = new Color("LawnGreen", "7CFC00");

	public static final Color LEMONCHIFFON = new Color("LemonChiffon", "FFFACD");

	public static final Color LIGHTBLUE = new Color("LightBlue", "ADD8E6");

	public static final Color LIGHTCORAL = new Color("LightCoral", "F08080");

	public static final Color LIGHTCYAN = new Color("LightCyan", "E0FFFF");

	public static final Color LIGHTGOLDENRODYELLOW = new Color("LightGoldenRodYellow", "FAFAD2");

	public static final Color LIGHTGREEN = new Color("LightGreen", "90EE90");

	public static final Color LIGHTGREY = new Color("LightGrey", "D3D3D3");

	public static final Color LIGHTPINK = new Color("LightPink", "FFB6C1");

	public static final Color LIGHTSALMON = new Color("LightSalmon", "FFA07A");

	public static final Color LIGHTSEAGREEN = new Color("LightSeaGreen", "20B2AA");

	public static final Color LIGHTSKYBLUE = new Color("LightSkyBlue", "87CEFA");

	public static final Color LIGHTSLATEGRAY = new Color("LightSlateGray", "778899");

	public static final Color LIGHTSTEELBLUE = new Color("LightSteelBlue", "B0C4DE");

	public static final Color LIGHTYELLOW = new Color("LightYellow", "FFFFE0");

	public static final Color LIME = new Color("Lime", "00FF00");

	public static final Color LIMEGREEN = new Color("LimeGreen", "32CD32");

	public static final Color LINEN = new Color("Linen", "FAF0E6");

	public static final Color MAGENTA = new Color("Magenta", "FF00FF");

	public static final Color MAROON = new Color("Maroon", "800000");

	public static final Color MEDIUMAQUAMARINE = new Color("MediumAquaMarine", "66CDAA");

	public static final Color MEDIUMBLUE = new Color("MediumBlue", "0000CD");

	public static final Color MEDIUMORCHID = new Color("MediumOrchid", "BA55D3");

	public static final Color MEDIUMPURPLE = new Color("MediumPurple", "9370D8");

	public static final Color MEDIUMSEAGREEN = new Color("MediumSeaGreen", "3CB371");

	public static final Color MEDIUMSLATEBLUE = new Color("MediumSlateBlue", "7B68EE");

	public static final Color MEDIUMSPRINGGREEN = new Color("MediumSpringGreen", "00FA9A");

	public static final Color MEDIUMTURQUOISE = new Color("MediumTurquoise", "48D1CC");

	public static final Color MEDIUMVIOLETRED = new Color("MediumVioletRed", "C71585");

	public static final Color MIDNIGHTBLUE = new Color("MidnightBlue", "191970");

	public static final Color MINTCREAM = new Color("MintCream", "F5FFFA");

	public static final Color MISTYROSE = new Color("MistyRose", "FFE4E1");

	public static final Color MOCCASIN = new Color("Moccasin", "FFE4B5");

	public static final Color NAVAJOWHITE = new Color("NavajoWhite", "FFDEAD");

	public static final Color NAVY = new Color("Navy", "000080");

	public static final Color OLDLACE = new Color("OldLace", "FDF5E6");

	public static final Color OLIVE = new Color("Olive", "808000");

	public static final Color OLIVEDRAB = new Color("OliveDrab", "6B8E23");

	public static final Color ORANGE = new Color("Orange", "FFA500");

	public static final Color ORANGERED = new Color("OrangeRed", "FF4500");

	public static final Color ORCHID = new Color("Orchid", "DA70D6");

	public static final Color PALEGOLDENROD = new Color("PaleGoldenRod", "EEE8AA");

	public static final Color PALEGREEN = new Color("PaleGreen", "98FB98");

	public static final Color PALETURQUOISE = new Color("PaleTurquoise", "AFEEEE");

	public static final Color PALEVIOLETRED = new Color("PaleVioletRed", "D87093");

	public static final Color PAPAYAWHIP = new Color("PapayaWhip", "FFEFD5");

	public static final Color PEACHPUFF = new Color("PeachPuff", "FFDAB9");

	public static final Color PERU = new Color("Peru", "CD853F");

	public static final Color PINK = new Color("Pink", "FFC0CB");

	public static final Color PLUM = new Color("Plum", "DDA0DD");

	public static final Color POWDERBLUE = new Color("PowderBlue", "B0E0E6");

	public static final Color PURPLE = new Color("Purple", "800080");

	public static final Color RED = new Color("Red", "FF0000");

	public static final Color ROSYBROWN = new Color("RosyBrown", "BC8F8F");

	public static final Color ROYALBLUE = new Color("RoyalBlue", "4169E1");

	public static final Color SADDLEBROWN = new Color("SaddleBrown", "8B4513");

	public static final Color SALMON = new Color("Salmon", "FA8072");

	public static final Color SANDYBROWN = new Color("SandyBrown", "F4A460");

	public static final Color SEAGREEN = new Color("SeaGreen", "2E8B57");

	public static final Color SEASHELL = new Color("SeaShell", "FFF5EE");

	public static final Color SIENNA = new Color("Sienna", "A0522D");

	public static final Color SILVER = new Color("Silver", "C0C0C0");

	public static final Color SKYBLUE = new Color("SkyBlue", "87CEEB");

	public static final Color SLATEBLUE = new Color("SlateBlue", "6A5ACD");

	public static final Color SLATEGRAY = new Color("SlateGray", "708090");

	public static final Color SNOW = new Color("Snow", "FFFAFA");

	public static final Color SPRINGGREEN = new Color("SpringGreen", "00FF7F");

	public static final Color STEELBLUE = new Color("SteelBlue", "4682B4");

	public static final Color TAN = new Color("Tan", "D2B48C");

	public static final Color TEAL = new Color("Teal", "008080");

	public static final Color THISTLE = new Color("Thistle", "D8BFD8");

	public static final Color TOMATO = new Color("Tomato", "FF6347");

	public static final Color TURQUOISE = new Color("Turquoise", "40E0D0");

	public static final Color VIOLET = new Color("Violet", "EE82EE");

	public static final Color WHEAT = new Color("Wheat", "F5DEB3");

	public static final Color WHITE = new Color("White", "FFFFFF");

	public static final Color WHITESMOKE = new Color("WhiteSmoke", "F5F5F5");

	public static final Color YELLOW = new Color("Yellow", "FFFF00");

	public static final Color YELLOWGREEN = new Color("YellowGreen", "9ACD32");

	private final String name;

	private final String sequence;

	private String opacity;

	/**
	 * @param name
	 */
	protected Color(String sequence) {
		this.name = null;
		this.sequence = sequence.toUpperCase();
	}

	/**
	 * @param name
	 * @param sequence
	 */
	protected Color(String name, String sequence) {
		this(name, sequence, true);
	}

	/**
	 * @param name
	 * @param sequence
	 */
	protected Color(String name, String sequence, boolean register) {
		if(sequence == null) {
			throw new NullPointerException("sequence");
		}
		this.name = name;
		this.sequence = sequence;
		if(register) {
			if(name != null) {
				NAME_COLOR.put(name.toLowerCase(), this);
			}
			SEQUENCE_COLOR.put(sequence.toUpperCase(), this);
		}
	}

	/**
	 * @param value
	 * @return
	 */
	public static Color valueOf(String value) {
		Color color = NAME_COLOR.get(value.toLowerCase());
		if(color != null) {
			return color;
		}
		Matcher matcher = SEQ_PATTERN.matcher(value);
		if(matcher.matches()) {
			String group = matcher.group(2);
			if(group != null) {
				color = NAME_COLOR.get(group.toLowerCase());
			}
			group = matcher.group(1);
			if(group != null) {
				color = SEQUENCE_COLOR.get(group.toUpperCase());
			}
			if(color == null) {
				color = new Color(group);
			}
			String opacity = matcher.group(3);
			if(opacity == null) {
				opacity = matcher.group(4);
			} else {
				opacity = opacity.toUpperCase();
			}
			if(opacity != null) {
				color = color.clone(opacity);
			}
			return color;
		}
		throw new IllegalArgumentException("Format invalid: " + value);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @return the opacity
	 */
	public String getOpacity() {
		return opacity;
	}

	/**
	 * @param opacity
	 * @return
	 */
	public Color opacity(double opacity) {
		if(opacity < 0 || opacity > 1) {
			throw new IllegalArgumentException("Opacity must be between 0 and 1: " + opacity);
		}
		String sop = Double.toString(Math.floor(opacity * 10000D) / 10000D);
		return clone(sop);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof Color)) {
			return false;
		}
		Color other = (Color)obj;
		return sequence.equals(other.sequence);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return sequence.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = name != null ? name : "#" + sequence;
		if(opacity != null) {
			s += '@' + opacity;
		}
		return s;
	}

	// **********************************************************

	/**
	 * @param opacity
	 * @return
	 */
	private Color clone(String opacity) {
		Color color = new Color(name, sequence, false);
		color.opacity = opacity;
		return color;
	}
}
