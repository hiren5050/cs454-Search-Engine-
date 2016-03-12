package indexingranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class GetTFIDF {

	public static final String INDEX_DIRECTORY = "indexDirectory";
	public static List<String[]> alltermsFromDocs = new ArrayList<String[]>();
	public static double tf = 0;
	public static double idf = 0;
	public static List<DocColletion> lstColletion = new ArrayList<DocColletion>();
	public static Map<String, Map<String, Integer>> termFrequency = new HashMap<>();
	public static Map<String, Double> itermFrequency = new HashMap<>();
	public static Map<String, Map<String, Double>> termFIDF = new HashMap<>();

	Map<String, Double> tfidt;

	public static int totalDocuments = 0;

	public static void main(String[] args) throws IOException, ParseException,
			JSONException {

		System.out.println("Starting.....");

		Directory directory = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexReader indexReader = IndexReader.open(directory);
		// System.out.println(indexReader.numDocs()) ;
		long totalDocs = totalDocuments = indexReader.numDocs();
		for (int i = 0; i < indexReader.numDocs(); i++) {

			TermFreqVector tfv = indexReader.getTermFreqVector(i, "contents");

			if (tfv != null) {
				String[] terms = tfv.getTerms();
				int[] frq = tfv.getTermFrequencies();

				alltermsFromDocs.add(terms);

				@SuppressWarnings("deprecation")
				String[] docname = indexReader.document(i).getField("path")
						.stringValue().split("articles");
				String name = "";
				if (docname.length > 1)
					name = docname[1];
				else
					name = docname[0];

				// Get the term frequency.
				for (int j = 0; j < terms.length; j++) {
					if (termFrequency.containsKey(terms[j].toLowerCase()))
						termFrequency.get(terms[j].toLowerCase()).put(name,
								frq[j]);
					else {
						Map<String, Integer> docCount = new HashMap<String, Integer>();
						docCount.put(name, frq[j]);
						termFrequency.put(terms[j].toLowerCase(), docCount);
					}
				}
			}
		}

		// Get the inverse term frequency.
		for (Map.Entry<String, Map<String, Integer>> entry : termFrequency
				.entrySet()) {

			itermFrequency.put(entry.getKey(),
					Math.log10(totalDocs / entry.getValue().size()));
			
		}

		
		// Get tf-idf
		for (Map.Entry<String, Map<String, Integer>> entry : termFrequency
				.entrySet()) {
			Double idf = itermFrequency.get(entry.getKey());
			Map<String, Integer> tf = entry.getValue();
			Map<String, Double> tfidf = new HashMap<String, Double>();
			for (String term : tf.keySet()) {
				int freq = tf.get(term);

				tfidf.put(term, freq * idf);
				termFIDF.put(entry.getKey(), tfidf);
			}

		}

		Database db = new Database();
		DBCollection collection = db.createDatabase();
		collection.drop();
		for (Entry<String, Map<String, Double>> entry : termFIDF.entrySet()) {
			JSONObject jsonObject = new JSONObject();
			JSONObject json = new JSONObject();
			for (Entry<String, Double> entry1 : entry.getValue().entrySet()) {
				json.put(entry1.getKey(), entry1.getValue());
			}
			jsonObject.put(entry.getKey(), json);

			BasicDBObject document = new BasicDBObject();
			document.put("termData", jsonObject.toString());
			collection.insert(document);

		}

		System.out.println("Finished....");

		GetTFIDF objGetTFIDF = new GetTFIDF();

		objGetTFIDF.tfidt = objGetTFIDF.searchText("java");

		System.out.println("Found in");

		objGetTFIDF.pageRank();

	}

	public Map<String, Double> searchText(String text) {
		return termFIDF.get(text);
	}

	// GET IDF..
	public static double idfCalculator(List<String[]> allTerms,
			String termToCheck) {
		double count = 0;
		for (String[] ss : allTerms) {
			for (String s : ss) {
				if (s.equalsIgnoreCase(termToCheck)) {
					count++;
					break;
				}
			}
		}
		return 1 + Math.log(allTerms.size() / count);
	}

	public Map<String, Double> pageRank() throws IOException, JSONException {

		Map<String, ArrayList<String>> outGoingLinks = null;
		Map<String, ArrayList<String>> inComingLinks = null;

		LinkAnalysis objLinkAnalysis = new LinkAnalysis();
		objLinkAnalysis.getOutGoingLinks();
		objLinkAnalysis.getIncomingLinks();

		outGoingLinks = objLinkAnalysis.outGoingLinks;
		inComingLinks = objLinkAnalysis.inComingLinks;


		Map<String, Double> map = new HashMap<String, Double>();
		Map<String, Double> finalMap = new HashMap<String, Double>();

		Double pageRank = 0.0;
		

		// Set default ranks
		for (Map.Entry<String, ArrayList<String>> entry : outGoingLinks
				.entrySet()) {
			map.put(entry.getKey(), (double) (1 / totalDocuments));
		}

		for (int i = 0; i < 10; i++) {

			for (Map.Entry<String, ArrayList<String>> entry : outGoingLinks
					.entrySet()) {

				// get incoming.
				ArrayList<String> incoming = inComingLinks.get(entry.getKey());
				if (incoming != null && !incoming.isEmpty()) {
					for (String in : incoming) {
						Double r = map.get(in) / outGoingLinks.get(in).size();
						map.replace(in, r);
						pageRank = pageRank + r;
					}

					pageRank = 0.85 * (pageRank) + 0.15;
					finalMap.put(entry.getKey(), pageRank);
				}

			}

		}

		Database db = new Database();
		DBCollection collection = db.createDatabaseForLink();
		collection.drop();
		BasicDBObject document = new BasicDBObject();
		JSONObject obj = new JSONObject();
		for (String output : finalMap.keySet()) {
			obj.put(output, finalMap.get(output));
		}
		document.put("linkAnalysis", obj.toString());
		collection.insert(document);

		System.out.println();
		return finalMap;

	}

}
