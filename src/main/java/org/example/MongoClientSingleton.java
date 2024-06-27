package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientSingleton {

    private static MongoClient client;

    // private constructor to avoid client applications using the constructor
    private MongoClientSingleton(){}

    public static synchronized MongoClient getInstance() {
        if (client == null) {
//            ConnectionString connectionString = new ConnectionString("mongodb+srv://myAtlasDBUser:<password>@myatlasclusteredu.fyvbwbb.mongodb.net/?retryWrites=true&w=majority&appName=myAtlasClusterEDU");
            ConnectionString connectionString = new ConnectionString("mongodb+srv://admin:admin@myatlasclusteredu.fyvbwbb.mongodb.net/?retryWrites=true&w=majority&appName=myAtlasClusterEDU");

            MongoClientSettings clientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();

            // Create a new client and connect to the server
            client = MongoClients.create(clientSettings);
        }

        return client;
    }
}
