package org.fagu.fmv.ffmpeg.utils.srcgen;

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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class GenerateClassFlags {

	private static final String path = "C:\\Project-4.4.1\\fmv-parent\\fmv-ffmpeg\\src\\main\\java\\org\\fagu\\fmv\\ffmpeg\\flags";

	/**
	 * @param param
	 */
	public static File generate(Param param) throws IOException {
		if(param.getType() != ParamType.FLAGS) {
			return null;
		}
		File folder = new File(path);
		String className = ClassNameUtils.type(param.getName());
		File file = new File(folder, className + ".java");
		boolean fileExists = file.exists();
		PrintStream ps = null;
		if(fileExists) {
			ps = System.out;
		} else {
			ps = new PrintStream(file);
		}

		ps.println("package org.fagu.fmv.ffmpeg.flags;");
		ps.println();
		ps.println("import org.fagu.fmv.ffmpeg.format.IO;");
		ps.println();
		ps.println("/**");
		ps.println(" * " + StringUtils.capitalize(param.getDescription()));
		ps.println(" * ");
		ps.println(" * @author f.agu");
		ps.println(" */");
		ps.println("public class " + className + " extends Flags<" + className + "> {");

		int index = 0;
		for(ParamValue value : param.getValues()) {
			ps.println("\t/**");
			ps.println("\t * " + StringUtils.capitalize(value.getDescription()));
			ps.println("\t */");
			ps.println("\tpublic static final " + className + " " + ClassNameUtils.fieldStatic(value.getName()) + " = new " + className + "(" + index
					+ ", \"" + value.getName() + "\", IO." + value.getFlags().io() + ");");
			++index;
		}
		ps.println();
		ps.println("\t/**");
		ps.println("\t * @param index");
		ps.println("\t * @param flag");
		ps.println("\t * @param io");
		ps.println("\t */");
		ps.println("\tprotected " + className + "(int index, String flag, IO io) {");
		ps.println("\t\tsuper(" + className + ".class, index, flag, io);");
		ps.println("\t}");
		ps.println("}");

		if( ! fileExists) {
			ps.close();
		}
		return file;
	}

	// public static void main(String[] args) throws IOException {
	// // Set<String>
	// Map<String, Group> extract = FullHelpExtract.extract();
	// for(Group group : extract.values()) {
	// for(Param param : group.getParams()) {
	// generate(param);
	// }
	// }
	// }

}
