package org.fagu.fmv.mymedia.movie.list.datatype;

import java.io.File;
import java.util.Optional;

import org.fagu.fmv.ffmpeg.FFHelper;
import org.fagu.fmv.ffmpeg.metadatas.MovieMetadatas;
import org.fagu.fmv.mymedia.movie.list.DataType;


/**
 * @author Utilisateur
 * @created 4 mai 2018 21:41:23
 */
public class FFDataType implements DataType<MovieMetadatas> {

	public static final DataType<MovieMetadatas> FF = new CachedDataType<>(new FFDataType());

	private FFDataType() {}

	@Override
	public Optional<MovieMetadatas> extractData(File file) {
		try {
			return Optional.of(FFHelper.videoMetadatas(file));
		} catch(Exception e) {
			return Optional.empty();
		}
	}

}
