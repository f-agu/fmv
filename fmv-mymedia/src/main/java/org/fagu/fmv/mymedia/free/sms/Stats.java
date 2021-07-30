package org.fagu.fmv.mymedia.free.sms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2021 09:20:03
 */
class Stats {

	private Stats() {}

	static void all(List<SMS> smsList) {
		LocalDateTime minDateTime = null;
		LocalDateTime maxDateTime = null;
		for(SMS sms : smsList) {
			if(minDateTime == null) {
				minDateTime = maxDateTime = sms.getLocalDateTime();
			} else {
				minDateTime = Stream.of(minDateTime, sms.getLocalDateTime())
						.collect(Collectors.minBy(Comparator.comparing(Function.identity())))
						.get();
				maxDateTime = Stream.of(maxDateTime, sms.getLocalDateTime())
						.collect(Collectors.maxBy(Comparator.comparing(Function.identity())))
						.get();
			}
		}

		System.out.println(smsList.size() + " SMS");
		System.out.println(minDateTime + " -> " + maxDateTime);
		System.out.println();
		Stats.favoritePhoneNumber(smsList);
		System.out.println();
		Stats.favoriteTime(smsList);
		System.out.println();
		Stats.byDate(smsList);
		System.out.println();
		Stats.firstHourByDate(smsList);
	}

	static void favoritePhoneNumber(List<SMS> smsList) {
		Map<String, SMSByNumber> byNumberSMS = new TreeMap<>();
		smsList.forEach(sms -> byNumberSMS.computeIfAbsent(sms.getPhoneNumber(), SMSByNumber::new).smsList.add(sms));
		SortedSet<SMSByNumber> byNumberSet = new TreeSet<>(Comparator.comparingInt(SMSByNumber::count).reversed());
		byNumberSMS.values().forEach(byNumberSet::add);
		byNumberSet.stream()
				.limit(20)
				.forEach(smsbn -> System.out.println(smsbn.phoneNumber + "\t" + smsbn.count()));
		System.out.println();
	}

	static void favoriteTime(List<SMS> smsList) {
		Map<LocalTime, SMSByHourQuarter> byTimeSMS = new TreeMap<>();
		smsList.forEach(sms -> byTimeSMS.computeIfAbsent(SMSByHourQuarter.cutByQuarter(sms.getLocalDateTime()), SMSByHourQuarter::new).smsList.add(
				sms));
		byTimeSMS.forEach((t, s) -> System.out.println(t + "\t" + s.smsList.size()));
	}

	static void byDate(List<SMS> smsList) {
		Map<LocalDate, SMSByDate> byDateSMS = new TreeMap<>();
		smsList.forEach(sms -> byDateSMS.computeIfAbsent(SMSByDate.cutByDate(sms.getLocalDateTime()), SMSByDate::new).smsList.add(sms));
		byDateSMS.forEach((t, s) -> System.out.println(t + "\t" + s.smsList.size()));
	}

	static void firstHourByDate(List<SMS> smsList) {
		Map<LocalDate, SMS> byDateSMS = new TreeMap<>();
		smsList.forEach(sms -> byDateSMS.merge(
				SMSByDate.cutByDate(sms.getLocalDateTime()),
				sms,
				(old, nw) -> old.getLocalDateTime().isBefore(nw.getLocalDateTime()) ? old : nw));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		byDateSMS.forEach((t, s) -> System.out.println(t + "\t" + formatter.format(s.getLocalDateTime().toLocalTime())));
	}

	// -------------------------------------------------------

	public static final class SMSByNumber {

		private final String phoneNumber;

		private final List<SMS> smsList = new ArrayList<>();

		private SMSByNumber(String phoneNumber) {
			this.phoneNumber = Objects.requireNonNull(phoneNumber);
		}

		public int count() {
			return smsList.size();
		}
	}

	// -------------------------------------------------------

	public static final class SMSByHourQuarter {

		private final LocalTime localTime;

		private final List<SMS> smsList = new ArrayList<>();

		private SMSByHourQuarter(LocalTime localTime) {
			this.localTime = Objects.requireNonNull(localTime);
		}

		private static LocalTime cutByQuarter(LocalDateTime localDateTime) {
			int minMinute = 15 * (localDateTime.getMinute() / 15);
			return LocalTime.of(localDateTime.getHour(), minMinute);
		}

		public int count() {
			return smsList.size();
		}
	}

	// -------------------------------------------------------

	public static final class SMSByDate {

		private final LocalDate localDate;

		private final List<SMS> smsList = new ArrayList<>();

		private SMSByDate(LocalDate localDate) {
			this.localDate = Objects.requireNonNull(localDate);
		}

		private static LocalDate cutByDate(LocalDateTime localDateTime) {
			return localDateTime.toLocalDate();
		}

		public int count() {
			return smsList.size();
		}
	}
}
