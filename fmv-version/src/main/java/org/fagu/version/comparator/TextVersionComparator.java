package org.fagu.version.comparator;

import org.fagu.version.Version;
import org.fagu.version.VersionComparator;


/**
 * @author f.agu
 */
public class TextVersionComparator implements VersionComparator {

	@Override
	public int compare(Version o1, Version o2) {
		String s1 = toString(o1);
		String s2 = toString(o2);
		if(s1 == null && s2 == null) {
			return 0;
		}
		if(s1 == null) {
			return - 1;
		}
		if(s2 == null) {
			return 1;
		}
		return s1.compareTo(s2);
	}

	@Override
	public int getOrder() {
		return 10000;
	}

	// ****************************************************

	private String toString(Version version) {
		String realValue = version.getText();
		String digitValue = version.getDigitValue();
		if(digitValue.equals(realValue)) {
			return null;
		}
		return realValue;
	}

}
