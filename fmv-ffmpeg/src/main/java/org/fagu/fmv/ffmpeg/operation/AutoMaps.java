package org.fagu.fmv.ffmpeg.operation;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fagu.fmv.ffmpeg.filter.FilterNaming;
import org.fagu.fmv.ffmpeg.filter.Label;
import org.fagu.fmv.ffmpeg.filter.graph.FilterGraph;
import org.fagu.fmv.ffmpeg.filter.graph.Visitors;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.utils.collection.MapSet;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public class AutoMaps {

	/**
	 *
	 */
	private AutoMaps() {}

	/**
	 * @param types
	 * @return
	 */
	public static AutoMap oneStreamByType(Set<Type> types, FilterNaming filterNaming) {
		return operation -> {

			FilterGraph filterGraph = FilterGraph.of(operation);
			// filterGraph.discover(() -> Visitors.checkSameTypes());

			MapSet<Label, Type> mapSet = MultiValueMaps.hashMapHashSet();
			filterGraph.discover(() -> Visitors.lastLabelWithType(mapSet));

			Map<Type, Label> chdirMap = new HashMap<>(4);
			for(Entry<Label, Set<Type>> entry : mapSet.entrySet()) {
				for(Type type : entry.getValue()) {
					if(chdirMap.putIfAbsent(type, entry.getKey()) != null) {
						throw new RuntimeException("Type " + type + " already defined in an other output filter: " + mapSet);
					}
				}
			}

			for(Type type : types) { // missing some types ?
				if( ! chdirMap.containsKey(type)) {
					InputParameters inputParameters = operation.getInputParameters();
					for(IOEntity ioEntity : inputParameters.getIOEntities()) {
						Processor<?> processor = inputParameters.getProcessor(ioEntity);
						if(processor instanceof InputProcessor) {
							try {
								MovieMetadatas movieMetadatas = ((InputProcessor)processor).getMovieMetadatas();
								if(movieMetadatas.contains(type)) {
									chdirMap.put(type, Label.input(processor.getIndex(), type));
									break;
								}
							} catch(Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}

			return new HashSet<>(chdirMap.values());
		};
	}

}
