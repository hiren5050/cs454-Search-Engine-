package indexingranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DBCursor;

public class Ranking {

	static Map<String, ArrayList<String>> outgoingLinks = new HashMap<String, ArrayList<String>>();
	static Map<String, ArrayList<String>> incomingoingLinks = new HashMap<String, ArrayList<String>>();

	static ArrayList<String> outlinksinlinks = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		Ranking objRanking = new Ranking();

		objRanking.setOutGoingLinks();
		objRanking.getIncomingLinks();

	}

	public void setOutGoingLinks() throws IOException {

		Database db = new Database();
		DBCursor cursor = db.getStoredDocumet().find();

		while (cursor.hasNext()) {

			JsonObject jsonObject = new JsonParser().parse(
					cursor.next().toString()).getAsJsonObject();
			getFile(jsonObject.get("filepath").getAsString(),
					jsonObject.get("url").getAsString());

		}

	}

	public void getFile(String fileName, String url) throws IOException {

		File file = new File(fileName);

		if (!outgoingLinks.containsKey(url)) {

			Document doc = Jsoup.parse(file, "UTF-8");
			Elements linksOnPage = doc.select("a");
			for (Element link : linksOnPage) {
				String links = link.attr("abs:href");
				if (!links.isEmpty()) {
					if (!outlinksinlinks.contains(links))
						outlinksinlinks.add(links);
				}
			}
			outgoingLinks.put(url, outlinksinlinks);
		}
	}

	public void getIncomingLinks() {
		for (Map.Entry<String, ArrayList<String>> entry : outgoingLinks
				.entrySet()) {
			ArrayList<String> strIncoming = new ArrayList<String>();
			for (Map.Entry<String, ArrayList<String>> entry1 : outgoingLinks
					.entrySet()) {

				if (!entry.getKey().equals(entry1.getKey())) {

					if (entry1.getValue().contains(entry.getKey())) {
						strIncoming.add(entry1.getKey());
					}
				}
			}
			incomingoingLinks.put(entry.getKey(), strIncoming);
		}

	}
}
