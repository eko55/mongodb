package org.example;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class MongoDbApp {

//    public static void main(final String[] args) {
//        Logger root = (Logger) LoggerFactory.getLogger("org.mongodb.driver");
//        // Available levels are: OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL
//        root.setLevel(Level.WARN);
//
//        String connectionString = System.getenv("MONGODB_URI");
//        try (MongoClient client = MongoClients.create(connectionString)) {
//            Document sampleDocument = new Document("_id", new ObjectId())
//                    .append("account_id", "MDB255054629")
//                    .append("account_holder", "Mai Kalange")
//                    .append("account_type", "savings")
//                    .append("balance", 2340)
//                    .append("last_updated", new Date());
//            //CRUD
//            MongoDBCrud crud = new MongoDBCrud(client);
//            //INSERT ONE
//            crud.insertOneDocument(sampleDocument);
//        }
//    }

    //<collection>.updateOne(<filter>,<update>);
    private static void updateOneExample(MongoCollection<Document> collection) {
        Bson filter = Filters.eq("account_id", "A51");
        Bson updates = Updates.combine(Updates.set("account_status", "active"), Updates.inc("balance", 100));

        UpdateResult updateResult = collection.updateOne(filter, updates);
        System.out.println(updateResult); //prints response like AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}
    }

    //<collection>.updateMany(<filter>,<update>);
    private static void updateManyExample(MongoCollection<Document> collection) {
        Bson filter = Filters.eq("account_type", "checking");
        Bson updates = Updates.combine(Updates.set("minimum_balance", 100));

        UpdateResult updateResult = collection.updateMany(filter, updates);
        System.out.println(updateResult); //prints response like AcknowledgedUpdateResult{matchedCount=4, modifiedCount=4, upsertedId=null}
    }

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClientSingleton.getInstance();

        try {
            MongoDatabase database = mongoClient.getDatabase("sample_supplies");
            MongoCollection<Document> accounts = database.getCollection("sales");

            Bson query = Filters.eq("_id", new ObjectId("5bd761dcae323e45a93ccfe8"));
            FindIterable<Document> documents = accounts.find(query);
            documents.forEach(doc -> System.out.println(doc.toJson()));

            //insertOne
//            Document someDocument = new Document("_id", new ObjectId())
//                    .append("business_id", "1234-2015-ENFO")
//                    .append("date", Date.from(LocalDate.of(2015, 2, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
//                    .append("address", new Document().append("city", "Sofia").append("zip", 1000).append("street", "Random Street"));
//
//            InsertOneResult insertOneResult = accounts.insertOne(someDocument);
//            BsonValue insertedId = insertOneResult.getInsertedId();
//            System.out.println(insertedId);
//
//            //insertMany
//            Document accountOne = new Document()
//                    .append("account_id", "A51")
//                    .append("account_holder", "John Doe")
//                    .append("account_type", "checking")
//                    .append("balance", 1785);
//
//            Document accountTwo = new Document()
//                    .append("account_id", "J17")
//                    .append("account_holder", "John Jon")
//                    .append("account_type", "checking")
//                    .append("balance", 1785);
//
//            InsertManyResult insertManyResult = accounts.insertMany(List.of(accountOne, accountTwo));
//            insertManyResult.getInsertedIds().forEach((index, id) -> System.out.println(index + " : " + id.asObjectId()));

            MongoDBCrud crud = new MongoDBCrud(mongoClient);
//            crud.find();
//            crud.findOne();

//            updateOneExample(accounts);
            updateManyExample(accounts);
        } catch (MongoException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }

//    public static void main(String[] args) {
////        ConnectionString connectionString = new ConnectionString("mongodb+srv://myAtlasDBUser:Nnoe82WmDiiGm0jB@myatlasclusteredu.fyvbwbb.mongodb.net/?appName=myAtlasClusterEDU");
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:admin@myatlasclusteredu.fyvbwbb.mongodb.net/?appName=myAtlasClusterEDU");
//
//        MongoClientSettings clientSettings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .serverApi(ServerApi.builder()
//                        .version(ServerApiVersion.V1)
//                        .build())
//                .build();
//
//        // Create a new client and connect to the server
//        try (MongoClient mongoClient = MongoClients.create(clientSettings)) {
//            try {
//                // Send a ping to confirm a successful connection
//                MongoDatabase database = mongoClient.getDatabase("admin");
//                database.runCommand(new Document("ping", 1));
//                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
//            } catch (MongoException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
