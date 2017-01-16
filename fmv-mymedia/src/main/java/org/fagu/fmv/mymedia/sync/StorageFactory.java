package org.fagu.fmv.mymedia.sync;

/*
 * #%L
 * fmv-mymedia
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.fagu.fmv.utils.ClassResolver;


/**
 * @author f.agu
 */
public abstract class StorageFactory {

	private static final Map<String, StorageFactory> FACTORY_MAP = new HashMap<>();

	static {
		ClassResolver classResolver = new ClassResolver();
		String packageName = StorageFactory.class.getPackage().getName();
		try {
			for(Class<?> findCls : classResolver.find(packageName, c -> {
				if(StorageFactory.class.isAssignableFrom(c)) {
					int mod = c.getModifiers();
					return ! Modifier.isAbstract(mod) && ! Modifier.isInterface(mod) && Modifier.isPublic(mod);
				}
				return false;

			})) {
				@SuppressWarnings("unchecked")
				Class<StorageFactory> cls = (Class<StorageFactory>)findCls;
				register(cls.newInstance());
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final String name;

	/**
	 * @param name
	 */
	public StorageFactory(String name) {
		this.name = Objects.requireNonNull(name);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param properties
	 * @return
	 */
	abstract public Storage create(Properties properties);

	/**
	 * @param propertiesFile
	 * @return
	 * @throws IOException
	 */
	public static Storage create(File propertiesFile) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(propertiesFile)) {
			properties.load(inputStream);
		}
		String type = properties.getProperty("type");
		StorageFactory factory = FACTORY_MAP.get(type);
		return factory != null ? factory.create(properties) : null;
	}

	/**
	 * @param factory
	 */
	public static void register(StorageFactory factory) {
		FACTORY_MAP.put(factory.getName(), factory);
	}

}
