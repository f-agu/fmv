package org.fagu.fmv.mymedia.movie.list.datatype;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:37:47
 */
public class ValuesDataType implements DataType<Map<String, String>> {

	public static final DataType<Map<String, String>> VALUES = new ValuesDataType();

	private ValuesDataType() {}

	@Override
	public Optional<Map<String, String>> extractData(File file) {
		throw new RuntimeException("Never call !");
	}
}
