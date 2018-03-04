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

import java.io.PrintStream;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class GenerateMuxer {

	/**
	 * @param name
	 */
	public void generate(String name) {
		Map<String, Group> extract = FullHelpExtract.extract();
		String n = name + " muxer";
		Group group = extract.get(n);
		if(group == null) {
			throw new RuntimeException("Not found: " + n);
		}

		writeClass(System.out, group);
	}

	/**
	 * @param ps
	 * @param group
	 */
	private void writeClass(PrintStream ps, Group group) {
		String groupName = StringUtils.substringBefore(group.getName(), " ");
		String className = StringUtils.capitalize(groupName) + "Muxer";

		// ps.println("package org.fagu.fmv.ffmpeg.mux;");
		ps.println();
		ps.println();
		ps.println("/**");
		ps.println(" * @author f.agu");
		ps.println(" */");
		ps.println("public class " + className + " extends Muxer<" + className + "> {");
		ps.println();

		ps.println("\t/**");
		ps.println("\t * @param mediaInput");
		ps.println("\t */");
		ps.println("\tprotected " + className + "(MediaOutput mediaOutput) {");
		ps.println("\t\tsuper(\"" + groupName + "\", mediaOutput);");
		ps.println("\t}");
		ps.println();

		ps.println("\t/**");
		ps.println("\t * @param file");
		ps.println("\t * @return");
		ps.println("\t */");
		ps.println("\tpublic static " + className + " from(File file) {");
		ps.println("\t	return new " + className + "(new FileMediaOutput(file));");
		ps.println("\t}");
		ps.println();

		GenerateAVContext.writeEnums(ps, group, false, false, true);

		GenerateAVContext.writeFields(ps, group, false, false, true);

		GenerateAVContext.writeIntoConstructor(ps, group, false, false, true);

		GenerateAVContext.writeMethods(ps, group, className, false, false, true);

		GenerateAVContext.writeMethod_eventAdded(ps, group, false, false, true);

		ps.println("}");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateMuxer generateFilter = new GenerateMuxer();
		generateFilter.generate("image2");
	}

}
