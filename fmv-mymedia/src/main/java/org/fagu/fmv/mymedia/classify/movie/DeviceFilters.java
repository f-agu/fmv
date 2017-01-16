package org.fagu.fmv.mymedia.classify.movie;

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


import java.util.function.Predicate;


/**
 * @author f.agu
 */
public class DeviceFilters {

	/**
	 * 
	 */
	public DeviceFilters() {}

	// -------

	/**
	 * @return
	 */
	public static Predicate<Movie> canon500D() {
		return byComptableBrands("qt  CAEP").and(byMajorBrand("qt"));
	}

	/**
	 * @return
	 */
	public static Predicate<Movie> gopro() {
		return byHandlerName("GoProa AVC");
	}

	/**
	 * @return
	 */
	public static Predicate<Movie> nikonCoolpixS10() {
		throw new RuntimeException("undefined");
	}

	/**
	 * @return
	 */
	public static Predicate<Movie> android() {
		return byHandlerName("VideoHandle").and(byComptableBrands("isom3gp4")).and(byMajorBrand("isom"));
	}

	// -------

	/**
	 * @param name
	 * @return
	 */
	public static Predicate<Movie> byHandlerName(String name) {
		return m -> name.equals(m.getMetadatas().getVideoStream().handlerName());
	}

	/**
	 * @param name
	 * @return
	 */
	public static Predicate<Movie> byComptableBrands(String brands) {
		return m -> brands.equals(m.getMetadatas().getFormat().compatibleBrands());
	}

	/**
	 * @param name
	 * @return
	 */
	public static Predicate<Movie> byMajorBrand(final String brand) {
		return m -> brand.equals(m.getMetadatas().getFormat().majorBrand());
	}

}
