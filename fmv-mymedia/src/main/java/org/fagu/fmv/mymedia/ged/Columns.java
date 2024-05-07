package org.fagu.fmv.mymedia.ged;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import org.folg.gedcom.model.Person;


/**
 * @author f.agu
 * @created 7 mai 2024 11:08:41
 */
public class Columns {

	private Columns() {}

	// =========================================

	public static class Id implements Column {

		@Override
		public String getTitle() {
			return "ID";
		}

		@Override
		public String getValue(Person person) {
			return person.getId();
		}

	}

	// =========================================

	public static class Sex implements Column {

		@Override
		public String getTitle() {
			return "Sexe";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getSex(person).name();
		}

	}

	// =========================================

	public static class Lastname implements Column {

		@Override
		public String getTitle() {
			return "Nom";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getLastname(person);
		}

	}

	// =========================================

	public static class Firstnames implements Column {

		@Override
		public String getTitle() {
			return "Prénoms";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getFirstnames(person);
		}

	}

	// =========================================

	public static class BirthDate implements Column {

		@Override
		public String getTitle() {
			return "Date de naissance";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getBirthDate(person).map(t -> formatDate(t)).orElse("");
		}

	}

	// =========================================

	public static class BirthPlace implements Column {

		@Override
		public String getTitle() {
			return "Lieu de naissance";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getBirthPlace(person).orElse("");
		}

	}

	// =========================================

	public static class DeathDate implements Column {

		@Override
		public String getTitle() {
			return "Date de décès";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getDeathDate(person).map(t -> formatDate(t)).orElse("");
		}

	}

	// =========================================

	public static class DeathPlace implements Column {

		@Override
		public String getTitle() {
			return "Lieu de décès";
		}

		@Override
		public String getValue(Person person) {
			return PersonUtils.getDeathPlace(person).orElse("");
		}

	}

	// *************************************************

	private static String formatDate(Temporal temporal) {
		if(temporal instanceof LocalDate t) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			return t.format(formatter);
		}
		if(temporal instanceof Year t) {
			return t.toString();
		}
		return "?";
	}

}
