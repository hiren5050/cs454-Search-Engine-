package springmvc.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class SearchText {

	Map<String, Double> mapRank = new HashMap<String, Double>();
	JSONArray jArray = new JSONArray();

	@SuppressWarnings({ "deprecation", "resource", "unchecked" })
	public SearchText() throws ParseException {
		MongoClient mongo = null;
		JSONArray jsona = new JSONArray();
		mongo = new MongoClient("localhost", 27017);
		DB db  = mongo.getDB("Indexing");
		DBCollection collectionterm = db.getCollection("TF-IDF");
		DBCursor cursorterm = collectionterm.find();
		while (cursorterm.hasNext()) {

			JSONParser parser = new JSONParser();
			JSONObject objJson = (JSONObject) parser.parse(cursorterm.next()
					.get("termData").toString());
			jsona.add(objJson);
		}

		jArray = jsona;

	}

	@SuppressWarnings({ "deprecation", "resource", "unchecked" })
	public Map<String, Double> searchtext(String term) throws ParseException {
		MongoClient mongo = null;
		DB db = null;
		DBCollection collection = null;
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("Indexing");
			collection = db.getCollection("TF-IDF");
		} catch (MongoException e) {
			e.printStackTrace();
		}

		collection = db.getCollection("TF-IDF");
		DBCursor cursor = collection.find();

		// mongo = new MongoClient("localhost", 27017);
		db = mongo.getDB("Link");
		DBCollection collectionLink = db.getCollection("LinkAnalysis");

		JSONObject jsonTerm = null;
		JSONObject jsonLinks = null;
		while (cursor.hasNext()) {

			jsonTerm = new JSONObject();
			JSONParser parser = new JSONParser();
			JSONObject objJson = (JSONObject) parser.parse(cursor.next()
					.get("termData").toString());

			if ((JSONObject) objJson.get(term) != null) {
				jsonTerm = (JSONObject) objJson.get(term);
				break;
			}

		}

		// System.out.println(jsonTerm);

		DBCursor cursorlinke = collectionLink.find();
		while (cursorlinke.hasNext()) {
			JSONParser parser = new JSONParser();
			JSONObject objJson = (JSONObject) parser.parse(cursorlinke.next()
					.get("linkAnalysis").toString());
			jsonLinks = (JSONObject) objJson;
		}

		for (Iterator iterator = jsonTerm.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			for (Iterator iterator1 = jsonLinks.keySet().iterator(); iterator1
					.hasNext();) {

				String key1 = (String) iterator1.next();
				if (key.equals(key1)) {

					Double rank = Double.parseDouble(jsonTerm.get(key)
							.toString())
							+ Double.parseDouble(jsonLinks.get(key1).toString());
					String link = key;

					mapRank.put(link, rank);

				}

			}

		}

		//
		// System.out.println(jsona.toJSONString());

		return sortByComparator(mapRank);

	}

	private Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static void printMap(Map<String, Double> map) {
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.println("[Key] : " + entry.getKey() + " [Value] : "
					+ entry.getValue());
		}
	}

}
