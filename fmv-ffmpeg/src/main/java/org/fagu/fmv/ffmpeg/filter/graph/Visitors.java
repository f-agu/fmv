package org.fagu.fmv.ffmpeg.filter.graph;

/*
 * #%L
 * fmv-ffmpeg
 * %%
 * Copyright (C) 2014 - 2015 fagu
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.operation.Type;
import org.fagu.fmv.utils.collection.MapSet;


/**
 * @author f.agu
 */
public class Visitors {

	/**
	 * 
	 */
	private Visitors() {}

	/**
	 * @param printStream
	 * @return
	 */
	public static Visitor<String> print(PrintStream printStream) {
		return (inLabel, inFilters, filterComplex, outFilters, outLabel, depth) -> {
			String indent = StringUtils.leftPad("", depth * 3);
			System.out.println(indent + filterComplex + "  " + filterComplex.getTypes() + "  {" + inLabel + " ; " + outLabel + "}");
		};
	}

	/**
	 * @return
	 */
	public static Visitor<Object> checkSameTypes() {
		Set<Type> set = new HashSet<>();
		return (inLabel, inFilters, filterComplex, outFilters, outLabel, depth) -> {
			Collection<Type> types = filterComplex.getTypes();
			if(set.isEmpty()) {
				set.addAll(types);
			} else {
				if(set.size() == types.size()) {
					for(Type type : types) {
						if( ! set.contains(type)) {
							throw new RuntimeException("FilterComplex(" + filterComplex + ") contains different types as " + set);
						}
					}
				}
			}
		};
	}

	/**
	 * @param map
	 * @return
	 */
	public static Visitor<Object> lastLabelWithType(MapSet<Label, Type> map) {
		// Map<Label, FilterComplex> labelMap = new HashMap<>();
		return new Visitor<Object>() {

			/**
			 * @see org.fagu.fmv.ffmpeg.filter.graph.Visitor#visit(org.fagu.fmv.ffmpeg.filter.Label, Set,
			 *      org.fagu.fmv.ffmpeg.filter.FilterComplex, Set, org.fagu.fmv.ffmpeg.filter.Label, int)
			 */
			public void visit(Label inLabel, Set<FilterComplex> inFilters, FilterComplex filterComplex, Set<FilterComplex> outFilters,
					Label outLabel, int depth) {
				// System.out.println(filterComplex + " / " + filterNaming.generate(outLabel));
				if(outFilters == null || outFilters.isEmpty()) {
					// labelMap.put(outLabel, filterComplex);
					for(Type type : filterComplex.getTypes()) {
						map.add(outLabel, type);
					}
				}
			}

			/**
			 * @see org.fagu.fmv.ffmpeg.filter.graph.Visitor#close()
			 */
			// public void close() {
			// for(Type type : lastFilterComplex.get().getTypes()) {
			// map.add(lastOutLabel.get(), type);
			// }
			// }
		};
	}
}
