package org.fagu.fmv.soft.win32;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.fagu.fmv.soft.Soft;
import org.fagu.version.Version;
import org.fagu.version.VersionParseException;


/**
 * @author f.agu
 * @created 24 sept. 2019 09:09:00
 */
public class BinaryVersionInfo {

	private BinaryVersionInfo() {}

	public static Optional<Map<String, String>> getInfo(File file) {
		if( ! SystemUtils.IS_OS_WINDOWS) {
			return Optional.empty();
		}
		try {
			Pattern pattern = Pattern.compile("(\\w+)(?: *):(?: *)(.*)");
			Map<String, String> map = new LinkedHashMap<>(32);
			Soft.withExecFile("powershell")
					.withParameters("-nologo",
							"-command",
							"(Get-Item '\"" + file.getAbsoluteFile() + "\"').VersionInfo | format-list")
					// .logCommandLine(cmdLine -> LOGGER.debug(CommandLineUtils.toLine(cmdLine)))
					.addOutReadLine(l -> {
						Matcher matcher = pattern.matcher(l);
						if(matcher.matches()) {
							map.put(matcher.group(1), matcher.group(2));
						}
					}).execute();
			return Optional.of(map);
		} catch(Exception e) { // ignore
			return Optional.empty();
		}
	}

	public static Optional<Version> getFileVersion(File file) {
		return getInfo(file)
				.map(m -> m.get("FileVersion"))
				.map(v -> {
					try {
						return Version.parse(v.trim());
					} catch(VersionParseException e) { // ignore
						return null;
					}
				})
				.filter(Objects::nonNull);
	}

}
