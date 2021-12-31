package org.fagu.fmv.soft.spring.actuator;

import java.io.File;
import java.util.Objects;

import org.fagu.fmv.soft.find.SoftInfo;


/**
 * @author f.agu
 */
public class TestSoftInfo extends SoftInfo {

	private final String info;

	public TestSoftInfo(String info) {
		super(new File("pom.xml"), "test");
		this.info = Objects.requireNonNull(info);
	}

	@Override
	public int compareTo(SoftInfo other) {
		return info.compareTo(((TestSoftInfo)other).info);
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return getInfo();
	}

}
