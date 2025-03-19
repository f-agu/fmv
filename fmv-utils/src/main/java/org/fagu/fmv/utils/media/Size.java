package org.fagu.fmv.utils.media;

/*
 * #%L
 * fmv-utils
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author f.agu
 */
public class Size implements Serializable {

	private static final long serialVersionUID = 8127536925000616968L;

	private static final Map<String, Size> NAME_MAP = new HashMap<>(64);

	private static final Map<String, Size> SIZE_MAP = new HashMap<>(64);

	private static final String CUSTOM_PREFIX = "custom-";

	public static final Size _1x1 = new Size(1, 1, "1x1");

	public static final Size NTSC = new Size(720, 480, "ntsc");

	public static final Size PAL = new Size(720, 576, "pal");

	public static final Size QNTSC = new Size(352, 240, "qntsc");

	public static final Size QPAL = new Size(352, 288, "qpal");

	public static final Size SNTSC = new Size(640, 480, "sntsc");

	public static final Size SPAL = new Size(768, 576, "spal");

	public static final Size FILM = new Size(352, 240, "film");

	public static final Size NTSC_FILM = new Size(352, 240, "ntsc-film");

	public static final Size SQCIF = new Size(128, 96, "sqcif");

	public static final Size QCIF = new Size(176, 144, "qcif");

	public static final Size CIF = new Size(352, 288, "cif");

	public static final Size _4CIF = new Size(704, 576, "4cif");

	public static final Size _4CIF_FIT_16_9 = new Size(704, 396, "4cif/16:9");

	public static final Size _16CIF = new Size(1408, 1152, "16cif");

	public static final Size QQVGA = new Size(160, 120, "qqvga");

	public static final Size QVGA = new Size(320, 240, "qvga");

	public static final Size VGA = new Size(640, 480, "vga");

	public static final Size SVGA = new Size(800, 600, "svga");

	public static final Size XGA = new Size(1024, 768, "xga");

	public static final Size UXGA = new Size(1600, 1200, "uxga", "2Mpixels");

	public static final Size QXGA = new Size(2048, 1536, "qxga", "3Mpixels");

	public static final Size SXGA = new Size(1280, 1024, "sxga");

	public static final Size QSXGA = new Size(2560, 2048, "qsxga");

	public static final Size HSXGA = new Size(5120, 4096, "hsxga");

	public static final Size WVGA = new Size(852, 480, "wvga");

	public static final Size WXGA = new Size(1366, 768, "wxga");

	public static final Size WSXGA = new Size(1600, 1024, "wsxga");

	public static final Size WUXGA = new Size(1920, 1200, "wuxga");

	public static final Size WOXGA = new Size(2560, 1600, "woxga");

	public static final Size WQSXGA = new Size(3200, 2048, "wqsxga");

	public static final Size WQUXGA = new Size(3840, 2400, "wquxga");

	public static final Size WHSXGA = new Size(6400, 4096, "whsxga");

	public static final Size WHUXGA = new Size(7680, 4800, "whuxga");

	public static final Size CGA = new Size(320, 200, "cga");

	public static final Size EGA = new Size(640, 350, "ega");

	public static final Size HD480 = new Size(852, 480, "hd480"); // should be 853 but libx264 need an even number,
																	// BECAREFUL it's not a 16/9 ratio

	public static final Size HD720 = new Size(1280, 720, "hd720");

	public static final Size HD1080 = new Size(1920, 1080, "hd1080");

	public static final Size _2K = new Size(2048, 1080, "2k");

	public static final Size _2KFLAT = new Size(1998, 1080, "2kflat");

	public static final Size _2KSCOPE = new Size(2048, 858, "2kscope");

	public static final Size _4K = new Size(4096, 2160, "4k");

	public static final Size _4KFLAT = new Size(3996, 2160, "4kflat");

	public static final Size _4KSCOPE = new Size(4096, 1716, "4kscope");

	public static final Size _4KULTRAWIDE = new Size(5120, 2160, "4kultrawide"); // add

	public static final Size _4KWHXGA = new Size(5120, 3200, "4kwhxga"); // add

	public static final Size NHD = new Size(640, 360, "nhd");

	public static final Size HQVGA = new Size(240, 160, "hqvga");

	public static final Size WQVGA = new Size(400, 240, "wqvga");

	public static final Size FWQVGA = new Size(432, 240, "fwqvga");

	public static final Size HVGA = new Size(480, 320, "hvga");

	public static final Size QHD = new Size(960, 540, "qhd");

	public static final Size _2KDCI = new Size(2048, 1080, "2kdci");

	public static final Size _4KDCI = new Size(4096, 2160, "4kdci");

	public static final Size UHD2160 = new Size(3840, 2160, "uhd2160");

	public static final Size UHD4320 = new Size(7680, 4320, "uhd4320");

	public static final Size _1M_Pixels = new Size(1280, 960, "1Mpixels");

	public static final Size _2M_Pixels = UXGA;

	public static final Size _3M_Pixels = QXGA;

	public static final Size _4M_Pixels = new Size(2240, 1680, "4Mpixels");

	public static final Size _5M_Pixels = new Size(2560, 1920, "5Mpixels");

	public static final Size _6M_Pixels = new Size(3032, 2008, "6Mpixels");

	public static final Size _7M_Pixels = new Size(3072, 2304, "7Mpixels");

	public static final Size _8M_Pixels = new Size(3264, 2448, "8Mpixels");

	public static final Size _10M_Pixels = new Size(3648, 2736, "10Mpixels");

	public static final Size _15M_Pixels = new Size(4752, 3168, "15Mpixels");

	public static final Size _18M_Pixels = new Size(5184, 3456, "18Mpixels");

	public static final Size _20M_Pixels = new Size(5472, 3648, "20Mpixels");

	private final List<String> names;

	private final int width;

	private final int height;

	private final boolean custom;

	protected Size(int width, int height, String... names) {
		this(width, height, false, names);
	}

	protected Size(int width, int height, boolean custom, String... names) {
		if(names == null || names.length == 0) {
			throw new NullPointerException("name");
		}
		if(width <= 0) {
			throw new IllegalArgumentException("Width must be positive: " + width);
		}
		if(height <= 0) {
			throw new IllegalArgumentException("Height must be positive: " + height);
		}
		this.names = Collections.unmodifiableList(Arrays.asList(names));
		this.width = width;
		this.height = height;
		this.custom = custom;
		synchronized(this) {
			init();
		}
	}

	public static Size parse(String value) {
		String v = value.trim().toLowerCase();
		Pattern pattern = Pattern.compile("(\\d+)(?:[ \\t]*)x(?:[ \\t]*)(\\d+)");
		Matcher matcher = pattern.matcher(v);
		if(matcher.matches()) {
			return valueOf(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		}
		Size size = NAME_MAP.get(v);
		if(size != null) {
			return size;
		}
		throw new IllegalArgumentException("Unable to parse: '" + value + '\'');
	}

	public static Size valueOf(int width, int height) {
		String sizeKey = getKey(width, height);
		Size size = SIZE_MAP.get(sizeKey);
		if(size != null) {
			return size;
		}
		return new Size(width, height, true, CUSTOM_PREFIX + sizeKey);
	}

	public static Size squareOf(int side) {
		return valueOf(side, side);
	}

	public static Collection<Size> values() {
		return Collections.unmodifiableCollection(SIZE_MAP.values());
	}

	public static List<Size> byRatio(Ratio ratio) {
		List<Size> list = new ArrayList<>();
		for(Size size : values()) {
			if(size.getRatio().equals(ratio)) {
				list.add(size);
			}
		}
		return list;
	}

	public static Size byName(String name) {
		return SIZE_MAP.get(name.toLowerCase());
	}

	public String getName() {
		return names.get(0);
	}

	public List<String> getNames() {
		return Collections.unmodifiableList(names);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int countPixel() {
		return width * height;
	}

	public String getStringSize() {
		return getKey(width, height);
	}

	public Ratio getRatio() {
		int pgcd = (int)org.fagu.fmv.utils.MathUtils.greatestCommonDivisor(width, height);
		return Ratio.valueOf(width / pgcd, height / pgcd);
	}

	public Ratio getRatioApproximated() {
		return getRatio().approximate();
	}

	public Size fitAndKeepRatioTo(Size size) {
		return getRatio().getSizeIn(size);
	}

	public double getDiagonal() {
		return Math.sqrt(width * width + height * height);
	}

	public Size rotate() {
		return valueOf(height, width);
	}

	public Size rotate(Rotation rotation) {
		return rotation.resize(this);
	}

	public boolean isCustom() {
		return custom;
	}

	public boolean isPortrait() {
		return width < height;
	}

	public boolean isLandscape() {
		return width > height;
	}

	public boolean isSquare() {
		return width == height;
	}

	public boolean isInside(Size other) {
		return width <= other.width && height <= other.height;
	}

	public boolean isOutside(Size other) {
		return width > other.width && height > other.height;
	}

	public boolean isPartialOutside(Size other) {
		return width > other.width ^ height > other.height;
	}

	@Override
	public boolean equals(Object obj) {
		if( ! (obj instanceof Size)) {
			return false;
		}
		Size other = (Size)obj;
		return width == other.width && height == other.height;
	}

	@Override
	public int hashCode() {
		return width << 15 + height;
	}

	@Override
	public String toString() {
		if(custom) {
			return getKey(width, height);
		}
		return getName();
	}

	public static Comparator<Size> comparatorByWidth() {
		return (s1, s2) -> s1.width - s2.width;
	}

	public static Comparator<Size> comparatorByHeight() {
		return (s1, s2) -> s1.height - s2.height;
	}

	public static Comparator<Size> comparatorByCountPixel() {
		return (s1, s2) -> s1.countPixel() - s2.countPixel();
	}

	public static Comparator<Size> comparatorByDiagonal() {
		return (s1, s2) -> Double.compare(s1.getDiagonal(), s2.getDiagonal());
	}

	// **********************************************************

	private static String getKey(int width, int height) {
		return new StringBuilder().append(width).append('x').append(height).toString();
	}

	private void init() {
		if(custom) {
			return;
		}
		for(String name : names) {
			String lcname = name.toLowerCase();
			NAME_MAP.put(lcname, this);
			NAME_MAP.put(lcname.replace("_", ""), this);
			NAME_MAP.put(lcname.replace("_", "-"), this);
			SIZE_MAP.put(getStringSize(), this);
		}
	}

	/**
	 * Serialization magic to prevent "doppelgangers". This is a performance optimization.
	 *
	 * @return
	 */
	private Object readResolve() {
		synchronized(Size.class) {
			Size type = SIZE_MAP.get(getStringSize());
			if(type != null) {
				return type;
			}
		}
		// Woops. Whoever sent us this object knows
		// about a new Type. Add it to our list.
		init();
		return this;
	}

}
