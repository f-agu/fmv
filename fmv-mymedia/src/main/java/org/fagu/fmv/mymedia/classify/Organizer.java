package org.fagu.fmv.mymedia.classify;

/*
 * #%L
 * fmv-mymedia
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.ServiceLoader;
import java.util.function.Function;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;
import org.fagu.fmv.utils.io.UnclosedInputStream;


/**
 * @author f.agu
 */
public class Organizer<F extends FileFinder<M>, M extends Media> {

	private final Class<M> mediaCls;

	public Organizer(Class<M> mediaCls) {
		this.mediaCls = mediaCls;
	}

	public void organize(File destFolder, F finder) throws IOException {
		List<Converter<M>> converters = get(ClassifierProvider.class, t -> t.getConverter(mediaCls, destFolder));
		try (Converter<M> converter = select(converters, "Conversion :", Converter::getTitle)) {

			List<ConverterListener<M>> listeners = get(ClassifierProvider.class, t -> t.getConverterListener(mediaCls));

			List<ClassifierFactory<FileFinder<M>, M>> classifierFactories = get(ClassifierProvider.class, t -> t.getClassifierFactories(mediaCls));
			ClassifierFactory<FileFinder<M>, M> classifierFactory = select(classifierFactories, "Organisation :", ClassifierFactory::getTitle);

			try (Classifier<FileFinder<M>, M> classifier = classifierFactory.create(finder, destFolder)) {
				classifier.classify(converter, createListener(listeners));
			}
		}
	}

	// ***************************************************

	private <T, R> List<R> get(Class<T> cls, Function<T, List<R>> supplier) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(cls);
		List<R> list = new ArrayList<>();
		for(T t : serviceLoader) {
			list.addAll(supplier.apply(t));
		}
		return list;
	}

	private <O> O select(List<O> list, String mainTitle, Function<O, String> titleSupplier) {
		O o = null;
		if(list.isEmpty()) {
			throw new RuntimeException("Never append ! (" + mainTitle + ')');
		}
		int size = list.size();
		if(size == 1) {
			o = list.get(0);
			System.out.println();
			System.out.println(mainTitle + " " + titleSupplier.apply(o));
			System.out.println();

		} else {
			System.out.println();
			System.out.println();
			System.out.println(mainTitle);
			System.out.println();

			for(int i = 0; i < size; i++) {
				O obj = list.get(i);
				System.out.println("  " + (i + 1) + ".  " + titleSupplier.apply(obj));
			}
			System.out.println();
			int choice = - 1;
			try (Scanner scanner = new Scanner(new UnclosedInputStream(System.in))) {
				while(choice < 0 || choice >= size) {
					System.out.print("? ");
					choice = scanner.nextInt() - 1;
				}
			}

			o = list.get(choice);
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	private ConverterListener<M> createListener(final List<ConverterListener<M>> listeners) {
		if(listeners.isEmpty()) {
			return new ConverterListener<M>() {};
		}
		if(listeners.size() == 1) {
			return listeners.get(0);
		}
		return (ConverterListener<M>)Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {ConverterListener.class},
				new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						for(ConverterListener<M> listener : listeners) {
							method.invoke(listener, args);
						}
						return null;
					}
				});
	}
}
