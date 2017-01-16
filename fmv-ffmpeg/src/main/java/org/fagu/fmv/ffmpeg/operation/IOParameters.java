package org.fagu.fmv.ffmpeg.operation;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fagu.fmv.ffmpeg.operation.Parameter.Way;
import org.fagu.fmv.utils.collection.MapList;
import org.fagu.fmv.utils.collection.MultiValueMaps;


/**
 * @author f.agu
 */
public abstract class IOParameters {

	protected final AbstractOperation<?, ?> operation;

	private final MapList<IOEntity, Parameter> paramsMap;

	/**
	 * @param abstractOperation
	 */
	public IOParameters(AbstractOperation<?, ?> abstractOperation) {
		this.operation = abstractOperation;
		paramsMap = MultiValueMaps.list(new LinkedHashMap<>(2), () -> new ArrayList<>(4));
	}

	/**
	 * @param ioEntity
	 * @return
	 */
	public boolean contains(IOEntity ioEntity) {
		return paramsMap.containsKey(ioEntity);
	}

	/**
	 * @param paramName
	 * @return
	 */
	public boolean contains(String paramName) {
		String mParamName = '-' + paramName;
		for(List<Parameter> parameters : paramsMap.values()) {
			for(Parameter parameter : parameters) {
				if(paramName.equals(parameter.getName()) || mParamName.equals(parameter.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param parameter
	 * @return
	 */
	public boolean contains(Parameter parameter) {
		return contains(parameter.getIOEntity(), parameter.getName());
	}

	/**
	 * @param ioEntity
	 * @param parameterName
	 * @return
	 */
	public boolean contains(IOEntity ioEntity, String parameterName) {
		List<Parameter> list = paramsMap.get(ioEntity);
		if(list == null) {
			return false;
		}
		return list.contains(parameterName);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return paramsMap.isEmpty();
	}

	/**
	 * @param ioEntity
	 * @param processor
	 */
	public void add(IOEntity ioEntity, Processor<?> processor) {
		if(ioEntity == null) {
			return;
		}
		paramsMap.addEmpty(ioEntity);
		operation.addProcessor(ioEntity, processor);
		if(ioEntity instanceof LibLog) {
			operation.add((LibLog)ioEntity);
		}
	}

	/**
	 * @param parameter
	 */
	public void add(Parameter parameter) {
		List<Parameter> list = paramsMap.addEmpty(parameter.getIOEntity());
		list.add(parameter);
	}

	/**
	 * @param index
	 * @param parameter
	 */
	public void add(int index, Parameter parameter) {
		List<Parameter> list = paramsMap.addEmpty(parameter.getIOEntity());
		list.add(index, parameter);
	}

	/**
	 * @return
	 */
	public Set<IOEntity> getIOEntities() {
		return Collections.unmodifiableSet(paramsMap.keySet());
	}

	/**
	 * @param ioEntity
	 * @return
	 */
	public Processor<?> getProcessor(IOEntity ioEntity) {
		return operation.getProcessor(ioEntity);
	}

	/**
	 * @return
	 */
	public Operation<?, ?> getOperation() {
		return operation;
	}

	/**
	 * @param ioEntity
	 * @param way
	 * @return
	 */
	public List<Parameter> getParameters(IOEntity ioEntity, Way way) {
		List<Parameter> list = paramsMap.get(ioEntity);
		if(list == null) {
			return Collections.emptyList();
		}
		List<Parameter> out = new ArrayList<>(list.size());
		for(Parameter parameter : list) {
			if(parameter.getWay() == way) {
				out.add(parameter);
			}
		}
		return out;
	}

	/**
	 * @param name
	 */
	public void removeParameter(String name) {
		for(List<Parameter> list : paramsMap.values()) {
			removeParameter(list, name);
		}
	}

	/**
	 * @param ioEntity
	 * @param name
	 */
	public void removeParameter(IOEntity ioEntity, String name) {
		List<Parameter> list = paramsMap.get(ioEntity);
		if(list != null) {
			removeParameter(list, name);
		}
	}

	/**
	 * @param parameter
	 * @return
	 */
	public boolean removeParameter(Parameter parameter) {
		List<Parameter> list = paramsMap.get(parameter.getIOEntity());
		if(list != null) {
			return list.remove(parameter);
		}
		return false;
	}

	/**
	 * @param commands
	 */
	public void toArguments(Collection<String> commands) {
		for(IOEntity ioEntity : getIOEntities()) {
			// before
			for(Parameter parameter : getParameters(ioEntity, Way.BEFORE)) {
				parameter.addTo(commands);
			}

			// ioEntity (source or ouput)
			String commandPrefixFile = getCommandPrefixFile();
			if(StringUtils.isNotEmpty(commandPrefixFile)) {
				commands.add(commandPrefixFile);
			}
			commands.add(ioEntity.toString());

			// after
			for(Parameter parameter : getParameters(ioEntity, Way.AFTER)) {
				parameter.addTo(commands);
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		List<String> list = new ArrayList<>();
		toArguments(list);
		return StringUtils.join(list, ',');
	}

	// ************************************************************

	/**
	 * @return
	 */
	abstract String getCommandPrefixFile();

	// ************************************************************

	/**
	 * @param ioParameters
	 */
	protected void addAll(IOParameters ioParameters) {
		for(Entry<IOEntity, List<Parameter>> entry : ioParameters.paramsMap.entrySet()) {
			List<Parameter> list = paramsMap.addEmpty(entry.getKey());
			list.addAll(entry.getValue());
		}
	}

	// ************************************************************

	/**
	 * @param parameters
	 * @param name
	 */
	private void removeParameter(List<Parameter> parameters, String name) {
		Iterator<Parameter> iterator = parameters.iterator();
		while(iterator.hasNext()) {
			if(name.equals(iterator.next().getName())) {
				iterator.remove();
			}
		}

	}

}
