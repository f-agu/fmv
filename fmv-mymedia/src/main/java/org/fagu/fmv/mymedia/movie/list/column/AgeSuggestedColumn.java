package org.fagu.fmv.mymedia.movie.list.column;

/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class AgeSuggestedColumn extends AgeFilteredColumn {

	public AgeSuggestedColumn() {
		super(Ages::getSuggested);
	}

	@Override
	public String title() {
		return "âge suggéré";
	}

}
