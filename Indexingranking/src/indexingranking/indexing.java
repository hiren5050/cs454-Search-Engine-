package indexingranking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.tartarus.snowball.ext.porterStemmer;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class indexing {

	public static final String FILES_TO_INDEX_DIRECTORY = "filesToIndex";
	public static final String INDEX_DIRECTORY = "indexDirectory";

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";
	public static Set<String> stopword = new HashSet<String>();
	public static porterStemmer stemmered = new porterStemmer();
	public static String[] StopWordSet = { "a", "as", "able", "about", "above",
			"according", "accordingly", "across", "actually", "after",
			"afterwards", "again", "against", "aint", "all", "allow", "allows",
			"almost", "alone", "along", "already", "also", "although",
			"always", "am", "among", "amongst", "an", "and", "another", "any",
			"anybody", "anyhow", "anyone", "anything", "anyway", "anyways",
			"anywhere", "apart", "appear", "appreciate", "appropriate", "are",
			"arent", "around", "as", "aside", "ask", "asking", "associated",
			"at", "available", "away", "awfully", "be", "became", "because",
			"become", "becomes", "becoming", "been", "before", "beforehand",
			"behind", "being", "believe", "below", "beside", "besides", "best",
			"better", "between", "beyond", "both", "brief", "but", "by",
			"cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause",
			"causes", "certain", "certainly", "changes", "clearly", "co",
			"com", "come", "comes", "concerning", "consequently", "consider",
			"considering", "contain", "containing", "contains",
			"corresponding", "could", "couldnt", "course", "currently",
			"definitely", "described", "despite", "did", "didnt", "different",
			"do", "does", "doesnt", "doing", "dont", "done", "down",
			"downwards", "during", "each", "edu", "eg", "eight", "either",
			"else", "elsewhere", "enough", "entirely", "especially", "et",
			"etc", "even", "ever", "every", "everybody", "everyone",
			"everything", "everywhere", "ex", "exactly", "example", "except",
			"far", "few", "ff", "fifth", "first", "five", "followed",
			"following", "follows", "for", "former", "formerly", "forth",
			"four", "from", "further", "furthermore", "get", "gets", "getting",
			"given", "gives", "go", "goes", "going", "gone", "got", "gotten",
			"greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt",
			"have", "havent", "having", "he", "hes", "hello", "help", "hence",
			"her", "here", "heres", "hereafter", "hereby", "herein",
			"hereupon", "hers", "herself", "hi", "him", "himself", "his",
			"hither", "hopefully", "how", "howbeit", "however", "i", "id",
			"ill", "im", "ive", "ie", "if", "ignored", "immediate", "in",
			"inasmuch", "inc", "indeed", "indicate", "indicated", "indicates",
			"inner", "insofar", "instead", "into", "inward", "is", "isnt",
			"it", "itd", "itll", "its", "its", "itself", "just", "keep",
			"keeps", "kept", "know", "knows", "known", "last", "lately",
			"later", "latter", "latterly", "least", "less", "lest", "let",
			"lets", "like", "liked", "likely", "little", "look", "looking",
			"looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean",
			"meanwhile", "merely", "might", "more", "moreover", "most",
			"mostly", "much", "must", "my", "myself", "name", "namely", "nd",
			"near", "nearly", "necessary", "need", "needs", "neither", "never",
			"nevertheless", "new", "next", "nine", "no", "nobody", "non",
			"none", "noone", "nor", "normally", "not", "nothing", "novel",
			"now", "nowhere", "obviously", "of", "off", "often", "oh", "ok",
			"okay", "old", "on", "once", "one", "ones", "only", "onto", "or",
			"other", "others", "otherwise", "ought", "our", "ours",
			"ourselves", "out", "outside", "over", "overall", "own",
			"particular", "particularly", "per", "perhaps", "placed", "please",
			"plus", "possible", "presumably", "probably", "provides", "que",
			"quite", "qv", "rather", "rd", "re", "really", "reasonably",
			"regarding", "regardless", "regards", "relatively", "respectively",
			"right", "said", "same", "saw", "say", "saying", "says", "second",
			"secondly", "see", "seeing", "seem", "seemed", "seeming", "seems",
			"seen", "self", "selves", "sensible", "sent", "serious",
			"seriously", "seven", "several", "shall", "she", "should",
			"shouldnt", "since", "six", "so", "some", "somebody", "somehow",
			"someone", "something", "sometime", "sometimes", "somewhat",
			"somewhere", "soon", "sorry", "specified", "specify", "specifying",
			"still", "sub", "such", "sup", "sure", "ts", "take", "taken",
			"tell", "tends", "th", "than", "thank", "thanks", "thanx", "that",
			"thats", "thats", "the", "their", "theirs", "them", "themselves",
			"then", "thence", "there", "theres", "thereafter", "thereby",
			"therefore", "therein", "theres", "thereupon", "these", "they",
			"theyd", "theyll", "theyre", "theyve", "think", "third", "this",
			"thorough", "thoroughly", "those", "though", "three", "through",
			"throughout", "thru", "thus", "to", "together", "too", "took",
			"toward", "towards", "tried", "tries", "truly", "try", "trying",
			"twice", "two", "un", "under", "unfortunately", "unless",
			"unlikely", "until", "unto", "up", "upon", "us", "use", "used",
			"useful", "uses", "usually", "value", "various", "very", "via",
			"viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed",
			"well", "were", "weve", "welcome", "well", "went", "were",
			"werent", "what", "whats", "whatever", "when", "whence",
			"whenever", "where", "wheres", "whereafter", "whereas", "whereby",
			"wherein", "whereupon", "wherever", "whether", "which", "while",
			"whither", "who", "whos", "whoever", "whole", "whom", "whose",
			"why", "will", "willing", "wish", "with", "within", "without",
			"wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you",
			"youd", "youll", "youre", "youve", "your", "yours", "yourself",
			"yourselves", "zero" };

	public static void main(String[] args) throws CorruptIndexException,
			LockObtainFailedException, IOException, SAXException, TikaException {

		System.out.println("Indexing...............");

		for (String stopwords : StopWordSet) {
			stopword.add(stopwords);
		}

		// StopAnalyzer.ENGLISH_STOP_WORDS_SET ="";

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35,
				StandardAnalyzer.STOP_WORDS_SET);

		Directory indexDirectory = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
				analyzer);

		if (new File(INDEX_DIRECTORY).exists()) {
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		} else {
			// Add new documents to an existing index:
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		}

		IndexWriter indexWriter = new IndexWriter(indexDirectory, iwc);

		index(new File(FILES_TO_INDEX_DIRECTORY), indexWriter);

		indexWriter.optimize();
		indexWriter.close();

		System.out.println("Finished Indexing...............");

	}

	// Call Index recursively to get file from each folder
	// and sub folders.
	public static void index(File file, IndexWriter writer)
			throws CorruptIndexException, LockObtainFailedException,
			IOException, SAXException, TikaException {
		if (!file.exists())
			return;
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				index(f, writer);
			}
		} else {
			if (!file.getName().equals(".DS_Store")) {
				createIndex(file, writer);
			}
		}
	}

	public static void createIndex(File file, IndexWriter writer)
			throws CorruptIndexException, LockObtainFailedException,
			IOException, SAXException, TikaException {

		Document document = new Document();
		if (!file.isDirectory()
				&& !file.getName().equalsIgnoreCase(".DS_Store")) {

			InputStream fis = new FileInputStream(file);

			int maxStringLength = 10 * 1024 * 1024;
			WriteOutContentHandler handler = new WriteOutContentHandler(
					maxStringLength);
			ContentHandler contenthandler = new BodyContentHandler(handler);
			Metadata metadata = new Metadata();
			Parser parser = new AutoDetectParser();
			parser.parse(fis, contenthandler, metadata, new ParseContext());
			String newString = contenthandler.toString()
					.replaceAll("/[^a-zA-Z 0-9]+/g", " ")
					.replaceAll("\\s+", " ").trim();

			Tokenizer tokenizer = new StandardTokenizer(Version.LUCENE_35,
					new StringReader(newString.toLowerCase()));
			final StandardFilter standardFilter = new StandardFilter(
					Version.LUCENE_35, tokenizer);
			@SuppressWarnings("resource")
			final StopFilter stopFilter = new StopFilter(Version.LUCENE_35,
					standardFilter, stopword);
			final CharTermAttribute charTermAttribute = tokenizer
					.addAttribute(CharTermAttribute.class);
			stopFilter.reset();
			StringBuilder sb = new StringBuilder();

			while (stopFilter.incrementToken()) {
				final String token = charTermAttribute.toString().toString();
				stemmered.setCurrent(token);
				stemmered.stem();
				sb.append(stemmered.getCurrent()).append(
						System.getProperty("line.separator"));
			}

			String path = file.getCanonicalPath();
			Field field = new Field(FIELD_PATH, path, Field.Store.YES,
					Field.Index.ANALYZED);

			field.setIndexOptions(IndexOptions.DOCS_ONLY);
			document.add(field);

			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_CONTENTS, reader, Field.TermVector.YES));
			System.out.println(file.getName());
			writer.addDocument(document);
		}

	}

	// public void createIndex(File f) throws CorruptIndexException,
	// LockObtainFailedException, IOException {
	//
	// Directory dir = FSDirectory.open(INDEX_DIRECTORY);
	// Analyzer analyzer = new StandardAnalyzer(
	// StandardAnalyzer.STOP_WORDS_SET); // using stop words
	// IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_2,
	// analyzer);
	//
	// if (INDEX_DIRECTORY.exists()) {
	// iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
	// } else {
	// // Add new documents to an existing index:
	// iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
	// }
	//
	// IndexWriter writer = new IndexWriter(dir, iwc);
	//
	// // for (File f : FILES_TO_INDEX_DIRECTORY.listFiles()) {
	// Document doc = new Document();
	// FieldType fieldType = new FieldType();
	// fieldType.setIndexed(true);
	// fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
	// fieldType.setStored(true);
	// fieldType.setStoreTermVectors(true);
	// fieldType.setTokenized(true);
	// if (!f.isDirectory() && !f.getName().equalsIgnoreCase(".DS_Store")) {
	// Field contentField = new Field(FIELD_CONTENTS, getAllText(f),
	// fieldType);
	// doc.add(contentField);
	// writer.addDocument(doc);
	// }
	// // }
	//
	// writer.close();
	// }

	// /**
	//
	// */
	// public String getAllText(File f) throws FileNotFoundException,
	// IOException {
	// String textFileContent = "";
	//
	// for (String line : Files.readAllLines(Paths.get(f.getAbsolutePath()))) {
	// textFileContent += line;
	// }
	// return textFileContent;
	// }

}
