package org.fagu.fmv.mymedia.movie.list.column;

/**
 * @author Utilisateur
 * @created 4 mai 2018 14:37:34
 */
public class AgeLegalColumn extends AgeFilteredColumn {

	public AgeLegalColumn() {
		super(Ages::getLegal);
	}

	@Override
	public String title() {
		return "âge legal";
	}

}
