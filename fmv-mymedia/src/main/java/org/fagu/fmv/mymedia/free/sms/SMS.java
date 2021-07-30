package org.fagu.fmv.mymedia.free.sms;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2021 09:19:00
 */
public final class SMS {

	private final String phoneNumber;

	private final LocalDateTime localDateTime;

	SMS(String phoneNumber, LocalDateTime localDateTime) {
		this.phoneNumber = Objects.requireNonNull(phoneNumber);
		this.localDateTime = Objects.requireNonNull(localDateTime);
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

}
