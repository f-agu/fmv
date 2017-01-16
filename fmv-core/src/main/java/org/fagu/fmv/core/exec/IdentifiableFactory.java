package org.fagu.fmv.core.exec;

/*
 * #%L
 * fmv-core
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


import org.dom4j.Element;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.Project;


/**
 * @author f.agu
 */
public class IdentifiableFactory<T extends Identifiable> extends Factory<T> {

	/**
	 * @param clazz
	 */
	public IdentifiableFactory(Class<T> clazz) {
		super(clazz, identifiable -> identifiable.getCode());
	}

	/**
	 * @param project
	 * @param element
	 * @param parent
	 * @return
	 * @throws LoadException
	 */
	public T get(Project project, Element element, Identifiable parent) throws LoadException {
		String code = element.attributeValue("code");
		T t = get(code);
		t.load(project, element, parent);
		return t;
	}

}
