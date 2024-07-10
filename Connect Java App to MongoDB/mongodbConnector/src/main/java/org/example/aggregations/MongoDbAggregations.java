package org.example.aggregations;

import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.MongoClientSingleton;
import org.example.MongoDBCrud;

import java.util.Arrays;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;

public class MongoDbAggregations {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClientSingleton.getInstance();

        try {
            MongoDatabase database = mongoClient.getDatabase("sample_airbnb");
            MongoCollection<Document> listingsAndReviews = database.getCollection("listingsAndReviews");

            Bson matchStage = Aggregates.match(Filters.eq("property_type", "House"));
            Bson groupStage = Aggregates.group("$property_type",sum("total_balance","$balance"),avg("average_balance","$balance"));
            Bson sortStage = Aggregates.sort(Sorts.orderBy(Sorts.descending("review_scores.review_scores_rating")));
            Bson projectStage = Aggregates.project(Projections.fields(Projections.include("property_type","review_scores.review_scores_rating")));
            Bson projectStage2 = Aggregates.project(Projections.fields(Projections.include("property_type","review_scores.review_scores_rating"),
                    Projections.computed("newField",new Document("$divide",Arrays.asList("$balance", 1.20F))),
                    Projections.excludeId()));

            AggregateIterable<Document> aggregate = listingsAndReviews.aggregate(Arrays.asList(matchStage,sortStage,projectStage));

            System.out.println("Display aggregation results");
            aggregate.forEach(a -> System.out.println(a.toJson()));

//db.listingsAndReviews.aggregate([{$match: {property_type: "House"}},{$project: {property_type: 1, review_scores: 1}},{$sort: {"review_scores.review_scores_rating": -1}},{$limit: 5}])```

        } catch (MongoException e) {
            e.printStackTrace();
        } finally {
            mongoClient.close();
        }
    }
}
