package org.fagu.fmv.soft.mediainfo;

import java.util.OptionalDouble;
import java.util.OptionalInt;


/**
 * @author Utilisateur
 * @created 7 avr. 2018 15:15:50
 */
public interface ParsableType {

	String getString(String key);

	default OptionalInt getInt(String key) {
		String value = getString(key);
		try {
			return OptionalInt.of(Integer.parseInt(value));
		} catch(NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	default OptionalDouble getDouble(String key) {
		String value = getString(key);
		try {
			return OptionalDouble.of(Double.parseDouble(value));
		} catch(NumberFormatException e) {
			return OptionalDouble.empty();
		}
	}

}
