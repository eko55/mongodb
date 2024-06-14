authentication - the process of identifying the user trying to access the database

create login credentials for users that need to connect to db

common auth mechanism - prompt for username and password

auth can be managed by the db itself or through integration with organization identity mgmt system

authorization - процесът по верифициране ,че даден user има права да изпълни определена операция

RBAC(role-based access control) - authorization approach, where permissions are granted to roles rather than to users.
Then users are granted one or more roles.

auditing - logging the changes made to the data and db configuration.Add overhead to the system.

Default auth mechanism in MongoDB is SCRAM (salted challenge response authentication mechanism)

How to enable authentication in mongo using SCRAM?
1.mongod process is started and mongosh is installed
2.Enable access control from conf file to enforce authentication
```sudo vi /etc/mongod.conf```

    #security
    security:
        authorization: enabled

```sudo systemctl restart mongod```

3.Connect to mongod with ```mongosh```. Успяваме благодарение на т.нар. localhost exception.
Така можем да създадем 1вият user (the user administrator), който да използваме за създаване и модифициране на всички други user-и и роли.
User admin потребителя трябва да се създаде в admin базата.
![title](./resources/createUserAdminUser.png)

userAdminAnyDatabase - вградена super user роля, която позволява на user-a да създава и модифицира user-и и роли
![title](./resources/logInWithUser.png)

Localhost exception - позволява да се свържем с localhost без аутентикация стига все още да не са създадени user-и и роли

