package indexingranking;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchText {

	public static final String INDEX_DIRECTORY = "indexDirectory";

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		searchIndex("cosine");

	}

	public static void searchIndex(String searchtext) throws IOException,
			ParseException {
		Directory directory = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(indexReader);

		Analyzer stdAn = new StandardAnalyzer(Version.LUCENE_35);
		QueryParser parser = new QueryParser(Version.LUCENE_35, "contents",
				stdAn);
		Query q = parser.parse(searchtext);

		TopDocs hits = searcher.search(q, 4);
		ScoreDoc[] scoreDocs = hits.scoreDocs;

		System.out.println("hits = " + scoreDocs.length);
		System.out.println("Hits (score,  docId)");

		for (int n = 0; n < scoreDocs.length; n++) {
			ScoreDoc sd = scoreDocs[n];
			float score = sd.score;
			int docId = sd.doc;

			System.out.printf("%4.2f  %d\n", score, docId);

			Document document = indexReader.document(docId);
			String docname = document.getField("path").stringValue();
			System.out.println(docname);
		}

		indexReader.close();
		searcher.close();

	}

}
