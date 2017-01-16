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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;


/**
 * @author f.agu
 */
public class FontUtils {

	/**
	 * 
	 */
	private static BidiMap<String, Font> FONT_MAP;

	/**
	 * 
	 */
	private static Map<Font, File> FONTFILE_MAP;

	//

	/**
	 * 
	 */
	private FontUtils() {}

	/**
	 * @param fontName
	 * @return
	 */
	public static File getFile(String fontName) {
		loadAllSystemFonts();
		Font font = FONT_MAP.get(fontName);
		return getFile(font);
	}

	/**
	 * @param font
	 * @return
	 */
	public static File getFile(Font font) {
		loadAllSystemFonts();
		return FONTFILE_MAP.get(font);
	}

	/**
	 * @return
	 */
	public static String getSystemFontPath() {
		Object fontManager = null;
		Class<?> fontManagerClass = null;
		try {
			Class<?> fontManagerFactoryClass = Class.forName("sun.font.FontManagerFactory");
			Method method = fontManagerFactoryClass.getMethod("getInstance");
			fontManager = method.invoke(fontManagerFactoryClass);
			fontManagerClass = Class.forName("sun.font.SunFontManager");
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		try {
			Method method = fontManagerClass.getDeclaredMethod("getFontPath", new Class<?>[] {boolean.class});
			method.setAccessible(true);
			return (String)method.invoke(fontManager, true);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	// ***************************************

	/**
	 * 
	 */
	private static void loadAllSystemFonts() {
		if(FONT_MAP != null) {
			return;
		}
		BidiMap<String, Font> fontMap = new DualHashBidiMap<>();
		Map<Font, File> fileMap = new HashMap<>();
		File folder = new File(getSystemFontPath());
		for(File file : folder.listFiles((FileFilter)new SuffixFileFilter(".ttf", IOCase.INSENSITIVE))) {
			try {
				Font font = Font.createFont(Font.TRUETYPE_FONT, file);
				fontMap.put(font.getName().toLowerCase(), font);
				fileMap.put(font, file);
			} catch(FontFormatException | IOException e) {
				// e.printStackTrace();
			}
		}
		FONT_MAP = fontMap;
		FONTFILE_MAP = fileMap;
	}
}
