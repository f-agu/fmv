package org.fagu.fmv.mymedia.classify;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fagu.fmv.media.Media;
import org.fagu.fmv.utils.file.FileFinder;


/**
 * @author f.agu
 */
public abstract class AskTimeOffsetComparator<M extends Media> implements MediaTimeComparator<M> {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private final FileFinder<M> fileFinder;

	private Map<String, Long> metadatasMap = new LinkedHashMap<>();

	public AskTimeOffsetComparator(FileFinder<M> fileFinder) {
		this.fileFinder = Objects.requireNonNull(fileFinder);
	}

	public void addMetadatas(String metadatas, long timeDiff) {
		metadatasMap.put(metadatas, timeDiff);
	}

	@Override
	public long getTime(M media) {
		String metadatasKey = media.getDevice();
		Long offset = metadatasMap.get(metadatasKey);
		if(offset == null) {
			offset = askTimeOffset(metadatasKey);
			metadatasMap.put(metadatasKey, offset);
		}
		return media.getTime() + offset;
	}

	// *************************************************************

	private long askTimeOffset(String key) {
		SortedSet<M> images = new TreeSet<>((m1, m2) -> m1.getFile().compareTo(m2.getFile()));
		for(FileFinder<M>.InfosFile infosFile : fileFinder.getAll()) {
			M media = infosFile.getMain();
			if(key.equals(media.getDevice())) {
				images.add(media);
			}
		}
		displayAllFilesFromSameDevice(images, null);
		long offset = 0;
		do {
			offset = scan(key);
			if(offset != 0) {
				displayAllFilesFromSameDevice(images, offset);
			}
		} while( ! scanYesOrNo());

		return offset;
	}

	private long scan(String key) {
		Scanner scanner = new Scanner(System.in);
		while(true) {
			System.out.println(key);
			System.out.print("Diff time in minute ? ");
			String nextLine = scanner.nextLine();
			try {
				return Long.parseLong(nextLine) * 60L * 1000L;
			} catch(Exception e) {
				//
			}
		}

	}

	private boolean scanYesOrNo() {
		return true;
	}

	private void displayAllFilesFromSameDevice(Collection<M> medias, Long offset) {
		for(M media : medias) {
			StringBuilder line = new StringBuilder(50);
			long time = media.getTime();
			line.append(DATE_FORMAT.format(new Date(time)));
			if(offset != null) {
				line.append(" -> ").append(DATE_FORMAT.format(new Date(time + offset)));
			}
			line.append(' ').append(media.getFile());
			System.out.println(line);
		}
	}
}
