package org.fagu.fmv.mymedia.ebook.organize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class Bootstrap {

	public static void more(File source, File dest) throws Exception {
		FilenameFilter filenameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".epub");
			}
		};

		String author = null;
		String title = null;
		for(File file : source.listFiles(filenameFilter)) {
			author = null;
			title = null;
			System.out.println(file.getName());
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			ZipEntry zipEntry = null;
			while((zipEntry = zipInputStream.getNextEntry()) != null) {
				if(zipEntry.getName().endsWith(".opf")) {
					SAXReader reader = new SAXReader();
					Document document = reader.read(zipInputStream);
					Element rootElement = document.getRootElement();
					Element metadatElement = rootElement.element("metadata");
					Element authorElement = metadatElement.element("creator");
					Element titleElement = metadatElement.element("title");
					author = authorElement.getText();
					title = titleElement.getText();
					break;
				} else {
					IOUtils.copyLarge(zipInputStream, NullOutputStream.NULL_OUTPUT_STREAM);
				}
			}
			zipInputStream.close();
			if(author != null && title != null) {
				String authorShort = formatAuthor(author, false);
				if(authorShort.length() > 50) {
					authorShort = authorShort.substring(0, 49) + "...";
				}
				String authorFull = formatAuthor(author, true);
				String newFileName = authorFull + " - " + title + ".epub";
				if("".equals(authorShort)) {
					authorShort = "Inconnu";
				}
				newFileName = newFileName.replace(':', ',');
				newFileName = newFileName.replace('?', ' ');
				newFileName = newFileName.replace('/', '-');
				newFileName = newFileName.replaceAll("\"", "");

				File destFile = new File(dest, Character.toString(Character.toUpperCase(authorShort.charAt(0))));
				destFile = new File(destFile, authorShort);
				destFile.mkdirs();
				destFile = new File(destFile, newFileName);
				// System.out.println(file.getName() + " => " +
				// destFile.getPath());
				if(destFile.exists()) {
					file.delete();
					continue;
				}
				if( ! destFile.exists() && ! file.renameTo(destFile)) {
					System.out.println(">>>>>>>>>>> " + newFileName);
				}
			} else {
				System.out.println("############################################");
			}
		}
	}

	public static String formatAuthor(String author, boolean full) {
		String sh = author;
		int pos = sh.indexOf('(');
		String in = null;
		if(pos > 0 && full) {
			sh = sh.substring(0, pos).trim();
			in = author.substring(pos + 1, author.indexOf(')')).trim();
			boolean feat = in.startsWith("feat");
			if(feat) {
				in = in.substring(in.indexOf(' '));
			}
			in = "(feat " + formatAuthor(in, true) + ")";
		}
		pos = sh.indexOf(',');
		if(pos > 0) {
			String[] ss = sh.split(",");
			if(ss.length == 1) {
				sh = ss[0].trim();
			} else {
				sh = ss[1].trim() + " " + ss[0].trim();
			}
		}
		return sh + (in != null ? " " + in : "");
	}

	public static void simple(File source, File dest) {
		for(File file : source.listFiles()) {
			String name = file.getName();
			String author = name.substring(0, name.indexOf('-')).trim();
			File authorFolder = new File(dest, author);
			if( ! authorFolder.exists()) {
				authorFolder.mkdirs();
			}
			File destFile = new File(authorFolder, name);
			file.renameTo(destFile);
		}
	}

	public static void main(String[] args) throws Exception {
		// simple(new File("C:\\Nos Documents\\Downloads\\92 Ebooks en Epub"),
		// new File("C:\\A graver\\eBooks"));
		more(new File("C:\\A graver\\eBooks\\_TODO"), new File("C:\\A graver\\eBooks2"));
	}
}
