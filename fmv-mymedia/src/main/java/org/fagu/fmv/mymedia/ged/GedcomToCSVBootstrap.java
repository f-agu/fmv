package org.fagu.fmv.mymedia.ged;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.folg.gedcom.model.Gedcom;
import org.folg.gedcom.model.Person;
import org.folg.gedcom.parser.ModelParser;
import org.xml.sax.SAXException;


/**
 * @author f.agu
 * @created 7 mai 2024 11:07:45
 */
public class GedcomToCSVBootstrap {

	private static final List<Column> COLUMNS = List.of(
			new Columns.Id(),
			new Columns.Sex(),
			new Columns.Lastname(),
			new Columns.Firstnames(),
			new Columns.BirthDate(),
			new Columns.BirthPlace(),
			new Columns.DeathDate(),
			new Columns.DeathPlace());

	private static final String COLUMN_SEPARATOR = "\t";

	public static void main(String... args) throws Exception {
		if(args.length != 1) {
			System.out.println("Usage: java -cp . org.fagu.fmv.mymedia.ged.GedcomToCSVBootstrap <ged or zip file>");
			return;
		}
		File file = new File(args[0]);
		if( ! file.exists()) {
			System.out.println("File not found: " + file);
			return;
		}
		if( ! file.isFile()) {
			System.out.println("It is not a file: " + file);
			return;
		}
		String extension = FilenameUtils.getExtension(file.getName());
		if("ged".equalsIgnoreCase(extension)) {
			exportGed(file);
		} else if("zip".equalsIgnoreCase(extension)) {
			exportZip(file);
		} else {
			System.out.println("Ignore: " + file);
		}
	}

	private static void exportGed(File file) throws IOException, SAXException {
		try (InputStream inputStream = new FileInputStream(file)) {
			export(inputStream);
		}
	}

	private static void exportZip(File file) throws IOException, SAXException {
		try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry zipEntry = null;
			while((zipEntry = inputStream.getNextEntry()) != null) {
				String extension = FilenameUtils.getExtension(zipEntry.getName());
				if("ged".equalsIgnoreCase(extension)) {
					export(inputStream);
					break;
				}
			}
		}
	}

	private static void export(InputStream inputStream) throws IOException, SAXException {
		ModelParser modelParser = new ModelParser();
		Gedcom gedcom = modelParser.parseGedcom(inputStream);

		System.out.println(COLUMNS.stream()
				.map(Column::getTitle)
				.collect(Collectors.joining(COLUMN_SEPARATOR)));
		List<Person> people = gedcom.getPeople();
		for(Person person : people) {
			System.out.println(COLUMNS.stream()
					.map(c -> c.getValue(person))
					.collect(Collectors.joining(COLUMN_SEPARATOR)));
		}
	}

}
