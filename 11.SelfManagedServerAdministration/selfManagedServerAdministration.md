=== L1. Managing MongoDB Servers ===

mongod - primary daemon service for MongoDB system
//daemon is a process that runs in the background. Daemons are long-running processes that typically start when the system boots and continue running until the system is shut down.
//by convention daemons have d appended to their name

Install MongoDB Community
1.Import the public key that’s used by the package management system
wget -qO - https://www.mongodb.org/static/pgp/server-6.0.asc | sudo apt-key add -

2.Create a list file for MongoDB:
echo “deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/6.0 multiverse” | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list

3.Reload the local packages
sudo apt-get update

4.Install latest stable version of MongoDb community edition
sudo apt-get install -y mongodb-org

sudo systemctl start mongod //start mongod service
sudo systemctl status mongod
sudo systemctl stop mongod

Lab:
systemctl start mongod
mongosh localhost:27000

=== L2. Configuring MongoDB servers ===
mongodb config file is yml file

/etc/mongod.conf - default location

Default config settings top level keys:
storage - where mongodb stores its data
systemlog - specify the path to the log
net - spcify the port and where mongod should listen for client connections
security - to enable authorization

They contain options.
Security is not enabled by default. Enable it rightaway after deployment. Then we will have role-based access control and the user will need the respective priviledges to access database resources.

Edit conf file: sudo nano /etc/mongod.conf -> Restart mongod service: sudo systemctl restart -l mongod

ss -ltp | grep mongo -> show port where mongo is running

=== L3. Connecting to the MongoDB Servers ===
Create db user with mongosh
Run admin command in mongosh using db.adminCommand()

All connections from localhost have full access to the instance so that the first user can be created on the admin database.This apply only when no users or roles are yet created.
Connections using the "Localhost Exception" can only create the first user.So it is essential for this user to have role that allows it to create new users.
root role gives full access to the instance, including users creation

> sudo systemctl start mongod
> mongosh //connect to mongodb on default port
> use admin //switch to admin db
//db.adminCommand(<command>) let us run commands against the admin db no matter the context
> db.createUser(
{
user: "dbaTestAdmin",
pwd: "dbaTestPassword",
roles: [ { role: 'root', db: 'admin' } ]
}
)
> show users
> db.auth("newlyCreatedUser", passwordPrompt())
> show users

db.adminCommand(
{
createUser: "dbaTestAdmin",
pwd: "dbaTestPassword",
roles: [
{ role: "userAdminAnyDatabase", db: "admin" }
]
}
)
is preffered rather than
use admin
db.createUser(...)

db.adminCommand({ shutdown: 1 }) //stop mongod by using mongosh

=== Lab ===
1.Start mongod service
systemctl start mongod

2.Connect to the mongod by using the MongoDB Shell
mongosh

3.Create user
db.adminCommand({createUser:"dbaTestAdmin", pwd:"dbaTestPassword", roles: [{role: "userAdminAnyDatabase", db: "admin"}]})
4.Switch to admin db
use admin
5.Authenticate
db.auth("dbaTestAdmin", passwordPrompt())

=== L4. Logging Basics for MongoDB Server ===
Return the file path for the log file:
db.serverCmdLineOpts().parsed.systemLog.path

show logs - display available tag names,used to group messages by name
show log <type> - display all log entries associated with a tag name or type:
show log global - display the combined output of all recent log entries: 