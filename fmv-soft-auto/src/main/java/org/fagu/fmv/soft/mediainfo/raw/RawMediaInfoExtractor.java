package org.fagu.fmv.soft.mediainfo.raw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.fagu.fmv.soft.Soft;
import org.fagu.fmv.soft.SoftExecutor;
import org.fagu.fmv.soft.SoftExecutorHelper;
import org.fagu.fmv.soft.mediainfo.Info;
import org.fagu.fmv.soft.mediainfo.InfoBase;
import org.fagu.fmv.soft.mediainfo.InfoType;
import org.fagu.fmv.soft.mediainfo.MediaInfo;


/**
 * @author f.agu
 * @created 7 avr. 2018 14:06:28
 */
public class RawMediaInfoExtractor extends SoftExecutorHelper<RawMediaInfoExtractor> {

	private final Soft mediaInfoSoft;

	public RawMediaInfoExtractor() {
		this(MediaInfo.search());
	}

	public RawMediaInfoExtractor(Soft mediaInfoSoft) {
		this.mediaInfoSoft = Objects.requireNonNull(mediaInfoSoft);
	}

	public Info extract(File file) throws IOException {
		return extractAll(Collections.singletonList(file)).get(file);
	}

	public Map<File, Info> extractAll(File... files) throws IOException {
		return extractAll(Arrays.asList(files));
	}

	public Map<File, Info> extractAll(Collection<File> files) throws IOException {
		List<String> parameters = new ArrayList<>(files.size() + 1);
		parameters.add("--Details=0");
		files.stream()
				.map(File::toString)
				.forEach(parameters::add);

		final Map<File, Info> fileInfoMap = new HashMap<>();
		final Iterator<File> iterator = files.iterator();
		final List<InfoBase> infoBases = new ArrayList<>();
		try (RawDetails0ReadLine readLineDetails0 = new RawDetails0ReadLine(info -> {
			if(info.getType() == InfoType.GENERAL && ! infoBases.isEmpty()) {
				fileInfoMap.put(iterator.next(), new Info(infoBases));
				infoBases.clear();
			}
			infoBases.add(info);
		})) {
			SoftExecutor softExecutor = mediaInfoSoft.withParameters(parameters)
					.addOutReadLine(readLineDetails0);
			populate(softExecutor);
			softExecutor.execute();
		}
		return fileInfoMap;
	}

}
