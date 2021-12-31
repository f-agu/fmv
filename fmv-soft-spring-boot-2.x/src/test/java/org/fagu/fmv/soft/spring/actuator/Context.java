package org.fagu.fmv.soft.spring.actuator;

import java.util.Objects;

import org.fagu.fmv.soft.spring.config.SoftIndicatorProperties.Health.CheckPolicy;


/**
 * @author Oodrive
 * @author f.agu
 * @created 7 déc. 2021 09:51:32
 */
class Context {

	private final String title;

	private final CheckPolicy checkPolicy;

	Context(String title, CheckPolicy checkPolicy) {
		this.title = Objects.requireNonNull(title);
		this.checkPolicy = Objects.requireNonNull(checkPolicy);
	}

	CheckPolicy getCheckPolicy() {
		return checkPolicy;
	}

	String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return title;
	}

}
