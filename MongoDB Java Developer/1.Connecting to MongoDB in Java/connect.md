### Connecting to an Atlas Cluster in Java Application

```
package com.mongodb.quickstart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Connection {

    public static void main(String[] args) {
        String connectionString = System.getProperty("mongodb.uri");
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            List<Document> databases = mongoClient.listDatabases().into(new ArrayList<>());
            databases.forEach(db -> System.out.println(db.toJson()));
        }
    }
}
```

Compile you project:
```
mvn --quiet compile
```

Run your project:
```
mvn --quiet exec:java -Dexec.mainClass=com.mdbu.app.Connection
```

Compile and execute you maven project:
```
mvn compile exec:java -Dexec.mainClass="com.mongodb.quickstart.Connection" -Dmongodb.uri="<connectionString>"mvn compile exec:java -Dexec.mainClass="com.mongodb.quickstart.Connection" -Dmongodb.uri="<connectionString>"
```
Connect to MongoDB Atlas cluster from Java application.


application requires drivers(set of libraries) to connect and interact with Mongodb.


Java Driver, the official MongoDB driver for synchronous Java applications

If your Java application requires asynchronous stream processing, use the Reactive Streams Driver which uses Reactive Streams to make non-blocking calls to MongoDB.

mongodb maintains java drivers for synchronous and asynchronous application code

-usage examples
-fundamental concepts
-reference documentation
-api documentation

Steps to connect our Java application to Mongodb:
1.You will need JDK8 or later
2.Create mvn project
3.Add mongodB java driver dependency:

<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>5.1.1</version>
    </dependency>
</dependencies>

4.Create a MongoDB cluster in Atlas,where we can make use of some samle datasets.

5.Connect to the cluster
We need the connection string,which contains information on the hostname or IP address and port of your cluster, authentication mechanism, user credentials when applicable, and other connection options

you ran a query on a sample collection to retrieve data in the map-like class Document

Next: Use POJO to map data from mongodb
1.Create MongoDBConnectorPojoExample class

POJOs are often used for data encapsulation, which is the practice of separating business logic from data representation.

Основната идея на pojo-тата е да държат данни и да са прости,като съдържат само конструктори,гетъри и сетъри.Не трябва да имплементират интерфейси,за да останат free of dependencies.

Използваме ключовата дума transient за да отбележим ,че не искаме дадено поле да бъде сериазлизирано.Изпозлваме за пароли и друго подобни полета със sensitive информация. При десериализация transient поле получава дефолтна стойност: 0 за инт,null за обект

When an object is serialized, only the non-static, non-transient fields of that specific object are written to the output stream.

In Java, static fields are not serialized as part of the object's state when you serialize an instance of a class. This is because static fields belong to the class itself rather than any individual instance of the class. 

You should have only one MongoClient instance per Atlas cluster for your application. 