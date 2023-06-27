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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import org.fagu.fmv.ffmpeg.filter.FilterComplex;
import org.fagu.fmv.ffmpeg.filter.FilterComplexBase.In;
import org.fagu.fmv.ffmpeg.filter.FilterInput;
import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.GeneratedSource;
import org.fagu.fmv.ffmpeg.filter.IOKey;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.OutputKey;
import org.fagu.fmv.ffmpeg.operation.InputProcessor;
import org.fagu.fmv.ffmpeg.operation.MediaInput;
import org.fagu.fmv.ffmpeg.operation.Operation;
import org.fagu.fmv.utils.collection.MapSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class FilterGraph {

	private FilterNaming filterNaming;

	private List<FilterComplex> roots = new ArrayList<>();

	private MapSet<Label, FilterComplex> byInMap = MultiValueMaps.hashMapHashSet();

	private MapSet<Label, FilterComplex> byOutMap = MultiValueMaps.hashMapHashSet();

	private FilterGraph(FilterNaming filterNaming) {
		this.filterNaming = filterNaming;
	}

	public static FilterGraph of(Operation<?, ?> operation) {
		FilterGraph filterGraph = new FilterGraph(operation.getFilterNaming());

		List<FilterComplex> filterComplexs = operation.getFilterComplexs();

		for(FilterComplex filterComplex : filterComplexs) {
			// System.out.println(filterComplex);
			Map<IOKey, In> inputMap = filterComplex.getInputMap();
			if(inputMap.isEmpty()) {
				for(MediaInput mediaInput : filterComplex.getInputs()) {
					if(mediaInput instanceof GeneratedSource) {
						filterGraph.roots.add(filterComplex);
						for(OutputKey outputKey : filterComplex.getOutputKeys()) {
							filterGraph.byOutMap.add(outputKey.getLabel(), filterComplex);
						}
					}
				}
			} else {
				for(Entry<IOKey, In> entry : inputMap.entrySet()) {
					FilterInput filterInput = entry.getValue().getFilterInput();
					for(OutputKey inputKey : filterInput.getOutputKeys()) {
						filterGraph.byInMap.add(inputKey.getLabel(), filterComplex);
					}
					if(filterInput instanceof InputProcessor) {
						filterGraph.roots.add(filterComplex);
					}
					for(OutputKey outputKey : filterComplex.getOutputKeys()) {
						filterGraph.byOutMap.add(outputKey.getLabel(), filterComplex);
					}
				}
			}
		}

		// filterGraph.debug();

		return filterGraph;
	}

	public List<FilterComplex> getRoots() {
		return Collections.unmodifiableList(roots);
	}

	public Set<FilterComplex> getByIn(Label label) {
		Set<FilterComplex> set = byInMap.get(label);
		return set != null ? Collections.unmodifiableSet(set) : Collections.emptySet();
	}

	public Set<FilterComplex> getByOut(Label label) {
		Set<FilterComplex> set = byOutMap.get(label);
		return set != null ? Collections.unmodifiableSet(set) : Collections.emptySet();
	}

	public boolean isEmpty() {
		return roots.isEmpty();
	}

	public <T> void discover(Supplier<Visitor<T>> visitorSupplier) {
		for(FilterComplex fcRoot : roots) {
			Visitor<T> visitor = visitorSupplier.get();
			discover(null, fcRoot, visitor, 0);
			visitor.close();
		}
	}

	// ***************************************************

	private <T> void discover(Label inLabel, FilterComplex filterComplex, Visitor<T> visitor, int depth) {
		for(OutputKey outputKey : filterComplex.getOutputKeys()) {
			Label outLabel = outputKey.getLabel();
			Set<FilterComplex> inFilters = null;
			if(inLabel != null) {
				inFilters = byOutMap.get(inLabel);
			}
			Set<FilterComplex> outFilters = byInMap.get(outLabel);
			visitor.visit(inLabel, inFilters, filterComplex, outFilters, outLabel, depth);
			if(outFilters != null) {
				for(FilterComplex childFC : outFilters) {
					discover(outLabel, childFC, visitor, depth + 1);
				}
			}
		}
	}

	// private void debug() {
	// System.out.println();
	// System.out.println("ROOTS : ");
	// for(FilterComplex root : roots) {
	// System.out.println(" " + root);
	// }
	// System.out.println("BY IN : ");
	// for(Entry<Label, Set<FilterComplex>> lblEntry : byInMap.entrySet()) {
	// System.out.println(" " + filterNaming.generate(lblEntry.getKey()));
	// for(FilterComplex filterComplex : lblEntry.getValue()) {
	// System.out.println(" -> " + filterComplex);
	// }
	// }
	// System.out.println("BY OUT : ");
	// for(Entry<Label, Set<FilterComplex>> lblEntry : byOutMap.entrySet()) {
	// System.out.println(" " + filterNaming.generate(lblEntry.getKey()));
	// for(FilterComplex filterComplex : lblEntry.getValue()) {
	// System.out.println(" <- " + filterComplex);
	// }
	// }
	// }
}
