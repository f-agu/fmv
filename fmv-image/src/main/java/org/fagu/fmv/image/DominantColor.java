package org.fagu.fmv.image;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2017 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 * #L%
 */
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fagu.fmv.image.soft.OverrideConvertCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ArrayListOutputConsumer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 10 janv. 2017 17:06:49
 */
public class DominantColor {

	private static final Pattern PATTERN = Pattern.compile("(\\w+)\\((\\d+(?:,\\d+)*)\\)");

	/**
	 * 
	 */
	private DominantColor() {}

	/**
	 * @param file
	 * @return
	 */
	public static Color getDominantColor(File file) throws IOException {
		return getDominantColor(file, null);
	}

	/**
	 * @param file
	 * @param logger
	 * @return
	 */
	public static Color getDominantColor(File file, Consumer<String> logger) throws IOException {
		IMOperation op = new IMOperation();
		op.addImage();
		op.addRawArgs("-scale", "1x1!");
		op.format("%[pixel:u]");
		op.addRawArgs("info:");

		ArrayListOutputConsumer outputConsumer = new ArrayListOutputConsumer();
		ConvertCmd cmd = new OverrideConvertCmd(logger);
		cmd.setOutputConsumer(outputConsumer);
		try {
			cmd.run(op, file.getAbsolutePath() + "[0]");
		} catch(InterruptedException e) {
			throw new IOException(e);
		} catch(IM4JavaException e) {
			throw new IOException(e);
		}
		List<String> lines = outputConsumer.getOutput();
		if(lines.isEmpty()) {
			throw new IOException("Data not found for " + file);
		}
		String value = lines.get(0);
		Matcher matcher = PATTERN.matcher(value);
		if( ! matcher.find()) {
			throw new IOException("Data not matches a RGB pattern: " + value);
		}
		return parse(value);
	}

	// ***************************************************

	/**
	 * @param value
	 * @return
	 * @throws IOException
	 */
	static Color parse(String value) throws IOException {
		Matcher matcher = PATTERN.matcher(value);
		if( ! matcher.find()) {
			throw new IOException("Data not matches a RGB pattern: " + value);
		}
		ColorSpace colorSpace = parseColorSpace(matcher.group(1));
		String values = matcher.group(2);
		String[] components = values.split(",");
		float[] floatComponents = new float[components.length];
		for(int i = 0; i < components.length; ++i) {
			floatComponents[i] = Float.valueOf(components[i]) / 255F;
		}

		return new Color(colorSpace, floatComponents, 1);
	}

	// ***************************************************

	/**
	 * @param colorSpace
	 * @return
	 */
	private static ColorSpace parseColorSpace(String colorSpace) {
		int type = 0; // any space
		if(colorSpace.equals("srgb")) {
			type = ColorSpace.CS_sRGB;
		} else if(colorSpace.endsWith("cmyk")) {
			return new ColorSpaceCMYK();
		}
		return ColorSpace.getInstance(type);
	}

	// public static void main(String[] args) throws IOException {
	// Color color = getDominantColor(new File("D:\\tmp\\files\\out.jpg"), System.out::println);
	// System.out.println(color);
	// }
}
