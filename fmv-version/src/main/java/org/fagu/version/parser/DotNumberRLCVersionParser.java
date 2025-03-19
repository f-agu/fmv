package org.fagu.version.parser;

import java.util.regex.Pattern;

import org.fagu.version.ReleaseLifeCycle;
import org.fagu.version.Version;
import org.fagu.version.Version.VersionBuilder;
import org.fagu.version.VersionParseException;
import org.fagu.version.VersionParser;


/**
 * @author f.agu
 */
public class DotNumberRLCVersionParser implements VersionParser {

	public static final String REGEX = "([0-9]+)(\\.[0-9]+)*-(\\w+)";

	private static final Pattern PATTERN = Pattern.compile(REGEX);

	public static final DotNumberRLCVersionParser INSTANCE = new DotNumberRLCVersionParser();

	@Override
	public boolean test(String str) {
		return str == null ? false : PATTERN.matcher(str).matches();
	}

	@Override
	public Version parse(String str) throws VersionParseException {
		if(test(str)) {
			VersionBuilder versionBuilder = DotNumberVersionParser.parseToBuilder(str.substring(0, str.indexOf('-')));
			versionBuilder.text(str);
			ReleaseLifeCycle rlc = ReleaseLifeCycle.parse(getReleaseLifeCycle(str));
			if(rlc != null) {
				versionBuilder.releaseLifeCycle(rlc);
			}
			return versionBuilder.build();
		}
		throw new VersionParseException(str);
	}

	public static String getReleaseLifeCycle(String str) {
		if(str != null && PATTERN.matcher(str).matches()) {
			return str.substring(str.indexOf('-') + 1);
		}
		return "";
	}
}
