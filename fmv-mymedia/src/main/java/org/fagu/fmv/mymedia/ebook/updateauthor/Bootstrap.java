package org.fagu.fmv.mymedia.ebook.updateauthor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.fagu.fmv.mymedia.ebook.EBooksFile;


/**
 * @author fagu
 */
public class Bootstrap {

	private static void updateForfolder(File folder) {

	}

	private static void updateAuthor(File folder) {
		File[] files = folder.listFiles(f -> f.isFile() && "epub".equalsIgnoreCase(FilenameUtils.getExtension(f.getName())));
		if(files == null) {
			return;
		}

		Map<String, String> metadataMap = new HashMap<>();
		metadataMap.put("creator", folder.getName());
		metadataMap.put("publisher", "nobody");
		metadataMap.put("contributor", "");

		System.out.println("Update author with " + folder.getName());
		System.out.println();

		for(File file : files) {
			System.out.println(file.getName() + "...");
			try {
				EBooksFile eBooksFile = EBooksFile.open(file);
				File newFile = eBooksFile.writeMetadatas(metadataMap);
				file.delete();
				newFile.renameTo(file);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage: java " + Bootstrap.class.getName() + " <folder>");
			return;
		}

		File folder = new File(args[0]);

	}

}
