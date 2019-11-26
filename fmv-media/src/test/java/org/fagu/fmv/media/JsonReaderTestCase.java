package org.fagu.fmv.media;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * @author Oodrive
 * @author f.agu
 * @created 25 nov. 2019 17:36:17
 */
public class JsonReaderTestCase {

	@Test
	public void test1() throws IOException {
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("ffprobe1.json"), "UTF-8")) {
			JsonElement rootElement = JsonParser.parseReader(reader);
			if(rootElement.isJsonObject()) {
				JsonObject asJsonObject = rootElement.getAsJsonObject();
				JsonArray streams = asJsonObject.getAsJsonArray("streams");
				streams.forEach(js -> {
					if(js.isJsonObject()) {
						System.out.println(js.getAsJsonObject().get("index").getAsString());
					}
					// System.out.println(js);
				});
			}
			// Gson gson = new Gson();
			// @SuppressWarnings("unchecked")
			// Map<String, Object> map = gson.fromJson(reader, Map.class);
			// System.out.println(map);
			// @SuppressWarnings("unchecked")
			// List<Object> streams = (List)map.get("streams");
			// streams.forEach( o-> {
			// if(o instanceof Map)
			// });

		}
	}

}
