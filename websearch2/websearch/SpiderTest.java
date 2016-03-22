package indexingranking;

import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.json.JSONException;
import org.xml.sax.SAXException;

public class SpiderTest {
	static String depth = "";
	static String url = "";
	static String extract = "";

	static Spider spider = new Spider();

	public static void main(String[] args) throws SAXException, TikaException,
			IOException, JSONException {

		SpiderLeg leg = new SpiderLeg();
		// leg.removeAllFiles();

		System.out.println(args.length);
		depth = args[1];
		url = args[3];
		if (args.length == 5)
			extract = args[4];

		// for (int i = 0; i <= depth.length(); i++) {
		spider.search(depth, url, extract);
		// }

		indexing index = new indexing();
		index.startIndexing();

		TFIDFLA tfidf = new TFIDFLA();
		tfidf.calculateTFIDF();

	}

}