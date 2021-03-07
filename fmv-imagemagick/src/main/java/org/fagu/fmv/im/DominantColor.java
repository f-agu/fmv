package org.fagu.fmv.im;

/*-
 * #%L
 * fmv-image
 * %%
 * Copyright (C) 2014 - 2017 fagu
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
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.utils.io.InputStreamSupplier;


/**
 * @author f.agu
 */
public class DominantColor {

	private static final Pattern PATTERN = Pattern.compile("(\\w+)\\((\\d+%?(?:[,\\.][\\d]+%?)*)\\)");

	private final Soft convertSoft;

	private DominantColor(Soft convertSoft) {
		this.convertSoft = Objects.requireNonNull(convertSoft);
	}

	public static DominantColor getInstance() {
		return getInstance(org.fagu.fmv.im.soft.Convert.search());
	}

	public static DominantColor getInstance(Soft convertSoft) {
		return new DominantColor(convertSoft);
	}

	public Color getDominantColor(File file) throws IOException {
		return getDominantColor(file, null);
	}

	public Color getDominantColor(File file, Consumer<CommandLine> logger) throws IOException {
		IMOperation op = createIMOperation(o -> o.image(file, "[0]"));
		List<String> outputs = new ArrayList<>();
		convertSoft.withParameters(op.toList())
				.addCommonReadLine(outputs::add)
				.logCommandLine(logger)
				.execute();
		return parseColor(outputs);
	}

	public Color getDominantColor(InputStreamSupplier inputStreamSupplier, Consumer<CommandLine> logger) throws IOException {
		return getDominantColor(inputStreamSupplier, logger, null);
	}

	public Color getDominantColor(InputStreamSupplier inputStreamSupplier, Consumer<CommandLine> logger, Consumer<IMOperation> imOpCons)
			throws IOException {
		IMOperation op = createIMOperation(o -> o.image("-", "[0]"));
		if(imOpCons != null) {
			imOpCons.accept(op);
		}
		List<String> outputs = new ArrayList<>();
		try (InputStream inputStream = inputStreamSupplier.getInputStream()) {
			Objects.requireNonNull(inputStream);
			convertSoft.withParameters(op.toList())
					.addCommonReadLine(outputs::add)
					.logCommandLine(logger)
					.input(inputStream)
					.execute();
		}
		return parseColor(outputs);
	}

	// ***************************************************

	static Color parse(String value) throws IOException {
		Matcher matcher = PATTERN.matcher(value);
		if( ! matcher.find()) {
			throw new IOException("Data not matches a color pattern: " + value);
		}
		ColorSpace colorSpace = parseColorSpace(matcher.group(1));
		String values = matcher.group(2);
		String[] components = values.split(",");
		int countNumComponents = colorSpace.getNumComponents();
		float[] floatComponents = new float[countNumComponents];
		float alpha = 1F;
		for(int i = 0; i < components.length; ++i) {
			if(countNumComponents <= i) {
				alpha = Float.valueOf(components[i]);
				continue;
			}
			if(components[i].endsWith("%")) {
				floatComponents[i] = Float.valueOf(components[i].substring(0, components[i].length() - 1)) / 100;
			} else {
				floatComponents[i] = Float.valueOf(components[i]) / 255F;
			}
		}
		return new Color(colorSpace, floatComponents, alpha);
	}

	// ***************************************************

	private static IMOperation createIMOperation(Consumer<IMOperation> imOpCons) {
		IMOperation op = new IMOperation();
		imOpCons.accept(op);
		op.scale("1x1!").format("%[pixel:u]").add("info:");
		return op;
	}

	private static Color parseColor(List<String> outputs) throws IOException {
		if(outputs.isEmpty()) {
			throw new IOException("Data not found");
		}
		String value = outputs.get(0);
		Matcher matcher = PATTERN.matcher(value);
		if( ! matcher.find()) {
			throw new IOException("Data not matches a RGB pattern: " + value);
		}
		return parse(value);
	}

	private static ColorSpace parseColorSpace(String colorSpace) {
		String v = StringUtils.substringBefore(colorSpace, "(");
		return ColorSpaces.parse(v).orElse(ColorSpace.getInstance(ColorSpace.CS_sRGB));
	}

}
