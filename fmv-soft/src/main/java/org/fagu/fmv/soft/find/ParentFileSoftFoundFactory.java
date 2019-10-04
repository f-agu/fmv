package org.fagu.fmv.soft.find;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.fagu.fmv.soft.find.info.VersionSoftInfo;
import org.fagu.fmv.soft.win32.BinaryVersionInfo;
import org.fagu.version.Version;


/**
 * @author f.agu
 * @created 16 sept. 2019 17:03:27
 */
public class ParentFileSoftFoundFactory implements SoftFoundFactory {

	private final String softName;

	public ParentFileSoftFoundFactory(String softName) {
		this.softName = Objects.requireNonNull(softName);
	}

	@Override
	public SoftFound create(File file, Locator locator, SoftPolicy softPolicy) throws IOException {
		Version version = BinaryVersionInfo.getFileVersion(file).orElse(null);
		if(version != null) {
			return SoftFound.found(new VersionSoftInfo(file, softName, version));
		}
		return SoftFound.found(file.getParentFile());
	}

}
