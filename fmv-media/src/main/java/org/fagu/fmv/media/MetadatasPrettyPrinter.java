package org.fagu.fmv.media;

import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author Oodrive
 * @author f.agu
 * @created 21 juin 2019 11:09:42
 */
public class MetadatasPrettyPrinter {

	private final PrintStream printStream;

	private final String indentation = "    ";

	public MetadatasPrettyPrinter() {
		this(null);
	}

	public MetadatasPrettyPrinter(PrintStream printStream) {
		this.printStream = printStream != null ? printStream : System.out;
	}

	public void print(Metadatas metadatas) {
		print(metadatas.getData(), "");
	}

	// **********************************************************

	private void print(Map<String, Object> map, String indent) {
		for(Entry<String, Object> entry : map.entrySet()) {
			if(entry.getValue() instanceof Map) {
				printStream.println(indent + entry.getKey());
				Map<String, Object> childMap = (Map<String, Object>)entry.getValue();
				print(childMap, indent + indentation);
			} else {
				printStream.println(indent + entry.getKey() + " : " + entry.getValue());
			}
		}
	}

}
