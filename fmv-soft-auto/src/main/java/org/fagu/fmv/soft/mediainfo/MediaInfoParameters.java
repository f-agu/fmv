package org.fagu.fmv.soft.mediainfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.fagu.fmv.soft.Soft;


/**
 * @author Oodrive
 * @author f.agu
 * @created 3 juil. 2019 10:42:58
 */
public class MediaInfoParameters {

	private final Soft mediaInfoSoft;

	public MediaInfoParameters() {
		this(MediaInfo.search());
	}

	public MediaInfoParameters(Soft mediaInfoSoft) {
		this.mediaInfoSoft = Objects.requireNonNull(mediaInfoSoft);
	}

	public Info getAllParameters() throws IOException {
		final List<InfoBase> infoBases = new ArrayList<>();
		try (ReadLineDetails0 readLineDetails0 = new ReadLineDetails0(infoBases::add)) {
			mediaInfoSoft.withParameters("--Info-Parameters")
					.addOutReadLine(readLineDetails0)
					.execute();
		}
		return new Info(infoBases);
	}

}