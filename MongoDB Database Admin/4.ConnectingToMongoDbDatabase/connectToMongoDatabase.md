### <span style="color:darkgoldenrod"> Какво е mongodb connection string?

mongodb connection string-a ни позволява да се connect-нем към the Mongo база (standalone cluster,replica set,sharded cluster)
Описва hostname(или ip адреса) и опциите, които ще използваме за връзката с базата.
Можем да използваме connection string-a за връзване с базата чрез mongosh, mongodb compass, програмен код.

Connect with Mongo shell:

    mongosh "mongodb+srv://mdb-training-cluster.swnn5.mongodb.net/myFirstDatabase" --apiVersion 1 --username MDBUser

Connect your application:

    mongodb+srv://MDBUser:<password>@mdb-training-cluster.swnn5.mongodb.net/myFirstDatabase?retryWrites=true&w=majority

Програмният код използва MongoDB драйвер за съответния език,за да се свърже с базата посредством connection string.

Connect using MongoDB Compass:

    mongodb+srv://MDBUser:<password>@mdb-training-cluster.swnn5.mongodb.net/test

### <span style="color:darkgoldenrod"> Колко формата има за connection string?
Standard формат и DNS seed list entry формат.

Standard format-ът изрично изброява адресът на всяка инстанция в replica set-a.
Standard format-ът е удобен когато имаме фиксиран сет от сървъри.

    mongodb://username:password@host1:27017,host2:27017,host3:27017/?replicaSet=myReplicaSet

- mongodb://: The protocol used to connect to MongoDB.
- username:password@: Optional authentication credentials.
- host1:27017,host2:27017,host3:27017: List of hosts and ports.
- /?replicaSet=myReplicaSet: Additional options such as the name of the replica set.

DNS seed list entry използва един DNS hostname,който се resolve-а до множество от адреси/hostnames на Mongo инстанции, 
към които ще се connect-нем. DNS seed list-а е удобен за динамични среди, където адресите на сървърите могат да се променят.

    mongodb+srv://username:password@cluster0.mongodb.net/test?retryWrites=true&w=majority

- mongodb+srv://: Показва употребата на DNS seed list
- cluster0.mongodb.net: DNS hostname-a, който ще се resolve-не до множество хостове
- /test: The database to connect to.
- ?retryWrites=true&w=majority: Additional options for the connection.

+srv опцията автоматично сетва TLS на true и казва на монго да използва dns seed list

<username>:<password> - username и парола за базата ,ако не са подадени се подразбира admin user-a

@cluster0.usqsf.mongodb.net - хостът и port-ът на базата,ако порта не е споменат ще се използва 27017

?... - опции

### <span style="color:darkgoldenrod"> Как да достъпим connection string-овете през Atlas?
Organization -> Project -> Cluster -> Connect

### <span style="color:darkgoldenrod"> Писане и изпълнение на JS код в mongosh:
mongosh is NodeJS REPL среда, което означава, че в shell-a можем да дефинираме променливи,функции и т.н.
![title](resources/runJsInMongosh.png)

### <span style="color:darkgoldenrod"> Възможни network access errors И user authentication errors при connect-ване към Atlas:
Ако публичното ip на машината от която искаме да се connect-нем не присъства в Network Access -> IpAccessList-a в Atlas:
![title](resources/publicIpNotSpecifiedInIpAccessListInAtlas.png)

Ако паролата е грешна или сме подали адрес на друга база в connection string-a:
![title](resources/AuthFailed.png)