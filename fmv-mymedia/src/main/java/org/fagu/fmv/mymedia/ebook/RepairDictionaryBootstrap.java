package org.fagu.fmv.mymedia.ebook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.fagu.fmv.utils.io.UnclosedInputStream;
import org.fagu.fmv.utils.io.UnclosedOutputStream;


/**
 * @author Utilisateur
 * @created 28 janv. 2024 13:42:01
 */
public class RepairDictionaryBootstrap {

	public static void main(String... args) throws IOException {

		String p = "D:\\A graver\\eBooks\\Développement personnel\\Jacques Martel\\a.epub";

		repair(new File(p));
	}

	private static void repair(File file) throws IOException {
		File outFile = new File(file.getParentFile(), "a.repair.epub");
		try (ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(file));
				ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outFile), StandardCharsets.UTF_8)) {
			ZipArchiveEntry zipEntry = null;
			while((zipEntry = zipArchiveInputStream.getNextZipEntry()) != null) {
				String name = zipEntry.getName();
				System.out.println(name);
				ZipEntry newZipEntry = new ZipEntry(name);
				newZipEntry.setTime(zipEntry.getTime());
				zipOutputStream.putNextEntry(newZipEntry);
				if(name.startsWith("index_split_") && name.endsWith(".html")) {
					repair(new UnclosedInputStream(zipArchiveInputStream), new UnclosedOutputStream(zipOutputStream));
				} else {
					IOUtils.copyLarge(zipArchiveInputStream, zipOutputStream);
				}
				zipOutputStream.closeEntry();
			}
		}
	}

	// private static void repair(Path path) throws IOException {
	// Path outPath = path.getParent().resolve(FilenameUtils.getBaseName(path.getFileName().toString()) +
	// "-repaired.html");
	//
	// }

	private static void repair(InputStream inputStream, OutputStream outputStream) throws IOException {

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
			String line = null;
			int count = 0;
			List<String> bloc = new ArrayList<>();
			AtomicBoolean inBloc = new AtomicBoolean();

			Flusher flusher = w -> {
				if( ! bloc.isEmpty()) {
					bloc.add(" </p>");
					writer.write(bloc.stream().collect(Collectors.joining(" ")));
					bloc.clear();
					inBloc.set(false);
					writer.flush();
				}
			};
			while((line = bufferedReader.readLine()) != null) {
				++count;
				if(line.startsWith("<p class=\"calibre1\">")) {
					String after = line.substring(20);
					if(after.endsWith("</p>")) {
						String text = after.substring(0, after.length() - 4);
						String unspec = removeHtmlTag(text);

						if(unspec.length() > 75 && Character.isAlphabetic(text.charAt(0))) {
							if( ! inBloc.get()) {
								bloc.add("<p class=\"calibre1\">");
							}
							bloc.add(text);
							inBloc.set(true);

							// System.out.println(count + " / " + unspec.length() + " : " + unspec);
							continue;
						}
						if(inBloc.get()) {
							flusher.flush(writer);
						}

						// if(text.startsWith("<b class=\"calibre3\">") && text.endsWith(" </b>")) {
						// if("<b class=\"calibre3\"> </b>".equals(text)) {
						//
						// }
						// }

						// if(text.startsWith("<b class=\"calibre3\">") && text.endsWith(" </b>")
						// && StringUtils.isAllUpperCase(StringUtils.substringBetween(text, "<b class=\"calibre3\">",
						// " </b>"))) {
						// if(inBloc.get()) {
						// flusher.flush(writer);
						// }
						// bloc.add(line);
						// inBloc.set(true);
						// continue;
						// }
						// if(count == 281) {
						// System.out.println(284);
						// }
						// if(text.startsWith("<a id=\"p")) {
						// flusher.flush(writer);
						// bloc.add(line);
						// continue;
						// }
						//
						// System.out.println(text);
						// if(inBloc.get()) {
						// bloc.add(line);
						// continue;
						// }
					}

				}
				writer.write(line);
				writer.write('\n');
			}
		}
	}

	private interface Flusher {

		void flush(Writer writer) throws IOException;
	}

	private static String removeHtmlTag(String s) {
		StringBuilder sb = new StringBuilder();
		boolean inTag = false;
		for(char c : s.toCharArray()) {
			if(c == '<') {
				inTag = true;
				continue;
			}
			if(c == '>') {
				inTag = false;
				continue;
			}
			if( ! inTag) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
