package springmvc.web.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class SearchText {

	Map<String, Double> mapRank = new HashMap<String, Double>();

	Map<String, Double> finalPageRank = new HashMap<String, Double>();
	JSONArray jArray = new JSONArray();
	MongoClient mongolink = null;
	DB dblink = null;
	DBCollection collectionLink;

	public SearchText() {
		mongolink = new MongoClient("localhost", 27017);
		dblink = mongolink.getDB("Link");
		collectionLink = dblink.getCollection("LinkAnalysis");
	}

	@SuppressWarnings({ "deprecation", "resource", "unchecked" })
	public ArrayList<Term> searchtext(String term) throws ParseException {
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

		String orand = "";
		// Map<String, Double> boolmap = null;
		Map<String, Double> termMap = null;
		// Map<String, Double> bool2map = null;

		DecimalFormat decimalFormat = new DecimalFormat("##.#####");

		if (term.toLowerCase().contains("or")) {
			orand = "or";
			String[] arr = term.split(" ");
			if (arr[1].equals("or")) {
				if (arr[0] != null) {
					// BasicDBObject query = new BasicDBObject("term", arr[0]);
					// collection = ;

					BasicDBObject andQuery = new BasicDBObject();
					List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
					obj.add(new BasicDBObject("term", arr[0]));
					obj.add(new BasicDBObject("term", arr[2]));
					andQuery.put("$or", obj);

					collection = db.getCollection("TF-IDF");

					System.out.println(andQuery.toString());

					DBCursor cursor = collection.find(andQuery);
					BasicDBList listdb = null;
					// Map<String, Double> termMap = null;
					termMap = new HashMap<String, Double>();
					while (cursor.hasNext()) {

						listdb = (BasicDBList) cursor.next().get("data");
						if (listdb != null) {
							BasicDBObject[] listobject = listdb
									.toArray(new BasicDBObject[0]);

							for (BasicDBObject objdb : listobject) {

								termMap.put(objdb.get("path").toString(),
										Double.parseDouble(objdb.get("tfidf")
												.toString()));

							}
						}
					}
					cursor.close();
				}
			}
		}

		if (term.toLowerCase().contains("and")) {
			orand = "and";
			String[] arr = term.split(" ");
			Map<String, Double> map1 = new HashMap<String, Double>();
			Map<String, Double> map2 = new HashMap<String, Double>();
			if (arr[1].equals("and")) {

				if (arr[0] != null) {
					BasicDBObject query = new BasicDBObject("term", arr[0]);
					collection = db.getCollection("TF-IDF");
					DBCursor cursor = collection.find(query);
					BasicDBList listdb = null;
					while (cursor.hasNext()) {

						listdb = (BasicDBList) cursor.next().get("data");
					}
					cursor.close();

					if (listdb != null) {
						BasicDBObject[] listobject = listdb
								.toArray(new BasicDBObject[0]);
						map1 = new HashMap<String, Double>();
						for (BasicDBObject objdb : listobject) {

							map1.put(objdb.get("path").toString(), Double
									.parseDouble(objdb.get("tfidf").toString()));

						}
					}

				}

				if (arr[2] != null) {
					BasicDBObject query = new BasicDBObject("term", arr[2]);
					collection = db.getCollection("TF-IDF");
					DBCursor cursor = collection.find(query);
					BasicDBList listdb = null;
					while (cursor.hasNext()) {
						listdb = (BasicDBList) cursor.next().get("data");
					}
					cursor.close();

					if (listdb != null) {
						BasicDBObject[] listobject = listdb
								.toArray(new BasicDBObject[0]);
						map2 = new HashMap<String, Double>();
						for (BasicDBObject objdb : listobject) {

							map2.put(objdb.get("path").toString(), Double
									.parseDouble(objdb.get("tfidf").toString()));

						}
					}

				}
				termMap = new HashMap<String, Double>();
				for (Map.Entry<String, Double> entry : map1.entrySet()) {
					// for (Map.Entry<String, Double> entry1 : map2.entrySet())
					// {
					if (map2.containsKey(entry.getKey())) {
						termMap.put(entry.getKey(), entry.getValue());
					}
					// }
					// }
				}
			}

		}

		BasicDBObject query = new BasicDBObject("term", term);
		collection = db.getCollection("TF-IDF");
		DBCursor cursor = collection.find(query);
		BasicDBList listdb = null;

		Map<String, Double> linkScore = new HashMap<String, Double>();
		ArrayList<Term> listTerm = new ArrayList<Term>();

		if (orand.isEmpty()) {

			while (cursor.hasNext()) {

				listdb = (BasicDBList) cursor.next().get("data");
			}
			cursor.close();

			if (listdb != null) {
				BasicDBObject[] listobject = listdb
						.toArray(new BasicDBObject[0]);
				termMap = new HashMap<String, Double>();
				for (BasicDBObject objdb : listobject) {

					termMap.put(objdb.get("path").toString(),
							Double.parseDouble(objdb.get("tfidf").toString()));

				}
			}
		}

		Double maxtfidf = 0.0;

		if (termMap != null) {
			if (termMap.size() > 0)
				maxtfidf = Collections.max(termMap.values());
			for (Map.Entry<String, Double> entry : termMap.entrySet()) {

				listTerm.add(new Term(entry.getKey(), entry.getValue(), 0.0,
						0.0));

			}
		}

		for (Term term2 : listTerm) {
			JSONObject jsonObj = getrank(term2.getUrl());
			term2.setScore(Double.parseDouble(jsonObj.get("score").toString()));
		}

		if (termMap != null) {
			for (String s : termMap.keySet()) {
				JSONObject jsonObj = getrank(s);
				linkScore.put(jsonObj.get("link").toString(),
						Double.parseDouble(jsonObj.get("score").toString()));
			}

		}

		Double maxlinkscore = 0.0;

		if (linkScore != null) {
			if (linkScore.size() > 0)
				maxlinkscore = Collections.max(linkScore.values());

		}

		for (Term term2 : listTerm) {

			Double normalTfidf = Double.parseDouble(decimalFormat
					.format((double) (term2.getTfidf() / maxtfidf) * 0.1));
			Double normalLinkScore = Double.parseDouble(decimalFormat
					.format((double) (term2.getScore() / maxlinkscore) * 0.9));

			term2.setTfidf(normalTfidf);
			term2.setScore(normalLinkScore);
			term2.setRank(Double.parseDouble(decimalFormat.format(normalTfidf
					+ normalLinkScore)));

		}

		Collections.sort(listTerm, new Comparator<Term>() {
			public int compare(Term o1, Term o2) {
				return (o2.getRank()).compareTo(o1.getRank());
			}
		});

		// ArrayList<Term> lstNewTerm = new ArrayList<Term>();
		//
		// int i = 0;
		// for (Term term2 : listTerm) {
		//
		// if (i < 5)
		// lstNewTerm.add(term2);
		// else
		// break;
		//
		// i++;
		// }

		return listTerm;

	}

	@SuppressWarnings({ "resource", "unchecked" })
	private JSONObject getrank(String url) {

		BasicDBObject query = new BasicDBObject("link", url);
		DBCursor cursor = collectionLink.find(query);
		BasicDBObject db1 = new BasicDBObject();
		JSONObject json = null;
		while (cursor.hasNext()) {
			db1 = (BasicDBObject) cursor.next();
		}
		cursor.close();
		if (db1 != null) {
			json = new JSONObject();
			json.put("link", db1.get("link").toString());
			json.put("score", Double.parseDouble(db1.get("score").toString()));
		}
		return json;
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
		int i = 0;
		double valdivide = 0;
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it
				.hasNext();) {

			Map.Entry<String, Double> entry = it.next();

			valdivide = entry.getValue();
			sortedMap.put(entry.getKey(), entry.getValue());
			i++;
		}
		return sortedMap;
	}

	public static void printMap(Map<String, Double> map) {
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.println("[Key] : " + entry.getKey() + " [Value] : "
					+ entry.getValue());
		}
	}

	public double normalize(double value) {

		double newvalue = 0;
		if (value > 1) {
			newvalue = (1 - 0) / ((1 - 0) * (value - 1) + 1);
		}
		return newvalue;
	}

}
