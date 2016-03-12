package indexingranking;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class Database {
	
	MongoClient mongo = null;
	DB db = null;
	DBCollection collection = null;

	public Database() {
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("crawler");
			collection = db.getCollection("crawleddata");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public DBCollection getStoredDocumet() {

		DBCollection collection = db.getCollection("crawleddata");

		return collection;
	}
	
	public DBCollection createDatabase()
	{
		
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("Indexing");
			collection = db.getCollection("TF-IDF");
			
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		return collection;
		
	}
	
	public DBCollection createDatabaseForLink()
	{
		
		try {
			mongo = new MongoClient("localhost", 27017);
			db = mongo.getDB("Link");
			collection = db.getCollection("LinkAnalysis");
			
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		return collection;
		
	}

}
