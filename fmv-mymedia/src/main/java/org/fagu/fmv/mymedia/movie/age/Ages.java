package org.fagu.fmv.mymedia.movie.age;

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
