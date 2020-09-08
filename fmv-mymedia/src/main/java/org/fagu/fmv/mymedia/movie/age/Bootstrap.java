package org.fagu.fmv.mymedia.movie.age;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
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

import java.util.Optional;

import org.fagu.fmv.mymedia.logger.Loggers;
import org.fagu.fmv.mymedia.movie.age.AgesCache.AgesElement;


/**
 * @author f.agu
 * @created 6 juil. 2018 23:04:37
 */
public class Bootstrap {

	public static void main(String[] args) {
		AgesCache agesCache = AgesCache.getInstance();
		AgesFilm agesFilm = new AgesFilm(Loggers.noOperation(), agesCache);
		for(String title : agesCache.getTitles()) {
			AgesElement agesElement = agesCache.find(title).get();
			if( ! agesElement.isPresent()) {
				// System.out.println(title);
				Optional<Ages> ages = agesFilm.getAges(title);
				if(ages.isPresent()) {
					agesElement = AgesElement.knowAges(title, ages.get());
				} else {
					agesElement = AgesElement.unknowAges(title, true);
				}
			}
			System.out.println(agesElement.toString());
		}
	}

}
