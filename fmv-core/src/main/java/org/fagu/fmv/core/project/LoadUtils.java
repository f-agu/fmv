package org.fagu.fmv.core.project;

/*
 * #%L
 * fmv-core
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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fagu.fmv.utils.time.Duration;
import org.fagu.fmv.utils.time.Time;


/**
 * @author f.agu
 */
public class LoadUtils {

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> elements(Element element) {
		if(element == null) {
			return Collections.emptyList();
		}
		return element.elements();
	}

	/**
	 * @param element
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> elements(Element element, String name) {
		if(element == null) {
			return Collections.emptyList();
		}
		return element.elements(name);
	}

	/**
	 * @param inEement
	 * @param elementName
	 * @return
	 */
	public static Element elementRequire(Element inEement, String elementName) throws LoadException {
		Element element = inEement.element(elementName);
		if(element == null) {
			throw LoadException.missElement(inEement, elementName);
		}
		return element;
	}

	/**
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Attribute> attributes(Element element) {
		if(element == null) {
			return Collections.emptyList();
		}
		return element.attributes();
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static String attributeRequire(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		if(value == null) {
			throw LoadException.attribute(element, attributeName);
		}
		return value;
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static Integer attributeInteger(Element element, String attributeName) {
		String value = element.attributeValue(attributeName);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			return null;
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static Duration attributeDuration(Element element, String attributeName) {
		String value = element.attributeValue(attributeName);
		if(value == null) {
			return null;
		}
		try {
			return Duration.parse(value);
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static int attributeRequireInt(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			throw LoadException.attribute(element, attributeName);
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static float attributeRequireFloat(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		try {
			return Float.parseFloat(value);
		} catch(NumberFormatException e) {
			throw LoadException.attribute(element, attributeName);
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static double attributeRequireDouble(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		try {
			return Double.parseDouble(value);
		} catch(NumberFormatException e) {
			throw LoadException.attribute(element, attributeName);
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 */
	public static Time attributeTime(Element element, String attributeName) {
		String value = element.attributeValue(attributeName);
		if(StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Time.parse(value);
		} catch(IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static Time attributeRequireTime(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		try {
			return Time.parse(value);
		} catch(IllegalArgumentException e) {
			throw LoadException.attribute(element, attributeName);
		}
	}

	/**
	 * @param element
	 * @param attributeName
	 * @return
	 * @throws LoadException
	 */
	public static Duration attributeRequireDuration(Element element, String attributeName) throws LoadException {
		String value = element.attributeValue(attributeName);
		try {
			return Duration.parse(value);
		} catch(IllegalArgumentException e) {
			throw LoadException.attribute(element, attributeName);
		}
	}

}
