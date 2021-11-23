package org.fagu.fmv.mymedia.rip.dvd;

/*-
 * #%L
 * fmv-mymedia
 * %%
 * Copyright (C) 2014 - 2020 fagu
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
