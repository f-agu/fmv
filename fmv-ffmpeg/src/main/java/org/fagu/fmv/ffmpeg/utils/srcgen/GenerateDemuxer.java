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
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * @author f.agu
 */
public class GenerateDemuxer {

	private static final Pattern FROM_TO_PATTERN = Pattern.compile(".*\\(from ([-\\w]+) to ([-\\w]+)\\).*");

	/**
	 * @param name
	 */
	public void generate(String name) {
		Map<String, Group> extract = FullHelpExtract.extract();
		String n = name + " demuxer";
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
		String className = StringUtils.capitalize(groupName) + "Demuxer";

		// ps.println("package org.fagu.fmv.ffmpeg.mux;");
		ps.println();
		ps.println();
		ps.println("/**");
		ps.println(" * @author f.agu");
		ps.println(" */");
		ps.println("public class " + className + " extends Demuxer<" + className + "> {");
		ps.println();

		writeEnums(ps, group);

		writeFields(ps, group);

		ps.println("\t/**");
		ps.println("\t * @param mediaInput");
		ps.println("\t */");
		ps.println("\tprotected " + className + "(MediaInput mediaInput) {");
		ps.println("\t\tsuper(\"" + groupName + "\", mediaInput);");
		ps.println("\t}");
		ps.println();

		ps.println("\t/**");
		ps.println("\t * @param file");
		ps.println("\t * @return");
		ps.println("\t */");
		ps.println("\tpublic static " + className + " from(File file) {");
		ps.println("\t	return new " + className + "(new FileMediaInput(file));");
		ps.println("\t}");
		ps.println();

		writeMethods(ps, group, className);

		writeMethod_eventAdded(ps, group);

		ps.println("}");
	}

	/**
	 * @param ps
	 * @param group
	 */
	private static void writeEnums(PrintStream ps, Group group) {
		Iterator<Param> paramIt = group.getParams().stream().filter(p -> ! p.getValues().isEmpty()).iterator();
		if(paramIt.hasNext()) {
			ps.println("\t// -----------------------------------------------");
		}

		while(paramIt.hasNext()) {
			Param param = paramIt.next();
			ps.println();
			ps.println("\t/**");
			ps.println("\t * @author f.agu");
			ps.println("\t */");
			String enumName = ClassNameUtils.type(param.getName());
			ps.println("\tpublic enum " + enumName + " {");

			Iterator<ParamValue> valueIt = param.getValues().iterator();
			for(;;) {
				ParamValue value = valueIt.next();
				if(StringUtils.isNotBlank(value.getDescription())) {
					ps.println("\t\t// " + value.getDescription());
				}
				ps.print("\t\t" + ClassNameUtils.fieldStatic(value.getName()) + "(\"" + value.getName() + "\")");
				if( ! valueIt.hasNext())
					break;
				ps.println(',');
			}
			ps.println(";");
			ps.println();
			ps.println("\t\t/**");
			ps.println("\t\t *");
			ps.println("\t\t */");
			ps.println("\t\tprivate String flag;");
			ps.println();
			ps.println("\t\t/**");
			ps.println("\t\t * @param flag");
			ps.println("\t\t */");
			ps.println("\t\tprivate " + enumName + "(String flag) {");
			ps.println("\t\t\tthis.flag = flag;");
			ps.println("\t\t}");
			ps.println();
			ps.println("\t\t/**");
			ps.println("\t\t * @return");
			ps.println("\t\t */");
			ps.println("\t\tpublic String flag() {");
			ps.println("\t\t\treturn flag;");
			ps.println("\t\t}");
			ps.println();
			ps.println("\t}");
			ps.println();
			ps.println("\t// -----------------------------------------------");
			ps.println();
		}
	}

	/**
	 * @param ps
	 * @param group
	 */
	private static void writeFields(PrintStream ps, Group group) {
		for(Param param : group.getParams()) {
			ps.println("\t/**");
			ps.println("\t * ");
			ps.println("\t */");
			String type = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.typeOf(param, false);
			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);
			ps.println("\tprivate " + type + ' ' + name + ';');
		}
	}

	/**
	 * @param ps
	 * @param group
	 * @param returnClassName
	 */
	private static void writeMethods(PrintStream ps, Group group, String returnClassName) {
		for(Param param : group.getParams()) {
			String type = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.typeOf(param, true);
			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);

			ps.println("\t/**");
			ps.println("\t * " + StringUtils.capitalize(param.getDescription()));
			ps.println("\t * ");
			ps.println("\t * @param " + name);
			ps.println("\t * @return");
			ps.println("\t */");
			ps.println("\tpublic " + returnClassName + " " + name + '(' + type + ' ' + name + ") {");
			writeMethodCheck(ps, param, name);
			ps.println("\t\tthis." + name + " = " + name + ';');
			ps.println("\t\treturn this;");
			ps.println("\t}");
			ps.println();
		}
	}

	/**
	 * @param ps
	 * @param param
	 * @param returnClassName
	 */
	private static void writeMethodCheck(PrintStream ps, Param param, String name) {
		if( ! param.getValues().isEmpty()) {
			return;
		}
		Matcher matcher = FROM_TO_PATTERN.matcher(param.getDescription());
		if(matcher.matches()) {
			String g1 = matcher.group(1);
			String g2 = matcher.group(2);
			int i1 = parseInt(g1);
			int i2 = parseInt(g2);
			if(i1 == Integer.MIN_VALUE) {
				if(i2 == Integer.MAX_VALUE) {
					return;
				}
				ps.println("\t\t\tif(" + name + " > " + i2 + ") {");
				ps.println("\t\t\t\tthrow new IllegalArgumentException(\"" + name + " must be at most " + i2 + "\");");
				ps.println("\t\t\t}");
				return;
			}
			if(i2 == Integer.MAX_VALUE) {
				ps.println("\t\t\tif(" + name + " < " + i1 + ") {");
				ps.println("\t\t\t\tthrow new IllegalArgumentException(\"" + name + " must be at least " + i1 + "\");");
				ps.println("\t\t\t}");
				return;
			}
			ps.println("\t\t\tif(" + i1 + " < " + name + " && " + name + " < " + i2 + ") {");
			ps.println("\t\t\t\tthrow new IllegalArgumentException(\"" + name + " must be between " + i1 + " and " + i2 + "\");");
			ps.println("\t\t\t}");
		}
	}

	/**
	 * @param s
	 * @return
	 */
	private static int parseInt(String s) {
		if("INT_MIN".equals(s)) {
			return Integer.MIN_VALUE;
		}
		if("INT_MAX".equals(s)) {
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(s);
	}

	/**
	 * @param ps
	 * @param group
	 */
	private static void writeMethod_eventAdded(PrintStream ps, Group group) {
		ps.println("\t/**");
		ps.println("\t * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)");
		ps.println("\t */");
		ps.println("\t@Override");
		ps.println("\tpublic void eventAdded(Processor<?> processor, IOEntity ioEntity) {");

		for(Param param : group.getParams()) {
			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);
			ps.println("\t\tif(" + name + " != null) {");
			String value = name;
			ParamType paramType = param.getType();
			if( ! param.getValues().isEmpty()) {
				value = value + ".flag()";
			} else if(paramType == ParamType.VIDEO_RATE) {
				value += ".getRate()";
			} else if(paramType == ParamType.IMAGE_SIZE) {
				value += ".toString()";
			} else if(paramType == ParamType.INT) {
				value = "Integer.toString(" + value + ')';
			} else if(paramType == ParamType.INT64) {
				value = "Long.toString(" + value + ')';
			}

			ps.println("\t\t\tparameter(processor, ioEntity, \"" + param.getName() + "\", " + value + ");");
			ps.println("\t\t}");
		}
		ps.println("\t}");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateDemuxer generateFilter = new GenerateDemuxer();
		generateFilter.generate("image2");
	}

}
