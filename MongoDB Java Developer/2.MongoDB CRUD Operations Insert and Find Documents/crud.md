MongoDB CRUD Operations in Java

LESSON 1: WORKING WITH MONGODB DOCUMENTS IN JAVA

1.Define BSON
-more secure than json,which is vulnerable to JSON injection attacks
2.How BSON documents are expressed in Java
3.Instantiate and build a sample doc with the JAva driver
Document class implements the bson interface and is used to represent BSON documents.

Document someDocument = new Document("_id", new ObjectId())
.append("business_id","1234-2015-ENFO")
.append("date", Date.from(LocalDate.of(2015,2,20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
.append("address",new Document().append("city","Sofia").append("zip",1000).append("street","Random Street"));

mvn --quiet compile
mvn --quiet exec:java -Dexec.mainClass=com.mdbu.app.DemoApp
### LESSON 2: INSERTING A DOCUMENT IN JAVA APPLICATIONS

```db.<collection>.insertOne(<document>)``` - create/inserts a single doc in mongodb collection

```db.<collection>.insertMany()``` - performs bulk write operation for storing multiple docs in mongodb

To create a new document, first we instantiate a new object of type Document that will represent the BSON document.
The fields and values are then set by the append() method of the Document class.

```
Document inspection = new Document("_id", new ObjectId())
        .append("id", "10021-2015-ENFO")
        .append("certificate_number", 9278806)
        .append("business_name", "ATLIXCO DELI GROCERY INC.")
        .append("date", Date.from(LocalDate.of(2015, 2, 20).atStartOfDay(ZoneId.systemDefault()).toInstant()))
        .append("result", "No Violation Issued")
        .append("sector", "Cigarette Retail Dealer - 127")
        .append("address", new Document().append("city", "RIDGEWOOD").append("zip", 11385).append("street", "MENAHAN ST").append("number", 1712));
```
The _id field uniquely identifies each document in the collection, similar to the primary key in a 
relational database.

To insert the document we call the insertOne() method of the MongoCollection instance:
```
InsertOneResult result = collection.insertOne(inspection);
BsonValue id = result.getInsertedId();
System.out.println(id);
```


### LESSON 3: QUERYING A MONGODB COLLECTION IN JAVA APPLICATIONS

```db.<collection>.find(<query filter>) ``` - returns all documents matching the filter

```db.<collection>.find(<query filter>).first()``` - returns the first document matching the filter

query conditions are known as predicates and are expressed as filters by using the Filters builder class

To avoid returning all docs from the collection specify a filter.

### LESSON 4: UPDATING DOCUMENTS IN JAVA APPLICATIONS

```db.<collection>.updateOne(<query filter>, <update>, {options})``` – updates the first document matching the filter

```db.<collection>.updateMany(<query filter>, <update>, {options})``` – updates all documents matching the criteria

<query filter> specifies the matching criteria
<update> statement specifies how to change the matching documents

You can update values of existing fields and define new fields and values.

```
MongoDatabase database = mongoClient.getDatabase("bank");
MongoCollection<Document> collection = database.getCollection("accounts");
Bson query  = Filters.eq("account_id","MDB12234728");
Bson updates  = Updates.combine(Updates.set("account_status","active"),Updates.inc("balance",100));
UpdateResult upResult = collection.updateOne(query, updates);

Bson query  = Filters.eq("account_type","savings");
Bson updates  = Updates.combine(Updates.set("minimum_balance",100));
UpdateResult upResult = collection.updateMany(query, updates);
```

Updates builder class can be used to combine multiple update operators.

To update array fields use:
Update.push(), Update.pull(), Update.popLast()

How to edit el at specific index in arr?

### LESSON 5: DELETING DOCUMENTS IN JAVA APPLICATIONS

```db.<collection>.deleteOne(<filter>)```

```db.<collection>.deleteMany(<filter>)```

deleteMany() with no filter passed will delete all documents in the collection
