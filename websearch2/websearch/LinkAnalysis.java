/**
 * 
 */
package indexingranking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Chavda
 *
 */
public class LinkAnalysis {

	public static final String INDEX_DIRECTORY = "indexDirectory";
	public String filesToIndex = "filesToIndex";
	public String fullPath = "/Users/Chavda/CS454/indexingranking/filesToIndex/";
	public Map<String, ArrayList<String>> outGoingLinks = new HashMap<String, ArrayList<String>>();
	public Map<String, ArrayList<String>> inComingLinks = new HashMap<String, ArrayList<String>>();

	public static void main(String[] args) throws IOException {

		LinkAnalysis objLinkAnalysis = new LinkAnalysis();

		objLinkAnalysis.getOutGoingLinks();
		objLinkAnalysis.getIncomingLinks();
		objLinkAnalysis.print();

	}

	public void getOutGoingLinks() throws IOException {

		Directory directory = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexReader indexReader = IndexReader.open(directory);

		for (int i = 0; i < indexReader.numDocs(); i++) {

			Document document = indexReader.document(i);
			String docname = fullPath
					+ document.getFieldable("path").stringValue();
			String name = document.getFieldable("path").stringValue();

			org.jsoup.nodes.Document doc = Jsoup.parse(new File(docname),
					"UTF-8");

			// System.out.println(doc.baseUri());

			String[] modified = doc.baseUri().split("articles");
			String mk = name;

			// if (modified.length > 1) {
			// mk = modified[1];

			Elements linksOnPage = doc.select("a[href]");
			ArrayList<String> list = new ArrayList<String>();

			for (Element link : linksOnPage) {

				String l = link.attr("href");

				// System.out.println(l);

				if (!l.isEmpty()) {
					list.add(l);
					// String[] m = l.split("articles");
					// if (m.length > 1) {
					// list.add(m[1]);
					// }
				}
			}

			// System.out.println(list.size());

			outGoingLinks.put(mk, list);

			// } else {
			// continue;
			//
			// }

		}

		// for (Map.Entry<String, ArrayList<String>> entry : outGoingLinks
		// .entrySet()) {
		//
		// System.out.println(entry.getKey());
		// for (String iterable_element : entry.getValue()) {
		// System.out.println(iterable_element);
		// }
		//
		// }

		// System.out.println(outGoingLinks.size());
	}

	public void getIncomingLinks() {

		for (Map.Entry<String, ArrayList<String>> entry : outGoingLinks
				.entrySet()) {
			ArrayList<String> strIncoming = new ArrayList<String>();
			for (Map.Entry<String, ArrayList<String>> entry1 : outGoingLinks
					.entrySet()) {

				if (!entry.getKey().equals(entry1.getKey())) {

					if (entry1.getValue().contains(entry.getKey())) {
						strIncoming.add(entry1.getKey());
					}
				}
			}
			inComingLinks.put(entry.getKey(), strIncoming);

		}
	}

	public void print() {
		System.out.println("...........>>>>>>>>>>>>>>>>>>>>>>");
		for (Map.Entry<String, ArrayList<String>> entry : inComingLinks
				.entrySet()) {

			//System.out.println(entry.getKey());
			for (String iterable_element : entry.getValue()) {
				System.out.println();
				System.out.println(iterable_element);
				System.out.println();
			}

		}
	}

}
