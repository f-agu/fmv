package org.fagu.fmv.mymedia.reduce.neocut;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.fagu.fmv.utils.time.Time;


/**
 * @author Utilisateur
 * @created 4 avr. 2018 14:47:54
 */
public class Template {

	private final Map<Time, File> modelMap;

	private Template(Map<Time, File> modelMap) {
		this.modelMap = modelMap;
	}

	public static Template load(File propertiesFile) throws IOException {
		Properties properties = new Properties();
		try (InputStream inputStream = new FileInputStream(propertiesFile)) {
			properties.load(inputStream);
		}

		Map<Time, File> modelMap = new HashMap<>();

		for(Entry<Object, Object> entry : properties.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			if(key.startsWith("cut.")) {

			} else if(key.startsWith("model.")) {
				Time time = Time.parse(key.substring(6));
			}
		}

	}

	private static findModel(String fileName) {
		File file = new 
	}

}
