package org.fagu.fmv.mymedia.rip.dvd;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
		try (Ripper ripper = Ripper.fromDVDDrive(dvdDrive).build()) {
			ripper.rip();
		}
		System.out.println();
	}

	// ***************************

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

}
