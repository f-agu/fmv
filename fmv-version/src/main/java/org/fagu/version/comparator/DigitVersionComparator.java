package org.fagu.version.comparator;

import java.util.Map;

import org.fagu.version.Version;
import org.fagu.version.VersionComparator;
import org.fagu.version.VersionField;
import org.fagu.version.VersionUnit;


/**
 * @author f.agu
 */
public class DigitVersionComparator implements VersionComparator {

	@Override
	public int compare(Version o1, Version o2) {
		VersionField othervf = null;
		Map<VersionUnit, VersionField> longMap = null;
		Map<VersionUnit, VersionField> shortMap = null;
		int sign = 1;
		if(o1.size() > o2.size()) { // me long, other short
			longMap = o1.toMap();
			shortMap = o2.toMap();
		} else { // me short, other long
			longMap = o2.toMap();
			shortMap = o1.toMap();
			sign = - 1;
		}

		for(VersionField versionField : longMap.values()) {
			othervf = shortMap.get(versionField.getVersionUnit());
			if(othervf == null) {
				if(versionField.getValue() == 0) {
					continue;
				}
				return sign;
			}
			if(othervf.getValue() < versionField.getValue()) {
				return sign;
			} else if(othervf.getValue() > versionField.getValue()) {
				return - sign;
			}
		}
		return 0;
	}

	@Override
	public int getOrder() {
		return 1000;
	}

}
