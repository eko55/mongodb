### <span style="color:darkgoldenrod"> Аuthentication в MongoDB:
authentication - процесът по идентифициране на user-a пред базата

authentication-a може да се менажира от базата или чрез интеграция с identity management система

### <span style="color:darkgoldenrod"> Authorization в MongoDB:
authorization - процесът по верифициране ,че даден user има права да изпълни определена операция

Оторизацията в монго се осъществява чрез <span style="color:orange">role-based access control(RBAC)</span> - на потребителите се присвояват роли, като 
всяка роля има сет от permission-и за изпълняване на определени операции.

auditing - процесът по логване главно на event-и свързани със сигурността(опити за достъп до базата,etc.), спазването на регулаторни изисквания(GDPR,etc.),промени по конфигурацията на базата.
Adds overhead to the system.


Default-ния authentication механизъм в MongoDB е SCRAM (salted challenge response authentication mechanism).При него
user-a инициира authentication процеса подавайки username на сървъра, който връща salt и iteration count, които клиента 
комбинира с паролата.Получената стойност се хешира с SHA-1 или SHA-256 и се продуцира stored key и server key.
Тези ключове се използва за генериране на client proof, който се изпраща на сървъра. Сървъра използва същата salt и 
iteration count заедно със съхраняваната потребителска парола,за да верифицира client proof-a. При този механизъм 
паролата изобщо не се изпраща по мрежата,което елиминира възможността за нейното прихващане.

За да избегнете hardcode-ване на пароли в connection string-a използвайте environment променливи

Example of Secure Connection String using TLS/SSL looks like this:

    mongodb://myUser:myPassword@localhost:27017/mydatabase?ssl=true

### <span style="color:darkgoldenrod"> Built-in роли и съответните им permission-и:
- Database User Roles:
  - read - позволява на user-a да чете данни от конкретна база
  - readWrite - позволява на user-a да чете и записва данни в конкретна база
  
- Database Admin Roles: 
  - dbAdmin - позволява създаването на идекси,гледането на статистики,compact и repair на базата и др.
  - userAdmin - позволява създаването и управлението на user-и и роли
  - dbOwner - комбинира dbAdmin,userAdmin и readWrite за съответната база - дава пълен контрол върху съответната база

- Superuser roles
  - root - пълни права върху всички бази и системни операции - най-високо ниво на контрол
  
- All-Database Roles:
  - readAnyDatabase - позволява четене на данни от всяка база освен config и local
  - readWriteAnyDatabase - позволява на потребителя да чете и пише във всяка база освен config и local
  - userAdminAnyDatabase - позволява създаването и управлението на user-и и роли във всички бази
  - dbAdminAnyDatabase - позволява изпълняването на административни task-ове във всички бази
  
- Backup и Restoration roles:
  - backup - потребителя може да изпълнява backup операции, включително четене на oplog-a и колекции
  - restore - потребителя може да restore-ва данни от backup
  
- Cluster Administration Roles:
  - clusterAdmin - позволява управлението на цял клъстър, включително управлението на sharded колекции и replica set-ове 
  - clusterManager,clusterMonitor,hostManager


### <span style="color:darkgoldenrod"> Как да създадем user в mongo?

1.Enable authentication from conf file

```sudo vi /etc/mongod.conf```

    #security
    security:
        authorization: enabled

```sudo systemctl restart mongod```

2.Connect to mongod with ```mongosh```.

3.Създаваме user в admin базата,с права да създава и менажира user-и и роли във всички бази:

    db.createUser(
    {
      user: "globalUserAdmin",
      pwd: passwordPrompt(),
      roles: [
        { role: "userAdminAnyDatabase", db: "admin" }
      ]   
    )
    
Въпреки,че аутентикацията е активирана,успяваме да се свържем с базата през mongo shell-a без да подаваме credential-и 
благодарение на т.нар. localhost exception.Това ни позволява да създадем първия user, с който в последствие ще 
създаваме нови user-и и роли. Веднъж когато имаме създаден user в admin базата localhost exception-a вече не е валиден.
![title](./resources/createUserAdminUser.png)

![title](./resources/logInWithUser.png)

### <span style="color:darkgoldenrod">Свързване към default-ната база с конкретен user:

    mongosh --username globalUserAdmin

### <span style="color:darkgoldenrod">Свързване към конкретна база с конкретен user:

    db.createUser(
    {
      user: "analystUser",
      pwd: passwordPrompt(),
      roles: [
        { role: "read", db: "sample_analytics" }
      ]   
    )

    mongosh "mongodb://analystUser@localhost:27017/sample_analytics?authSource=admin"

authSource=admin частта позказва че analystUser-a трябва да бъде аутентикиран срещу admin базата

Localhost exception - позволява да се свържем с localhost без аутентикация стига все още да не са създадени user-и и роли

### <span style="color:darkgoldenrod">Настройване на authorization чрез Role Based Access Control.
Добавяне на роля към потребител при неговото създаване:
![title](./resources/createUser.png)
![title](./resources/confrimReadPermissions.png)

### <span style="color:darkgoldenrod"> Отнемане роля на потребител:

    db.getUser("analystUser")

    db.revokeRolesFromUser(
      "analystUser",
      [
        { role: "read", db: "sample_analytics" }
      ]   
    )

    db.getUser("analystUser")

![title](./resources/revokeUserRole.png)

![title](./resources/createUser2.png)

### <span style="color:darkgoldenrod">Как да актвираме auditing?
- Добавяйки --auditDestination опцията при стартиране на mongod.
- Сетвайки auditLog.destination опцията в config файла.

Можем да записваме audit event-и в:
- syslog-a в JSON формат (не е налична на Windows)
- конзолата в JSON формат
- файл в JSON или BSON формат


    auditLog:
        destination: file
        format: JSON
        path: /var/log/mongodb/auditLog.json

![title](./resources/auditLog.png)

### <span style="color:darkgoldenrod">Какво е encryption?
Encoding data to ensure only permitted users can read it.

Encryption categories:
- <span style="color:orange">transport encryption(network encryption)</span> - 
<span style="color:orange">Mongo поддържа TLS за криптиране на комуникацията между клиентите и монго инстанциите(data in motion)</span>
Това предпазва данните при потенциален interception(освен ако attacker-a няма съответните decryption 
ключове).<span style="color:orange">TLS(transport layer security)</span> е криптографски протокол осъществяващ transport encryption.
  
За да използва TLS, монго трябва да има валиден TLS сертификат(issued by certificate authority или self-signed cert),верифициращ identity-то на всеки от сървърите в replica set-a.
Always enable TLS.
В Atlas e enabled по default,в self-managed deployment-не.За да включум TLS-a трябва да имаме TLS сертификат в pim файл на всеки от сървърите в релика сета.

**Deploy 3 member replica set with TLS enabled:**

![title](./resources/enableTLS.png)
![title](./resources/enableTLS2.png)
- Повтаряме същите стъпки за остналите сървъри от replice set-a
- Рестартираме mongod на всеки сървър:```sudo systemctl restart mongod```
- Създаваме (initiate) replica set-a:
  - connect-ваме се към mongod0 с connection string включващ следните TLS опции:
  ![title](./resources/connectToInstanceWithTLS.png)
  ![title](./resources/replicaSetInit.png)
- Тестваме,че сме конфигурирали TLS успешно:
![title](./resources/connectWithTLS.png)
![title](./resources/testTLSconfWithBadConnectionString.png)

### <span style="color:darkgoldenrod">Еncryption at rest
<span style="color:darkgoldenrod">Описание:</span>
Процесът по криптиране на данните в базата, заедно с backup копия-та.

<span style="color:darkgoldenrod">Реализация:</span>
  <span style="color:orange">Encrypted Storage Engine</span>-а криптира data file-овете на диска,
  които могат да се декриптират само със съответния decryption ключ.Engine-a е наличен само за mongo enterprise.
  File system and full disk encryption са external подходите за постигане на encryption at rest при отсъствието на Encrypted Storage Engine.

<span style="color:darkgoldenrod">Limitations:</span>
- Ако криптиращият данните ключ бъде изгубен или откраднат, данните могат да станат компреметирани или недостъпни.
- Не работи срещу вътрешни заплахи(insider attacks)-от вече оторизирани user-и.
- Не може да защити вече декриптираните данни по време на обработка в RAM паметта.

### <span style="color:darkgoldenrod"> In-use encryption
<span style="color:darkgoldenrod">Описание:</span>
Данните се криптират в app-a още преди да бъдат изпратени към базата и се декриптират, когато отново постъпят в app-a, така server-a работи само с криптирани данни.

<span style="color:darkgoldenrod">Реализация:</span>
Предпазва от insider attacks и в монго се постига със <span style="color:orange">CSFLE/ client-side field level encryption</span>. Така данните заредени в РАМ паммета също са криптирани.
Данните се криптират и декриптират в client app-a, server-a работи само с криптирани данни.
  <span style="color:orange">CSFLE осигурява in-use encryption, at rest encryption и transport encryption</span>
Със CSFLE можем да криптираме отделни полета в документ,преди да го изпратим по мрежата към базата.По този нячин те няма да бъдат expose-нати в plain text по време на query lifecycle-a.
В същото време client-a взима декриптиращи ключове от key management system-a.
CSFLE работи за community и enterprise, но community версия изисква ръчно настройване на encryption логиката в app-a с помощта на монгодб encryption библиотеката.
При enterprise имаме автоматично криптиране и декриптиране, като посочваме полетата за криптиране в JSON schema-та

Automatic encryption is a mechanism available in MongoDB Enterprise for setting up Client-Side Field Level Encryption (CSFLE). Automatic encryption enables you to perform encrypted read and write operations without having to write code to specify how to encrypt fields.

### <span style="color:darkgoldenrod">Инсталиране на replica set,който приема само кънекции криптирани с TLS?
Със следната конфигурация в mongod.conf файла, сървърът ще приема само TLS-криптирани кънекции:

    net:
      tls:
        mode: requireTLS

Задачи:

    Distinguish between authentication and authorization
    
    Define role-based access
    
    Set up SCRAM for client authentication on a standalone mongod instance
    
    Set a built-in role for a database user
    
    Remove a role from a user
    
    Access the audit log
    
    Identify the purpose of enabling TLS
    
    Identify the purpose and limitations of encryption at rest
    
    Distinguish between client-side field-level encryption and encryption at rest
    
    Identify how MongoDB encrypts data at rest, data in transit, and data in use
    
    Enable network encryption (TLS) on a MongoDB replica se

