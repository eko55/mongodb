package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.model.Movie;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBConnectorPojoExample {

    public static void main(String[] args) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        // Replace the uri string with your MongoDB deployment's connection string
        String user = System.getenv("MONGODB_USER");
        String password = System.getenv("MONGODB_PASS");
        String uri =String.format("mongodb+srv://%s:%sa@myatlasclusteredu.fyvbwbb.mongodb.net/?retryWrites=true&w=majority&appName=myAtlasClusterEDU",user, password);

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            //getDatabase и getCollection конфигурират mongoClient-a, не извикват базата
            //опита за конекция и аутентикацията се случват при опит за операция върху база или колекция
            MongoDatabase database = mongoClient.getDatabase("sample_mflix").withCodecRegistry(pojoCodecRegistry);
            MongoCollection<Movie> collection = database.getCollection("movies", Movie.class);
            Movie movie = collection.find(Filters.eq("title", "Back to the Future")).first();
            System.out.println(movie);
        }
    }
}
