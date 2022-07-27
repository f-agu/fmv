package org.fagu.fmv.mymedia.free.sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * @author Oodrive
 * @author f.agu
 * @created 29 juil. 2021 10:51:53
 */
public class StatsFromTextBootstrap {

	private static final Pattern IS_DIGITS = Pattern.compile("\\d+");

	public static void main(String... args) throws Exception {
		if(args.length == 0) {
			System.out.println("Usage: java -cp . " + StatsFromPDFBootstrap.class.getName() + " <file.txt>");
			return;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

		List<SMS> smsList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])))) {
			String line = null;

			String phoneNumber = null;
			LocalDateTime localDateTime = null;
			while((line = reader.readLine()) != null) {
				if("SMS".equals(line) || IS_DIGITS.matcher(line).matches()) {
					continue;
				}
				if(line.contains("€") || line.contains("�")) {
					smsList.add(new SMS(phoneNumber, localDateTime));
				} else if(line.endsWith("***")) {
					phoneNumber = line;
				} else if(line.contains("/")) {
					localDateTime = LocalDateTime.parse(line, formatter);
				}
			}
		}

		Stats.all(smsList);
	}

}
