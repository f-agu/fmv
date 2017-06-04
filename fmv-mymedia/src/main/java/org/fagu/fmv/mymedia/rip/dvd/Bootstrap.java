package org.fagu.fmv.mymedia.rip.dvd;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.fagu.fmv.soft.mplayer.MPlayer;
import org.fagu.fmv.soft.mplayer.MPlayerDump;
import org.fagu.fmv.soft.mplayer.MPlayerTitle;
import org.fagu.fmv.soft.mplayer.MPlayerTitles;


/**
 * @author fagu
 */
public class Bootstrap {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Optional<File> dvdDriveOpt = findDVDDrive();
		if( ! dvdDriveOpt.isPresent()) {
			return;
		}
		File dvdDrive = dvdDriveOpt.get();

		System.out.println("Analyzing DVD on " + dvdDrive + "...");
		MPlayerTitles mPlayerTitles = MPlayerTitles.fromDVDDrive(dvdDrive).find();
		SortedSet<MPlayerTitle> titlesLongest = mPlayerTitles.getTitlesLongest();
		System.out.println("Find " + titlesLongest.size() + " titles: " + titlesLongest.stream()
				.limit(5)
				.map(t -> t.getNum() + "/" + t.getLength())
				.collect(Collectors.joining(", ")));
		MPlayerTitle first = titlesLongest.first();

		String dvdName = getDVDName(dvdDrive, first.getNum());

		File tmpDirectory = new File("d:\\tmp\\dvd-rip");
		if( ! tmpDirectory.exists() && ! tmpDirectory.mkdirs()) {
			throw new IOException("Unable to make directory: " + tmpDirectory);
		}
		File outFile = File.createTempFile("dvd-" + dvdName + "-", ".vob", tmpDirectory);

		MPlayerDump mPlayerDump = MPlayerDump.fromDVDDrive(dvdDrive).dump(first.getNum(), outFile);

	}

	/**
	 * @return
	 * @throws IOException
	 */
	private static Optional<File> findDVDDrive() {
		List<File> dvdDrives = findDVDDrives();
		if(dvdDrives.isEmpty()) {
			System.out.println("DVD drive not found");
			return Optional.empty();
		}
		if(dvdDrives.size() != 1) {
			System.out.println("Found too many DVD drives: " + dvdDrives);
			return Optional.empty();
		}
		return Optional.of(dvdDrives.get(0));
	}

	/**
	 * @return
	 */
	private static List<File> findDVDDrives() {
		List<File> drives = new ArrayList<>();
		for(File f : File.listRoots()) {
			try {
				FileStore store = Files.getFileStore(f.toPath());
				if("UDF".equals(store.type())) {
					drives.add(f);
				}
			} catch(IOException e) {
				// ignore
			}
		}
		return drives;
	}

	/**
	 * @param dvdDrive
	 * @param titleNum
	 * @return
	 * @throws IOException
	 */
	private static String getDVDName(File dvdDrive, int titleNum) throws IOException {
		List<String> params = new ArrayList<>();
		params.add("-noquiet");
		params.add("-slave");
		params.add("-identify");
		params.add("-dvd-device");
		params.add(dvdDrive.toString());
		params.add("-frames");
		params.add("0");
		params.add("dvd://" + titleNum);

		MutableObject<String> volumeId = new MutableObject<>();
		MPlayer.search()
				.withParameters(params)
				.addOutReadLine(l -> {
					if(l.startsWith("ID_DVD_VOLUME_ID=")) {
						volumeId.setValue(StringUtils.substringAfter(l, "="));
					}
				})
				.execute();
		return volumeId.getValue();
	}

}
