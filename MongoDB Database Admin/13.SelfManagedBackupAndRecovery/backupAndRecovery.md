= L1. Backup Plans =
Key words: backup plan, key elements of a backup plan, recovery point objectives(RPOs), recovery time objectives (RTOs)

Backup plan includes:
1.How to back up data
- "mongodump" is a type of backup,suitable for small IoT devices, but not for larges systems

2.How often to back up data

3.How long to retain backup data

4.Where to store backup data

<span style="color:darkgoldenrod">Recovery Point Objective</span>: maximum acceptable amount of data loss a business is willing to tolerate in an event of disruption,expressed as an amount of time
RPO is determined by criticality of the data, time and effort required to recreate or re-enter lost data, cost of downtime
Example:
A business decided that 2 hours worth of data loss is acceptable.If they experience an outage at 12PM the business needs to recover all data that was recorder before 10 AM.

<span style="color:darkgoldenrod">Recovery Time Objective</span>: maximum amount of time that a business is willing to tolerate after an outage
RTO is determined by criticality of the system, recovery process, availability of resources,cost of downtime
Example:
A business has an RTO of 3 hours. All systems must be running by 3 hours after an outage

<span style="color:darkgoldenrod">L2. Filesystem Snapshots on a MongoDB server</span>:
Common way to create backup in Linux is to create filesystem snapshots.
Snapshot is a complete copy of your data at a specific point in time
It is suitable for both small and large systems.
Factors to consider before making a snapshot:
Snapshots create pointers between source volume and the snapshot volume.
Snapshot volume is a point-in-time, read-only view of a source volume.
A volume is a container with a filesystem,that allows us to store and access data.
Snapshot can be created with:
- Logical Volume Manager for Linux
- MongoDB Ops Manager
- MongoDB Atlas

You need to lock your MongoDB deployment by using the db.fsyncLock() command. This prevents additional write operations while creating the snapshot. After creating the snapshot, remember to unlock your deployment by using the db.fsyncUnlock() command.

Before creating a snapshot of MongoDb data the database have to be locked using "db.fsyncLock()" command, which flush all pending write operations to disk and locks the entire instance to prevent additional writes.
Release the lock with "db.fsyncUnlock()" command after the snapshot is taken.
The source volume may contain more than just your mongodb deployment, which may lead to very large snapshot volume archives.That is why it is recommended to isolate your mongodb deployment.
Sometimes component of mongodb deployment are stored separately.Sometimes the journal is separated for performance reasons.
The journal is a transaction log that is used to bring the db to valid state after a hard shutdown.
If the journal is not part of your snapshot, the snapshot will be incomplete.That is why it is importnat to create a snapshot of your entire deployment.

How to extract the data from the snapshot for offline storage(2 methods):
- snapshot volume archive (a complete copy of the source volume + any changes that occurred while the snapshot was being created) can be created with the Linux dd utility(this can make the resulting archive quite large)
- filesystem archive (mounting the snapshot volume and using filesystem tools such as tar to archive the actual data files) (can lead to smaller archive)
Which method to choose depends on what your backup plan requires, factors are size,the type of data,performance requirements

= L3. Filesystem snapshot volumes on a MongoDB server =
You can use the dd command to restore an archived volume snapshot. 
dd is a command-line utility in Linux that allows you to convert and copy data.

Backup and restore mongodb by Logical Volume Manager(LVM) snapshot and dd command (dd is a Linux command that allows you to convert and copy data,useful for backing up and restoring volumes using LVM)

For our mongodb deployment we have created physical volume on one of our provision hard drives. In this volume we have created group name "vg0".Inside the vg0 volume group we have created 600mb logical volume named "mdb" and mounted it to the data files located at /var/lib/mongodb

	1.Connect to the database with "mongosh"
	2.show dbs - list databases
	3.db.fsyncLock();
	4.exit //exit mongodb shell
	5.sudo lvcreate --size 100M --snapshot --name mdb-snapshot /dev/vg0/mdb                //create snapshot/logical volume with max size of 100mb with name mdb-snapshot and backed by our data store volume
	6.sudo lvs  //list logical volumes,snapshot volume is a logical volume
	7.mongosh
	8.db.fsyncUnlock()
	9.exit
//archive the snapshot
    10.sudo dd status=progress if=/dev/vg0/mdb-snapshot | gzip > mdb-snapshot.gz          //copies the snapshot volume and streams the data to the gzip to compress the content in an archive file in the cur dir
//we should move the above archive to a different location(on different server in case mongodb server becomes unavailable) for a safer storage

Restore the db from archive:
1.sudo lvcreate --size 1G --name mdb-new vg0
//extract the snapshot and write it to the new logical volume
2.gzip -d -c mdb-snapshot.gz | sudo dd status=progress of=/dev/vg0/mdb-new
//mount the volume somewhere to access the data files
3.sudo systemcl stop -l mongod; sudo systemctl status -l mongod
4.sudo rm -r /var/lib/mongodb/*       //delete existing mongodb data files
5.sudo unmount /var/lib/mongodb		  //unmount our mongodb deployment so we can mount the newly restored logical volume in its place
6.sudo mount /dev/vg0/mdb-new /var/lib/mongodb   //after this command the db is restored by snapshot
7.systemctl start -l mongod; sudo systemctl status -l mongod
8.mongosh
9.show dbs
//after being done with snapshots you can remove them from server to preserve resources

= L4. Filesystem archive on a mongodb server =
Backup and restore mongodb from an archive taken from the filesystem of a volume snapshot.We will use LVM and tar.

tar: Archives multiple files into a single file.Can compress data when option is enabled.
gzip: Compresses a single file.

For our mongodb deployment we have created physical volume on one of our provision hard drives. In this volume we have created group name "vg0".Inside the vg0 volume group we have created 600mb logical volume named "mdb" and mounted it to the data files located at /var/lib/mongodb

	1.Connect to the database with "mongosh"
	2.show dbs - list databases
	3.db.fsyncLock();
	4.exit //exit mongodb shell
	5.sudo lvcreate --size 100M --snapshot --name mdb-snapshot /dev/vg0/mdb                //create snapshot/logical volume with max size of 100mb with name mdb-snapshot and backed by our data store volume
	6.sudo lvs  //list logical volumes,snapshot volume is a logical volume
	7.mongosh
	8.db.fsyncUnlock()
	9.exit
	10.mkdir /tmp/mongodbsnap
//mount the snapshot volume we took earlier as read only on the newly created dir
11.sudo mount -t xfs -o nouuid,ro dev/vg0/mdb-snapshot /tmp/mongodsnap
//create archive on mongodbsnap dir
12.sudo tar -czvf mdb-snapshot.tar.gz -C /tmp/mongodbsnap/ .
//move the archive to different location for safer storage

Restore mongo from tar file:

    1.sudo mkdir /mdb
    2.sudo tar -xzf mdb-snapshot.tar.gz -C /mdb
    3.sudo systemctl stop -l mongod | sudo systemctl status -l mongod
    //make the mondogb user and group the owner of /mdb dir and all of its files, otherwise the server will fail to start after we change the db path in the conf file
    4.sudo chown -R mongodb:mongodb /mdb
    //change the db path in the mongo.conf file
    5.sudo nano /etc/mongod.conf
    dbPath: /mdb
    6.sudo systemctl start -l mongod | sudo systemctl status -l mongod

= L5. Backing up a mongodb deployment =
replica set - group of MongoDB instances,maintaining the same data set.Replica sets are the primary way of implementing replication accross multiple server in MongoDB.

Back up a replica set with mongodump.
mongodump - not ideal for large systems,good for small deployments and for seeding data.
mongodump should not be used on sharded clusters

//seeding data - populate a db with initial set of data

For production-quality backup and recovery MongoDB Atlas/Cloud Manager/OpsManager are recommended.(by mongo)

Example:
1.Create user with backup role:
mongosh admin

		db.createUser({
		   user: "backup-admin",
		   pwd: "backup-pass",
		   roles: ["backup"]
		 })
	2.Create a backup:
		mongodump \
		--oplog \
		--gzip \
		--archive=mongodump-april-2023.gz  \
		"mongodb://backup-admin@mongod0.repleset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset&readPreference=secondary"

The oplog option captures incoming write operations during the mongodump operation.
The gzip option compresses the output file.
The archive option is used to specify the file location for the dump file.
Finally connection string + auth source are specified.

	3.Create a backup for a specific collection:
		--collection=neighborhoods \
		--gzip \
		--archive=mongodump-neighborhoodss-2023.gz \
		"mongodb://backup-admin:@mongod0.repleset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/sample_restaurants?authSource=admin&replicaSet=replset"

The --archive option will create backups in an archive file that include the date of the backup.

mongodump --gzip --db=sales --collection=items --archive=backup/mongodump-april "mongodb://backup-admin:backup-pass@mongod0.replset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset"

Lab:
mongodump --db "library" --collection "books" --gzip --archive=/app/library.books.archive "mongodb://dba-admin:dba-pass@localhost:27000,localhost:27001,localhost:27002/?authSource=admin&replicaSet=mongodb-replSet"
//confirm the archive is created
ls -alh /app/library.books.archive

= L6.Restoring a MongoDb deployment =
Snapshots are better for larger systems.

mongorestore restores or seeds system from dump file created with mongodump.
When restorig a mongodb database with a dump file, the major version of mongodb in the dump should be the same as the version of mongo on the server where we run mongorestore.
The version of mongorestore used to load the data should be the same as the version of mongodump used to create the dump.

Example:
mongosh admin

	db.createUser({
	   user: "restore-admin",
	   pwd: "restore-pass",
	   roles: ["restore"]
	 })
	 
	mongorestore \
	--drop \
	--gzip \
	--oplogReplay \
	--noIndexRestore \
	--archive=mongodump-april-2023.gz \
	"mongodb://restore-admin@mongod0.repleset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset"

The --drop option removes any existing collections from the database.
The --gzip option is used to restore from a compressed file.
The --oplogReplay option replays the oplog entries from the oplog.bson file.
The --noIndexRestore option is used to reduce the impact on the system.
The archive option is used to specify the file location of the dump file. In this case, the file location is mongodump-april-2023.

"mongodb://restore-admin@mongod0.repleset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset" - specifies the seedlist of hosts and the replica set in the URI, which are needed to restore a MongoDB replica set by using mongorestore.

mongorestore --drop --gzip --archive=backup/mongodump-april-2023 "mongodb://restore-admin:restore-pass@mongod0.replset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset" - will restore all databases and collections, as it does not use the --db and --collection options.

Lab:
file /sampledata/library.books.archive - shows the file file type
mongorestore --gzip --archive=/sampledata/library.books.archive "mongodb://dba-admin:dba-pass@localhost:27000,localhost:27001,localhost:27002/?authSource=admin&replicaSet=mongodb-replSet"

= Conclusion =
In this unit, you learned how to:

Identify key elements of backup plans
Distinguish between Recovery Point Objective (RPO) and Recovery Time Objective (RTO)
Define what a snapshot is
Create a snapshot volume on Linux
Create a filesystem archive on Linux
Use mongodump to create a backup of a replica set that has access control enabled
Use mongorestore to restore a MongoDB replica set


It’s a good idea to store your backups on a separate server from the MongoDB deployment. This allows you to easily access your backups in case your MongoDB deployment server becomes unavailable. It also allows you to save server resources for your deployment server.

The tar command is used to create a new archive of all the files in a directory.

Username, password, and authSource in the connection string are all required to create a backup of a replica set with access control enabled:

    mongodump --gzip --archive=backup/mongodump-april "mongodb://backup-admin:backup-pass@mongod0.replset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset"

This command specifies the seedlist of hosts and the replica set in the URI, which are needed to restore a MongoDB replica set by using mongorestore:

    mongorestore --drop --gzip --archive=backup/mongodump-april-2023 "mongodb://restore-admin:restore-pass@mongod0.replset.com:27017,mongod1.replset.com:27017,mongod2.replset.com:27017/?authSource=admin&replicaSet=replset"