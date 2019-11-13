package org.fagu.fmv.media;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author Oodrive
 * @author f.agu
 * @created 13 nov. 2019 12:23:40
 */
public class JsonReader {

	private JsonReader() {}

	public static List<NavigableMap<String, Object>> parse(JSONArray jsonArray) {
		List<NavigableMap<String, Object>> list = new LinkedList<>();
		Iterator<?> iterator = jsonArray.iterator();
		while(iterator.hasNext()) {
			list.add(parse((JSONObject)iterator.next()));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static NavigableMap<String, Object> parse(JSONObject jsonObject) {
		JSONArray names = jsonObject.names();
		Iterator<?> iterator = names.iterator();
		TreeMap<String, Object> map = new TreeMap<>();
		while(iterator.hasNext()) {
			String name = (String)iterator.next();
			Object value = jsonObject.get(name);
			if(value instanceof JSONObject) {
				value = parse((JSONObject)value);
			} else if(value instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray)value;
				value = jsonArray.stream()
						.map(o -> {
							if(o instanceof String) {
								return o;
							}
							if(o instanceof JSONObject) {
								return parse((JSONObject)o);
							}
							return o; // TODO
						})
						.collect(Collectors.toList());
			} else if(value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character) {
				value = String.valueOf(value);
			} else {
				// TODO
				// System.out.println(value);
			}
			map.put(name, value);
			map.put(name.toLowerCase(), value);
		}
		return map;
	}
}
