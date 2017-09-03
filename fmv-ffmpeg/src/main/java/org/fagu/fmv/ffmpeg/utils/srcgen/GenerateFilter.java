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
public class GenerateFilter {

	/**
	 *
	 */
	public GenerateFilter() {

	}

	/**
	 * @param name
	 */
	public void generate(String name) {
		Map<String, Group> extract = FullHelpExtract.extract();
		Group group = extract.get(name);
		if(group == null) {
			throw new RuntimeException("Not found: " + name);
		}
		if( ! group.getFlags().isFilter()) {
			throw new RuntimeException("It is not a filter: " + name);
		}

		writeClass(System.out, group);
	}

	/**
	 * @param ps
	 * @param group
	 */
	private void writeClass(PrintStream ps, Group group) {

		GenerateAVContext.writeEnums(ps, group, false, true, true);

		String className = StringUtils.capitalize(group.getName());
		boolean audio = group.getFlags().isAudio();
		boolean video = group.getFlags().isVideo();

		ps.println("package org.fagu.fmv.ffmpeg.filter.impl;");
		ps.println();
		ps.println("import java.util.Set;");
		if(audio && video) {
			ps.println("import java.util.Collections;");
		} else if(audio || video) {
			ps.println("import java.util.Arrays;");
		}
		ps.println("import org.fagu.fmv.ffmpeg.filter.AbstractFilter;");
		ps.println("import org.fagu.fmv.ffmpeg.operation.Type;");
		ps.println();
		ps.println("/**");
		ps.println(" * @author f.agu");
		ps.println(" */");
		ps.println("public class " + className + " extends AbstractFilter {");
		ps.println();

		ps.println("\t/**");
		ps.println("\t * ");
		ps.println("\t */");
		ps.println("\tprotected " + className + "() {");
		ps.println("\t	super(\"" + group.getName() + "\");");
		ps.println("\t}");
		ps.println();

		ps.println("\t/**");
		ps.println("\t * @return");
		ps.println("\t */");
		ps.println("\tpublic static " + className + " build() {");
		ps.println("\t	return new " + className + "();");
		ps.println("\t}");
		ps.println();

		GenerateAVContext.writeMethods(ps, group, className, false, true, true);

		ps.println("\t/**");
		ps.println("\t * @see org.fagu.fmv.ffmpeg.filter.Filter#getTypes()");
		ps.println("\t */");
		ps.println("\t@Override");
		ps.println("\tpublic SET<Type> getTypes() {");
		if(audio && video) {
			ps.println("\t	return Arrays.asList(Type.AUDIO, Type.VIDEO);");
		} else if(audio) {
			ps.println("\t	return Collections.singleton(Type.AUDIO);");
		} else if(video) {
			ps.println("\t	return Collections.singleton(Type.VIDEO);");
		}
		ps.println("\t}");

		ps.println("}");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateFilter generateFilter = new GenerateFilter();
		generateFilter.generate("showwaves");
	}

}
