package org.fagu.fmv.media;

import java.io.File;

/*
 * #%L
 * fmv-media
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

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import org.fagu.fmv.soft.exec.exception.ExceptionKnown;
import org.fagu.fmv.soft.exec.exception.NestedException;
import org.fagu.fmv.utils.ClassResolver;


/**
 * @author f.agu
 */
public abstract class MetadatasFactory implements Predicate<FileType> {

	private static final List<MetadatasFactory> METADATAS_FACTORIES = new ArrayList<>();

	private final Class<? extends ExceptionKnown> exceptionKnownSPIClass;

	private List<ExceptionKnown> exceptionKnownList;

	/**
	 * @param exceptionKnownSPIClass
	 */
	public MetadatasFactory(Class<? extends ExceptionKnown> exceptionKnownSPIClass) {
		this.exceptionKnownSPIClass = exceptionKnownSPIClass;
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	abstract public Metadatas extract(File file) throws IOException;

	/**
	 * @param json
	 * @return
	 */
	abstract public Metadatas parseJSON(String json);

	/**
	 * @return
	 */
	public List<ExceptionKnown> getExceptionKnowns() {
		if(exceptionKnownList == null) {
			List<ExceptionKnown> list = new ArrayList<>();
			for(ExceptionKnown exceptionKnown : ServiceLoader.load(exceptionKnownSPIClass)) {
				list.add(exceptionKnown);
			}
			exceptionKnownList = list;
		}
		return exceptionKnownList;
	}

	/**
	 * @param e
	 * @return
	 */
	public Optional<ExceptionKnown> isExceptionKnown(Exception e) {
		NestedException nestedException = new NestedException(e);
		return getExceptionKnowns().stream()
				.filter(ek -> ek.test(nestedException))
				.findFirst();
	}

	// --------------------------------------------------

	/**
	 * @param fileType
	 * @return
	 */
	public static MetadatasFactory createFactory(FileType fileType) {
		search();
		for(MetadatasFactory metadatasFactory : METADATAS_FACTORIES) {
			if(metadatasFactory.test(fileType)) {
				return metadatasFactory;
			}
		}
		throw new IllegalArgumentException("FileType undefined: " + fileType);
	}

	/**
	 * @param fileType
	 * @return
	 */
	public static Metadatas parseJSON(FileType fileType, String json) {
		MetadatasFactory metadatasFactory = createFactory(fileType);
		return metadatasFactory.parseJSON(json);
	}

	/**
	 * @param metadatasFactory
	 */
	public static void register(MetadatasFactory metadatasFactory) {
		METADATAS_FACTORIES.add(metadatasFactory);
	}

	/**
	 * @param packageName
	 */
	public static void register(String packageName) {
		ClassResolver classResolver = new ClassResolver();
		try {
			for(Class<?> findCls : classResolver.find(packageName, cls -> {
				if(MetadatasFactory.class.isAssignableFrom(cls)) {
					int mod = cls.getModifiers();
					return ! Modifier.isAbstract(mod) && ! Modifier.isInterface(mod) && Modifier.isPublic(mod);
				}
				return false;
			})) {
				@SuppressWarnings("unchecked")
				Class<MetadatasFactory> cmdClass = (Class<MetadatasFactory>)findCls;
				register(cmdClass.newInstance());
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		} catch(InstantiationException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	// ************************************************

	/**
	 *
	 */
	private static void search() {
		if(METADATAS_FACTORIES.isEmpty()) {
			ServiceLoader.load(MetadatasFactory.class).forEach(MetadatasFactory::register);
		}
	}

}