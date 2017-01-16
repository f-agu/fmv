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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fagu.fmv.image.Image;
import org.fagu.fmv.media.Media;
import org.fagu.fmv.mymedia.classify.image.ReduceImageConverter;
import org.fagu.fmv.mymedia.classify.movie.Movie;
import org.fagu.fmv.mymedia.classify.movie.MovieScriptConverter;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public class DefaultClassifierProvider implements ClassifierProvider {

	/**
	 * 
	 */
	public DefaultClassifierProvider() {}

	/**
	 * @see org.fagu.fmv.mymedia.classify.ClassifierProvider#getConverter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Media> List<Converter<M>> getConverter(Class<? extends M> mediaCls, File destFolder) {
		List<Converter<M>> list = new ArrayList<>();
		list.add((Converter<M>)new MoveConverter(destFolder));
		if(mediaCls == Image.class) {
			list.add((Converter<M>)new ReduceImageConverter(destFolder));
		} else if(mediaCls == Movie.class) {
			list.add((Converter<M>)new MovieScriptConverter(destFolder));
		}
		return list;
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.ClassifierProvider#getConverterListener(java.lang.Class)
	 */
	@Override
	public <M extends Media> List<ConverterListener<M>> getConverterListener(Class<? extends M> mediaCls) {
		return Collections.singletonList(new ConverterListener<M>() {

			/**
			 * @see org.fagu.fmv.mymedia.classify.ConverterListener#eventPreConvert(org.fagu.fmv.media.Media,
			 *      java.io.File)
			 */
			public void eventPreConvert(M srcMedia, File destFile) {
				System.out.println("Convert " + srcMedia.getFile());
			}
		});
	}

	/**
	 * @see org.fagu.fmv.mymedia.classify.ClassifierProvider#getClassifierFactories(java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <F extends FileFinder<M>, M extends Media> List<ClassifierFactory<F, M>> getClassifierFactories(Class<? extends Media> mediaCls) {
		List<ClassifierFactory<F, M>> list = new ArrayList<>();
		if(mediaCls == Image.class) {
			list.add((ClassifierFactory<F, M>)ClassifierFactories.imageSejour());
		} else if(mediaCls == Movie.class) {
			list.add((ClassifierFactory<F, M>)ClassifierFactories.movie());
		}
		return list;
	}

}
