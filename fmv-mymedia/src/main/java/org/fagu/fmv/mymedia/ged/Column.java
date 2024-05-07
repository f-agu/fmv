package org.fagu.fmv.mymedia.ged;

import org.folg.gedcom.model.Person;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 mai 2024 11:08:05
 */
public interface Column {

	String getTitle();

	String getValue(Person person);
}
