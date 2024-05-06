package org.fagu.fmv.mymedia.ged;

import java.io.File;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.StringJoiner;

import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ModelParser;


public class GedcomToCSVBootstrap {

	public static void main(String... args) throws Exception {
		File gedcomIn = new File("d:\\tmp\\base.ged");
		ModelParser modelParser = new ModelParser();
		Gedcom gedcom = modelParser.parseGedcom(gedcomIn);

		StringJoiner joiner = new StringJoiner("\t");
		joiner.add("ID").add("Sexe")
				.add("Nom")
				.add("Prénoms")
				.add("Date de naissance")
				.add("Lieu de naissance")
				.add("Date de décès")
				.add("Lieu de décès");
		System.out.println(joiner);
		List<Person> people = gedcom.getPeople();
		for(Person person : people) {
			joiner = new StringJoiner("\t");
			joiner
					.add(person.getId())
					.add(PersonUtils.getSex(person).name())
					.add(PersonUtils.getLastname(person))
					.add(PersonUtils.getFirstnames(person))
					.add(PersonUtils.getBirthDate(person).map(t -> formatDate(t)).orElse(""))
					.add(PersonUtils.getBirthPlace(person).orElse(""))
					.add(PersonUtils.getDeathDate(person).map(t -> formatDate(t)).orElse(""))
					.add(PersonUtils.getDeathPlace(person).orElse(""));
			// .add(person.);
			System.out.println(joiner);
		}
	}

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
