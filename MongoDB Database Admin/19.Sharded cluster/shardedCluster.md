### Sharding
При много голям обем от данни и голям на брой операции ресурсите на един сървър могат да бъдат 
изчерапи.
Има две възможни решения на този проблем: хоризонтално и вертикално скалиране.

При вертикалното скалиране увеличаваме ресурсите на машината,но максимумът от заделени ресурси, 
които хардуерът може да понесе отново може да се окаже недостатъчен.

При хоризонталното скалиране данните и заявките към базата се разпределят към множество сървъри.

MongoDB поддържа хоризонтално скалиране чрез <span style="color:darkgoldenrod">sharding</span>.

### Sharded cluster
В MongoDB sharded cluster се състои от:
- shards - всеки shard е replica set и пази част от данните
- mongos - query router, който приема всички заявки от client application-a и ги разпределя към
отделните shard-ове на база метаданните в config server-a. Обикновенно mongos инстанциите се 
инсталират на application server-ите, но могат да бъдат и на dedicated server.При app-ове с много
голям поток от заявки към базата можем да имаме load balancer пред mongos инстанциите.
- config servers - config server-ите са 3 и са конфигурирани в replica set(CSRS - Config Server Replica Set) 
и задачата им е да пазят метаданните и конфигурационните настройки за cluster-a

### How to move sharded cluster to a new hardware/datacenter without downtime
#### 1.Disable the balancer
Disable the balancer to stop chunk migration and do not perform any metadata write operations 
until the process finishes.
To disable the balancer connect to one mongos instance and run:

```sh.getBalancerState()```

```sh.stopBalancer```

```sh.getBalancerState()```

#### 2.Migrate each config server separately

https://www.mongodb.com/docs/manual/tutorial/migrate-sharded-cluster-to-new-hardware/

1.Start the replacement config server.
//Before you bind your instance to a publicly-accessible IP address, you must secure your cluster from unauthorized access. 

```
mongod --configsvr --replSet <replicaSetName> --bind_ip localhost,<hostname(s)|ip address(es)>
```

2.Add the new config server to the replica set

Connect mongosh to the primary of the config server replica set and use rs.add() to add the new member.

```
rs.add( { host: "<hostnameNew>:<portNew>", priority: 0, votes: 0 } )
```

The initial sync process copies all the data from one member of the config server replica set to the new member without restarting.

