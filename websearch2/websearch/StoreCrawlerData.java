package indexingranking;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class StoreCrawlerData {

	MongoClient mongo = null;
	DB db = null;
	DBCollection collection = null;

	public void insertlink(String rawhtml, String metatags, String url,
			String filepath) {
		BasicDBObject document = new BasicDBObject();

		// save the document.
		document.put("document", rawhtml);

		// if there is metadata save it.
		// if (metatags != null && metatags != "")
		document.put("metadata", metatags);

		document.put("url", url);
		document.put("filepath", filepath);
		document.put("createdDate", new Date());

		collection.insert(document);

	}

	public StoreCrawlerData() {
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("crawler");
			collection = db.getCollection("crawleddata");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

}
