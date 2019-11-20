package org.fagu.fmv.media;

/**
 * @author Oodrive
 * @author f.agu
 * @created 20 nov. 2019 15:17:46
 */
public interface Parser<O> {

	boolean accept(Object i);

	O parse(Object i);

}
