package org.example;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class MongoDBCrud {
    private final MongoCollection<Document> collection;

    public MongoDBCrud(MongoClient client) {
        this.collection = client.getDatabase("bank").getCollection("accounts");
    }

    public void insertOneDocument(Document doc) {
        System.out.println("Inserting one account document");
        InsertOneResult result = collection.insertOne(doc);
        BsonValue id = result.getInsertedId();
        System.out.println("Inserted document Id: " + id);
    }

    //Uses find()
    public void find(Bson query) {
        FindIterable<Document> documents = collection.find(query);
        documents.forEach(doc -> System.out.println(doc.toJson()));
    }

    public void find() {
        Bson query = Filters.and(Filters.gte("balance", 1000), Filters.eq("account_type", "checking"));
        FindIterable<Document> documents = collection.find(query);
        documents.forEach(doc -> System.out.println(doc.toJson()));
    }

    //Uses find().first()
    public Document findOne(Bson query) {
        return collection.find(query).first();
    }

    public void findOne() {
        Bson query = Filters.and(Filters.gte("balance", 1000), Filters.eq("account_type", "checking"));
        Document document = collection.find(query).first();
        System.out.println(document.toJson());
    }

    public void updateOne(Bson query, Bson updates){
        UpdateResult updateResult = collection.updateOne(query, updates); //finds the first document that matches the filter and applies the updates modifications
        System.out.println(updateResult);
    }
}
