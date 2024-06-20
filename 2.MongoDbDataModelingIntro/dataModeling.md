### <span style="color:darkgoldenrod"> Какво е data modeling в MongoDB?
Data modeling е процесът по определяне структурата на данните в базата и взаимоотношенията между 
отделните entity-та. Структурата на данните се определя от schema, която задава ограничения върху типовете на
отделните полета и техните допустими стойности. За разлика от релационните бази, схемата в Монго е динамична, 
различните документи в една колекция могат да имат различна структура.По подразбиране, в колекция могат да бъдат 
добавяни документи с всякаква структура.

Пример: Flexible дата модела в Монго е полезен за онлайн магазин например, където различните продукти имат 
различни характеристики.

Пример за дефинирането на schema validation rules при създаването на колекция(полето age е optional):

    db.createCollection("users", {
        validator: {
            $jsonSchema: {
                bsonType: "object",
                required: ["username", "email", "createdAt"],
                properties: {
                    username: {
                        bsonType: "string",
                        minLength: 3,
                        description: "must be a string and at least 3 characters long"
                    },
                    email: {
                        bsonType: "string",
                        pattern: "^.+@.+$",
                        description: "must be a string and match the regular expression pattern for an email"
                    },
                    age: {
                        bsonType: "int",
                        minimum: 18,
                        description: "must be an integer and at least 18"
                    },
                    createdAt: {
                        bsonType: "date",
                        description: "must be a date"
                    }
                }
            }
        }
    });

Ако при insert документът не спазва ограниченията наложени в схемата операцията ще fail-не.

### <span style="color:darkgoldenrod"> Как се изразяват връзките между данните в MongoDB?
Типове връзки между данните:

1.one to one - one entity from one set is connected to exactly one entity from another set
2.one to many - едно entity от даден set е свързано с много entity-та от друг set
3.many to many

В релационните бази можем да свържем резултатите от две таблици имащи one-to-one relationship използвайки JOIN.
В MongoDB можем да имаме one-to-one relationship в рамките на един документ:

    {
        "_id": ObjectId("00000001"),
        "name": "Marnie Dupree",
        "grade": "Freshman",
        "studentId": 123456,
        "email": "mdupree@college.edu"
    }
Student документа има one-to-one връзка със studentId

В MongoDB можем да имаме one-to-many relationship в рамките на един документ(embedding, с nested array - great way to model one-to-many relationships):
Предимството е,че само с едно query можем да извлечем необходимата информация.

?.Основен принцип в монгодб?
main principle "Data that is accessed together should be stored together" (embedded in the same document) ,so not to search through multiple collections to answer the query

### <span style="color:darkgoldenrod"> Какви са начините за моделиране на връзките между данните в MongoDB?
2 начина за моделиране на връзките между данните в Монго:

- embedding (nest objects) - Stores related data in a single doc.Used when you have one-to-many or many-to-many relationships in the data. Embedding helps to simplify queries and improve query performance.
For example if you embed mailingAddress,secondaryAddress and emergencyAddress in single object you will be able to get 
them with single call. Also if you want to update them you can do it with single write operation.
Max BSON doc size is 16mb.
Large docs and unbounded docs are schema antipatterns.

    Example of embedding: 
    //the actor documents are embedded in the movie document
    {
        "_id": ObjectId("sd534gf67j"),
        "title": "Star Wars",
        "cast": [
            {"actor": "...", "character":".."},
            {"actor": "...", "character":".."}
        ]
    }
- embedding е препоръчителен когато:
    - когато relate-натите данни често се достъпват заедно (nest-ването ще подобри read performance-a)/ "related data that is accessed together should be store together"
    - subdocument-ите се ъпдейтват рядко(няма да се налага да поддържаме множество колекции и референции)
    - размера на subdocument-ите не е много голям и не нараства значително във времето (16mb limit)
	
- referencing (refer to documents in another collection by storing the other doc id)
Използването на референции за поддържане на връзки между данните се нарича linking или data normalization.
Използва се главно при many-to-many връзки.(students,courses,enrolments collections)
Използването на референции избягва дублирането на данни и води до по-малки документи. Недостатък е ,че трябва да се 
трябва да се изпълнят повече от 1 read заявка,за да се прочетат данните, което се отразява на performance-a.
Нормализацията е препоръчителна, когато:

  - subdocument-ите са големи и не винаги се изискват при заявка към главния документ
  - иначе embed-натите subdocument-и се ъпдейтват често независимо от главния документ (especially relevant for write-heavy приложения)
  - дублираните данни затрудняват поддръжката

Пример за referencing и nest-ване на subdocument-и:
В документът репрезентиращ студент номерата са embed-нати, тъй като рядко ще бъдат променяни и броят им няма да нараства 
във времето. И тъй като имаме one-to-many връзка между студент и тел. номера, няма да имаме дублиране на данни.
За сметка на това броят на курсовете посещавани от студент ще се променя по-често и тъй като връзката между student-и и 
курсове е many-to-many ще имаме дублиране на данни сред различни документи при embed-ване и при евентуален ъпдейт на някой от курсовете ще се наложи да се ъпдейтнат всички документи,в които е вграден съответния курс. По тази причина за курсовете 
използваме referencing:

    {
        "student": "John Smith",
        "student_id": "008"
        "email": "randmail@hgt.com",
        "contact_number": [
            {"number": "...", "type": "home"},
            {"number": "...", "type": "cell"},
            {"number": "...", "type": "emergency"}
        ],
        "courses": [
            {
                "course_id": "CS150",
                "course_name": "MongoDB101"
            },
            {
                "course_id": "CS177",
                "course_name": "Intro to sth"
            }
        ]
    }

    {
        "courses": [
            {
                "course_id": "CS100",
                "course_name": "An intro to sth",
                "professor": "Baih"
                ...
            }
            {...}
            ...
        ]
    }

Embedding
+ single query to retrieve data
+ single update/delete operation
- data duplication при many-to-many връзки
- update на subdocument-и при many-to-many връзки ще изисква ъпдейтването на тези данни във всички документи
- large documents
Referencing
+ no duplication
+ smaller documents
- need to join data from multiple documents

Unbounded documents and large documents are schema anti-patterns.

embedded doc = nested doc = document in another document

embedded documents store related data in a single document

using references = linking = data normalization
denormalized data model - related data is stored in a single document

References save the _id field of one document in another document as a link between the two.(based on that field a second query should be executed to get the related data)

!!! MongoDB relies on application logic and conventions to manage and resolve references between documents.In MongoDB, there isn't an inherent mechanism to declare a field as a reference to another document like in relational databases where foreign key constraints are used.

Пример за embedding:

    {
        name: {
            firstName: "Sarah",
            lastName: "Davis"
        },
        job: "Professor",
        address: [
            {
                "type": "mailing",
                "street": "402 Maple"
            },
            {
                "type": "secondary",
                "street": "318 uni"
            }
        ]
    }

name и address са embedded/nested subdocuments
customer и address имат one-to-many relationship

Ако адресът е само един с embedding можем да реализираме и one-to-one relationship:

    {
        "user_id": 1,
        "name": "John Doe",
        "email": "john@example.com",
        "address": {
            "street": "123 Main St",
            "city": "Anytown",
            "state": "CA",
            "zip": "12345"
        }
    }
 Пример:
 Avoid unbounded documents(that grow infinitely) when creating data model.For example if we have a document representing a post that embeds all comments of the post, we can get all comments in a single read operation,but it can reach max doc size of 16mb.
 !!!Also each comment addition requires rewriting the whole document in Mongo.
 Also the pagination will be difficult since all comments are read at once.
 Since we probably wont have a case where we will need to read all post comments at once the embedded data model does not make sense in that case.
 It would be better to create comments collection where each comment will store "post_entry_id" to refer to specific post from the posts collection.

How you model your data depends entirely on your particular application's data access patterns. You want to structure your data to match the ways that your application queries and updates it.

### <span style="color:darkgoldenrod"> Каквo наричаме нормализация в MongoDB?
Процесът по разбиване на данните в отделни колекции и използването на референции, за да поддържаме връзка между тях.

### <span style="color:darkgoldenrod"> Каква е разликата между MongoDB data model-a и RDBMS data model-a?
В RDBMS дата модела е стриктен,докато в mongo е flexible, което значи че по подразбиране колекциите не изискват 
от документите да имат конкретна структура.

### <span style="color:darkgoldenrod"> Кой е основният принцип в Mongo по отношение на data modeling-a?
Data that is accessed together should be stored together.

### <span style="color:darkgoldenrod"> Какви са предимствата на добрия data model?
По-лесно менажиране на данните.
По-ефективни заявки.
Използване на по-малко memory и CPU при заявка.
Намалени разходи.

### <span style="color:darkgoldenrod"> Кои са schema anti-patterns?
Schema anti-patterns: massive arrays, massive number of collections, bloated documents, unnecessary indexes, quieries without indexes,data that is accessed together,but stored in different collections