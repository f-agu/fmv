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
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.format.IO;


/**
 * @author f.agu
 */
public class GenerateAVContext {

	private static final Pattern FROM_TO_PATTERN = Pattern.compile(".*\\(from ([\\-\\+\\w\\.]+) to ([\\-\\+\\w\\.]+)\\).*");

	/**
	 * @param name
	 */
	public void generate() {
		Map<String, Group> extract = FullHelpExtract.extract();
		String name = "AVCodecContext";
		Group group = extract.get(name);
		if(group == null) {
			throw new RuntimeException("Not found: " + name);
		}
		boolean input = true;
		boolean output = true;
		writeClass(System.out, group, true, input, output);
	}

	/**
	 * @param ps
	 * @param group
	 */
	public static void writeClass(PrintStream ps, Group group, boolean checkio, boolean input, boolean output) {
		String groupName = StringUtils.substringBefore(group.getName(), " ");
		String className = StringUtils.capitalize(groupName);

		writeEnums(ps, group, checkio, input, output);

		writeFields(ps, group, checkio, input, output);

		writeIntoConstructor(ps, group, checkio, input, output);

		writeMethods(ps, group, className, checkio, input, output);

		writeMethod_eventAdded(ps, group, checkio, input, output);

		ps.println("}");
	}

	/**
	 * @param ps
	 * @param group
	 */
	public static void writeClassFlags(PrintStream ps, Param param, boolean checkio, boolean input, boolean output) {
		IO io = param.getFlags().io();
		if(checkio && ! accept(io, input, output)) {
			return;
		}

		try {
			File file = GenerateClassFlags.generate(param);
			System.out.println("// write class " + param.getName() + " : " + file.getPath());
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param ps
	 * @param group
	 */
	public static void writeEnums(PrintStream ps, Group group, boolean checkio, boolean input, boolean output) {
		Iterator<Param> paramIt = group.getParams().stream().filter(p -> ! p.getValues().isEmpty()).iterator();
		if(paramIt.hasNext()) {
			ps.println("\t// -----------------------------------------------");
		}

		while(paramIt.hasNext()) {
			Param param = paramIt.next();
			if(param.getType() == ParamType.FLAGS) {
				writeClassFlags(ps, param, checkio, input, output);
				ps.println();
				ps.println("\t// -----------------------------------------------");
				continue;
			}

			IO io = param.getFlags().io();
			if(checkio && ! accept(io, input, output)) {
				continue;
			}

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
				if(io != null) {
					ps.print("\t\t" + ClassNameUtils.fieldStatic(value.getName()) + "(\"" + value.getName() + "\", IO." + value.getFlags().io()
							+ ")");
				} else {
					ps.print("\t\t" + ClassNameUtils.fieldStatic(value.getName()) + "(\"" + value.getName() + "\")");
				}
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
			if(io != null) {
				ps.println("\t\t/**");
				ps.println("\t\t *");
				ps.println("\t\t */");
				ps.println("\t\tprivate IO io;");
				ps.println();
			}
			ps.println("\t\t/**");
			ps.println("\t\t * @param flag");
			if(io != null) {
				ps.println("\t\t * @param io");
			}
			ps.println("\t\t */");
			if(io != null) {
				ps.println("\t\tprivate " + enumName + "(String flag, IO io) {");
			} else {
				ps.println("\t\tprivate " + enumName + "(String flag) {");
			}
			ps.println("\t\t\tthis.flag = flag;");
			if(io != null) {
				ps.println("\t\t\tthis.io = io;");
			}
			ps.println("\t\t}");
			ps.println();
			ps.println("\t\t/**");
			ps.println("\t\t * @return");
			ps.println("\t\t */");
			ps.println("\t\tpublic String flag() {");
			ps.println("\t\t\treturn flag;");
			ps.println("\t\t}");
			if(io != null) {
				ps.println();
				ps.println("\t\t/**");
				ps.println("\t\t * @return");
				ps.println("\t\t */");
				ps.println("\t\tpublic IO io() {");
				ps.println("\t\t\treturn io;");
				ps.println("\t\t}");
			}
			ps.println("\t}");
			ps.println();
			ps.println("\t// -----------------------------------------------");
			ps.println();
		}
	}

	/**
	 * @param ps
	 * @param group
	 * @param checkio
	 * @param input
	 * @param output
	 */
	public static void writeFields(PrintStream ps, Group group, boolean checkio, boolean input, boolean output) {
		for(Param param : group.getParams()) {
			if( ! accept(param.getFlags().io(), input, output)) {
				continue;
			}

			String type = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.typeOf(param, false);
			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);

			if(param.getType() == ParamType.FLAGS) {
				ps.println("\t/**");
				ps.println("\t * ");
				ps.println("\t */");
				ps.println("\tprivate Set<" + type + "> " + name + "s;");
			} else {
				// ps.println("// " + type + " " + name);
				// ps.println("\tprivate " + type + ' ' + name + ';');
			}

		}
	}

	/**
	 * @param ps
	 * @param group
	 * @param checkio
	 * @param input
	 * @param output
	 */
	public static void writeIntoConstructor(PrintStream ps, Group group, boolean checkio, boolean input, boolean output) {
		ps.println();
		ps.println();
		for(Param param : group.getParams()) {
			if(checkio && ! accept(param.getFlags().io(), input, output)) {
				continue;
			}
			if(param.getType() == ParamType.FLAGS) {
				String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);
				ps.println("\t" + name + "s = new HashSet<>();");

			}
		}
		ps.println();
		ps.println();
	}

	/**
	 * @param ps
	 * @param group
	 * @param returnClassName
	 * @param checkio
	 * @param input
	 * @param output
	 */
	public static void writeMethods(PrintStream ps, Group group, String returnClassName, boolean checkio, boolean input, boolean output) {
		String returnName = StringUtils.defaultString(returnClassName, "M");

		for(Param param : group.getParams()) {
			if(checkio && ! accept(param.getFlags().io(), input, output)) {
				continue;
			}
			String type = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.typeOf(param, true);
			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);

			ps.println("\t/**");
			ps.println("\t * " + StringUtils.capitalize(param.getDescription()));
			ps.println("\t * ");
			ps.println("\t * @param " + name);
			ps.println("\t * @return");
			ps.println("\t */");
			boolean isFlag = param.getType() == ParamType.FLAGS;
			Class<?> classOf = classOf(param);

			if(isFlag) {
				ps.println("\tpublic " + returnName + " " + name + '(' + type + "... " + name + "s) {");
			} else {
				if(classOf == boolean.class) {
					ps.println("\tpublic " + returnName + " " + name + "(boolean " + name + ") {");
				} else {
					ps.println("\tpublic " + returnName + " " + name + '(' + type + ' ' + name + ") {");
					writeMethodCheck(ps, param, name, input, output);
				}
			}

			if(isFlag) {
				ps.println("\t\treturn " + name + "(Arrays.asList(" + name + "s));");
			} else {
				if(classOf == boolean.class) {
					ps.println("\t\tparameter(\"" + param.getName() + "\", Integer.toString(" + name + " ? 1 : 0));");
				} else {
					String value = name;
					ParamType<?> paramType = param.getType();
					if( ! param.getValues().isEmpty()) {
						value = value + ".flag()";
					} else if(paramType == ParamType.VIDEO_RATE) {
						value += ".toString()";
					} else if(paramType == ParamType.IMAGE_SIZE) {
						value += ".toString()";
					} else if(paramType == ParamType.INT) {
						value = "Integer.toString(" + value + ')';
					} else if(paramType == ParamType.INT64) {
						value = "Long.toString(" + value + ')';
					} else if(paramType == ParamType.FLOAT) {
						value = "Float.toString(" + value + ')';
					} else if(paramType == ParamType.DOUBLE) {
						value = "Double.toString(" + value + ')';
					} else if(paramType == ParamType.DURATION) {
						value += ".toString()";
					}
					ps.println("\t\tparameter(\"" + param.getName() + "\", " + value + ");");
				}
				if(returnClassName == null) {
					ps.println("\t\treturn getMThis();");
				} else {
					ps.println("\t\treturn " + returnClassName + ';');
				}
			}

			ps.println("\t}");
			ps.println();
			if(isFlag) {
				writeMethodsFlagsCollection(ps, param, returnClassName, checkio, input, output);
			}
		}
	}

	/**
	 * @param ps
	 * @param param
	 * @param returnClassName
	 * @param checkio
	 * @param input
	 * @param output
	 */
	public static void writeMethodsFlagsCollection(PrintStream ps, Param param, String returnClassName, boolean checkio, boolean input,
			boolean output) {
		if(checkio && ( ! accept(param.getFlags().io(), input, output) || param.getType() != ParamType.FLAGS)) {
			return;
		}
		String type = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.typeOf(param, true);
		String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);

		ps.println("\t/**");
		ps.println("\t * " + StringUtils.capitalize(param.getDescription()));
		ps.println("\t * ");
		ps.println("\t * @param " + name);
		ps.println("\t * @return");
		ps.println("\t */");
		ps.println("\tpublic " + returnClassName + " " + name + "(Collection<" + type + "> " + name + "s) {");
		ps.println("\t\t" + name + "s.stream().filter(f -> getIO().accept(f.io())).forEach(f -> this." + name + "s.add(f));");
		if("M".equals(returnClassName)) {
			ps.println("\t\treturn getMThis();");
		} else {
			ps.println("\t\treturn this;");
		}
		ps.println("\t}");
		ps.println();
	}

	/**
	 * @param ps
	 * @param param
	 * @param name
	 * @param input
	 * @param output
	 */
	public static void writeMethodCheck(PrintStream ps, Param param, String name, boolean input, boolean output) {
		if( ! param.getValues().isEmpty()) {
			if(input && output) {
				return;
			} else if(input) {
				ps.println("\t\tif( ! " + name + ".io().isInput()) {");
			} else if(output) {
				ps.println("\t\tif( ! " + name + ".io().isOutput()) {");
			}
			ps.println("\t\t\tthrow new IllegalArgumentException(\"IO is wrong: \" + " + name + ".io() + \": \" + io);");
			ps.println("\t\t}");

			return;
		}
		Matcher matcher = FROM_TO_PATTERN.matcher(param.getDescription());
		if(matcher.matches()) {
			String g1 = matcher.group(1);
			String g2 = matcher.group(2);
			ParamType type = param.getType();
			Object parse1 = type.parse(g1);
			Object parse2 = type.parse(g2);
			boolean hasMin = type.isMin(parse1);
			boolean hasMax = type.isMax(parse2);

			if(hasMin) {
				if(hasMax) {
					return;
				}
				ps.println("\t\tif(" + name + " > " + parse2.toString() + ") {");
				ps.println("\t\t\tthrow new IllegalArgumentException(\"" + name + " must be at most " + parse2.toString() + ": \" + " + name + ");");
				ps.println("\t\t}");
				return;
			}
			if(hasMax) {
				ps.println("\t\tif(" + name + " < " + parse1.toString() + ") {");
				ps.println("\t\t\tthrow new IllegalArgumentException(\"" + name + " must be at least " + parse1.toString() + ": \" + " + name + ");");
				ps.println("\t\t}");
				return;
			}
			ps.println("\t\tif(" + parse1.toString() + " > " + name + " || " + name + " > " + parse2.toString() + ") {");
			ps.println("\t\t\tthrow new IllegalArgumentException(\"" + name + " must be between " + parse1.toString() + " and " + parse2.toString()
					+ ": \" + " + name + ");");
			ps.println("\t\t}");

		}
	}

	public static Class<?> classOf(Param param) {
		Matcher matcher = FROM_TO_PATTERN.matcher(param.getDescription());
		if(matcher.matches()) {
			String g1 = matcher.group(1);
			String g2 = matcher.group(2);
			ParamType<?> type = param.getType();
			Object parse1 = type.parse(g1);
			Object parse2 = type.parse(g2);
			if(parse1 instanceof Number && parse2 instanceof Number) {
				long i1 = ((Number)parse1).longValue();
				long i2 = ((Number)parse2).longValue();
				if(i1 == 0 && i2 == 1) {
					return boolean.class;
				}
			}
		}
		return null;
	}

	/**
	 * @param io
	 * @param input
	 * @param output
	 * @return
	 */
	public static boolean accept(IO io, boolean input, boolean output) {
		if(io == null) {
			return true;
		}
		if(input && output) {
			return io == IO.INPUT_OUTPUT;
		}
		if(input) {
			return io == IO.INPUT;
		}
		if(output) {
			return io == IO.OUTPUT;
		}
		return false;
	}

	/**
	 * @param ps
	 * @param group
	 */
	public static void writeMethod_eventAdded(PrintStream ps, Group group, boolean checkio, boolean input, boolean output) {
		ps.println("\t/**");
		ps.println("\t * @see org.fagu.fmv.ffmpeg.operation.IOEntity#eventAdded(org.fagu.fmv.ffmpeg.operation.Processor, IOEntity)");
		ps.println("\t */");
		ps.println("\t@Override");
		ps.println("\tpublic void eventAdded(Processor<?> processor, IOEntity ioEntity) {");

		if( ! checkio || (input ^ output)) {
			ps.println("\t\tsuper.eventAdded(processor, ioEntity);");
		}

		for(Param param : group.getParams()) {
			if(checkio && ! accept(param.getFlags().io(), input, output)) {
				continue;
			}
			boolean isFlag = param.getType() == ParamType.FLAGS;

			String name = org.fagu.fmv.ffmpeg.utils.srcgen.ClassUtils.nameOf(param);
			if(isFlag) {
				ps.println("\t\tif( ! " + name + "s.isEmpty()) {");
				ps.println("\t\t\tparameter(processor, ioEntity, \"" + param.getName() + "\", " + name + "s);");
				ps.println("\t\t}");
			}
		}
		ps.println("\t}");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GenerateAVContext generateFilter = new GenerateAVContext();
		generateFilter.generate();
	}

}
