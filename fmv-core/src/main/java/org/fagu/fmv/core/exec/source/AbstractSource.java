package org.fagu.fmv.core.exec.source;

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
import org.fagu.fmv.core.exec.Attributable;
import org.fagu.fmv.core.exec.Identifiable;
import org.fagu.fmv.core.exec.Source;
import org.fagu.fmv.core.project.LoadException;
import org.fagu.fmv.core.project.Project;


/**
 * @author f.agu
 */
public abstract class AbstractSource extends Attributable implements Source {

	/**
	 * 
	 */
	public AbstractSource() {}

	/**
	 * @param project
	 */
	public AbstractSource(Project project) {
		super(project);
	}

	/**
	 * @see org.fagu.fmv.core.exec.Attributable#load(org.fagu.fmv.core.project.Project, org.dom4j.Element,
	 *      org.fagu.fmv.core.exec.Identifiable)
	 */
	@Override
	public void load(Project project, Element fromElement, Identifiable parent) throws LoadException {
		super.load(project, fromElement, parent);

		String code = fromElement.attributeValue("code");
		if( ! getCode().equals(code)) {
			throw new RuntimeException(code + " in " + fromElement.getPath());
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append(getCode()).append(' ').append(super.toString());
		return buf.toString();
	}

}
