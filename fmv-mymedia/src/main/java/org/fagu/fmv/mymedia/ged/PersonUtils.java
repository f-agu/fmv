package org.fagu.fmv.mymedia.ged;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.folg.gedcom.model.EventFact;
import org.folg.gedcom.model.Name;
import org.folg.gedcom.model.Person;


/**
 * @author f.agu
 * @created 6 mai 2024 22:53:40
 */
public class PersonUtils {

	private static final Pattern DATE_Y = Pattern.compile("\\d{4}");

	private static final Pattern DATE_D_M_Y = Pattern.compile("\\d+ [A-Z]+ \\d+");

	private PersonUtils() {}

	static String getLastname(Person person) {
		return person.getNames()
				.stream()
				.map(Name::getDisplayValue)
				.findFirst()
				.map(s -> StringUtils.substringBetween(s, "/").trim())
				.orElseThrow();
	}

	static String getFirstnames(Person person) {
		return person.getNames()
				.stream()
				.map(Name::getDisplayValue)
				.findFirst()
				.map(s -> StringUtils.substringBefore(s, "/").trim())
				.orElseThrow();
	}

	static Sex getSex(Person person) {
		return person.getEventsFacts()
				.stream()
				.filter(ef -> "sex".equalsIgnoreCase(ef.getTag()))
				.map(EventFact::getValue)
				.map(Sex::valueOf)
				.findFirst()
				.orElseThrow();
	}

	static Optional<Temporal> getDate(String tag, Person person) {
		return person.getEventsFacts()
				.stream()
				.filter(ef -> tag.equalsIgnoreCase(ef.getTag()))
				.map(EventFact::getDate)
				.map(PersonUtils::parseDate)
				.filter(Objects::nonNull)
				.findFirst();
	}

	static Optional<Temporal> getBirthDate(Person person) {
		return getDate("birt", person);
	}

	static Optional<Temporal> getDeathDate(Person person) {
		return getDate("deat", person);
	}

	static Optional<String> getPlace(String tag, Person person) {
		return person.getEventsFacts()
				.stream()
				.filter(ef -> tag.equalsIgnoreCase(ef.getTag()))
				.map(EventFact::getPlace)
				.filter(Objects::nonNull)
				.findFirst();
	}

	static Optional<String> getBirthPlace(Person person) {
		return getPlace("birt", person);
	}

	static Optional<String> getDeathPlace(Person person) {
		return getPlace("deat", person);
	}

	static Temporal parseDate(String s) {
		if(s == null) {
			return null;
		}
		String d = s.startsWith("ABT ") ? s.substring(4) : s;
		d = d.startsWith("EST ") ? d.substring(4) : d;
		d = d.startsWith("BEF ") ? d.substring(4) : d;
		if(DATE_D_M_Y.matcher(d).matches()) {
			try {
				int day = Integer.parseInt(StringUtils.substringBefore(d, " "));
				int year = Integer.parseInt(StringUtils.substringAfterLast(d, " "));
				String monthLetters = StringUtils.substringBetween(d, " ").toLowerCase();
				int month = Arrays.stream(Month.values())
						.filter(m -> m.name().toLowerCase().startsWith(monthLetters))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Month undefined for " + s))
						.ordinal() + 1;
				return LocalDate.of(year, month, day);
			} catch(NumberFormatException e) {
				throw new IllegalArgumentException("Failed to parse " + s);
			}
		} else if(DATE_Y.matcher(d).matches()) {
			return Year.parse(d);
		}
		throw new IllegalArgumentException("Failed to parse " + s);
	}

}
