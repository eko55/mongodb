package org.example;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class MongoDbConnector {
    public static void main( String[] args ) {

        String user = System.getenv("MONGODB_USER");
        String password = System.getenv("MONGODB_PASS");
        String uri =String.format("mongodb+srv://%s:%s@myatlasclusteredu.fyvbwbb.mongodb.net/?retryWrites=true&w=majority&appName=myAtlasClusterEDU",user, password);

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));

//            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
//            MongoCollection<Document> collection = database.getCollection("movies");
//
//            Document doc = collection.find(eq("title", "Back to the Future")).first();
//            if (doc != null) {
//                System.out.println(doc.toJson());
//            } else {
//                System.out.println("No matching documents found.");
//            }
        }
    }
}
