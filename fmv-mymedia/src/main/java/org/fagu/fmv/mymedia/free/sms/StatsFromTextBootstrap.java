package org.fagu.fmv.mymedia.free.sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
 * @created 29 juil. 2021 10:51:53
 */
public class StatsFromTextBootstrap {

	public static void main(String... args) throws Exception {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		List<SMS> smsList = new ArrayList<>();

		LocalDateTime minDateTime = null;
		LocalDateTime maxDateTime = null;

		try (BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])))) {
			String line = null;

			String phoneNumber = null;
			LocalDateTime localDateTime = null;
			while((line = reader.readLine()) != null) {
				if("SMS".equals(line) || "1".equals(line)) {
					continue;
				}
				if(line.contains("€")) {
					smsList.add(new SMS(phoneNumber, localDateTime));
				} else if(line.endsWith("***")) {
					phoneNumber = line;
				} else if(line.contains("/")) {
					localDateTime = LocalDateTime.parse(line, formatter);
					if(minDateTime == null) {
						minDateTime = maxDateTime = localDateTime;
					} else {
						minDateTime = Stream.of(minDateTime, localDateTime)
								.collect(Collectors.minBy(Comparator.comparing(Function.identity())))
								.get();
						maxDateTime = Stream.of(maxDateTime, localDateTime)
								.collect(Collectors.maxBy(Comparator.comparing(Function.identity())))
								.get();
					}
				}
			}
		}

		System.out.println(smsList.size() + " SMS");
		System.out.println(minDateTime + " -> " + maxDateTime);
		System.out.println();
		favoritePhoneNumber(smsList);
		System.out.println();
		favoriteTime(smsList);
		System.out.println();
		byDate(smsList);
		System.out.println();
		firstHourByDate(smsList);
	}

	private static void favoritePhoneNumber(List<SMS> smsList) {
		Map<String, SMSByNumber> byNumberSMS = new TreeMap<>();
		smsList.forEach(sms -> byNumberSMS.computeIfAbsent(sms.phoneNumber, SMSByNumber::new).smsList.add(sms));
		SortedSet<SMSByNumber> byNumberSet = new TreeSet<>(Comparator.comparingInt(SMSByNumber::count).reversed());
		byNumberSMS.values().forEach(byNumberSet::add);
		byNumberSet.stream()
				.limit(20)
				.forEach(smsbn -> System.out.println(smsbn.phoneNumber + "\t" + smsbn.count()));
		System.out.println();
	}

	private static void favoriteTime(List<SMS> smsList) {
		Map<LocalTime, SMSByHourQuarter> byTimeSMS = new TreeMap<>();
		smsList.forEach(sms -> byTimeSMS.computeIfAbsent(SMSByHourQuarter.cutByQuarter(sms.localDateTime), SMSByHourQuarter::new).smsList.add(sms));
		byTimeSMS.forEach((t, s) -> System.out.println(t + "\t" + s.smsList.size()));
	}

	private static void byDate(List<SMS> smsList) {
		Map<LocalDate, SMSByDate> byDateSMS = new TreeMap<>();
		smsList.forEach(sms -> byDateSMS.computeIfAbsent(SMSByDate.cutByDate(sms.localDateTime), SMSByDate::new).smsList.add(sms));
		byDateSMS.forEach((t, s) -> System.out.println(t + "\t" + s.smsList.size()));
	}

	private static void firstHourByDate(List<SMS> smsList) {
		Map<LocalDate, SMS> byDateSMS = new TreeMap<>();
		smsList.forEach(sms -> byDateSMS.merge(
				SMSByDate.cutByDate(sms.localDateTime),
				sms,
				(old, nw) -> old.localDateTime.isBefore(nw.localDateTime) ? old : nw));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		byDateSMS.forEach((t, s) -> System.out.println(t + "\t" + formatter.format(s.localDateTime.toLocalTime())));
	}

	// -------------------------------------------------------

	public static final class SMS {

		private final String phoneNumber;

		private final LocalDateTime localDateTime;

		private SMS(String phoneNumber, LocalDateTime localDateTime) {
			this.phoneNumber = Objects.requireNonNull(phoneNumber);
			this.localDateTime = Objects.requireNonNull(localDateTime);
		}

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
