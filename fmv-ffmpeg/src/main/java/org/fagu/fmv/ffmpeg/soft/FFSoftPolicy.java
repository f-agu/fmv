package org.fagu.fmv.ffmpeg.soft;

import java.time.LocalDate;
import java.util.Objects;

import org.fagu.fmv.soft.find.SoftFound;
import org.fagu.version.Version;


/**
 * @author f.agu
 */
public class FFSoftPolicy {

	public static final LocalDate MIN_DATE = LocalDate.of(2014, 01, 01);

	public static final int MIN_BUILD_VERSION = 64000;

	public static final Version MIN_VERSION = new Version(2, 2, 1);

	private FFSoftPolicy() {}

	public static SoftFound toSoftFound(FFInfo ffInfo) {
		Objects.requireNonNull(ffInfo);

		Version version = ffInfo.getVersion().orElse(null);
		if(version != null) {
			return check(version.isUpperOrEqualsThan(MIN_VERSION), ffInfo);
		}

		Integer builtVersion = ffInfo.getBuiltVersion();
		if(builtVersion != null) {
			return check(builtVersion > MIN_BUILD_VERSION, ffInfo);
		}

		LocalDate builtDate = ffInfo.getBuiltDate();
		if(builtDate != null) {
			return check(builtDate.isAfter(MIN_DATE), ffInfo);
		}

		return SoftFound.foundError(ffInfo.getFile(), "Unable to verify the soft: " + ffInfo);
	}

	// ******************************************************

	private static SoftFound check(boolean verify, FFInfo ffInfo) {
		if(verify) {
			return SoftFound.found(ffInfo);
		}
		return SoftFound.foundBadVersion(ffInfo, "require at least " + MIN_VERSION + ", or built " + MIN_BUILD_VERSION
				+ ", or built date 2014-01-01");
	}

}
