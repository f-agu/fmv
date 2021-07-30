package org.fagu.fmv.mymedia.free.sms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Oodrive
 * @author f.agu
 * @created 30 juil. 2021 09:22:59
 */
public class StatsFromPDFBootstrap {

	public static void main(String... args) throws Exception {
		if(args.length == 0) {
			System.out.println("Usage: java -cp . " + StatsFromPDFBootstrap.class.getName() + " <file.txt>");
			return;
		}
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		List<SMS> smsList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])))) {
			String line = null;
			while((line = reader.readLine()) != null) {
				if(line.endsWith(" 0.00")) {
					String[] strs = line.split(" ");
					LocalDate localDate = LocalDate.parse(strs[0], dateFormatter);
					LocalTime localTime = LocalTime.parse(strs[1], timeFormatter);
					LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
					String phoneNumber = strs[3];
					smsList.add(new SMS(phoneNumber, localDateTime));
				}
			}
		}

		Stats.all(smsList);
	}

}
