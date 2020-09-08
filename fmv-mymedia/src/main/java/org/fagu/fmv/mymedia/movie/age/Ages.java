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

/**
 * @author Utilisateur
 * @created 4 mai 2018 20:55:38
 */
public class Ages implements Comparable<Ages> {

	private final String title;

	private final int legal;

	private final int suggested;

	private final double score;

	public Ages(String title, Integer legal, Integer suggested, double score) {
		this.title = title;
		this.legal = legal != null ? legal : suggested;
		this.suggested = suggested != null ? suggested : legal;
		this.score = score;
	}

	public String getTitle() {
		return title;
	}

	public int getLegal() {
		return legal;
	}

	public int getSuggested() {
		return suggested;
	}

	@Override
	public int compareTo(Ages o) {
		return score < o.score ? 1 : - 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + legal;
		result = prime * result + suggested;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Ages other = (Ages)obj;
		if(legal != other.legal)
			return false;
		return suggested == other.suggested;
	}

	@Override
	public String toString() {
		return title;
	}
}
