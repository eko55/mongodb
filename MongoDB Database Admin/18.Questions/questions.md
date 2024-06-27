13.Which two mongod command line options affects where data files are stored?

    --dbpath
    --direcotryperdb

14. Which two mongosh commands connect to node-1.cluster1.mycompany.local on port 28412? 


    mongosh "node-1.cluster1.mycompany.local:28412"
    mongosh --host "node-1.cluster1.mycompany.local" --port 28412

15. What command returns in-progress operations that have been running for more than 3 seconds?
    
    
    db.currentOp( { "active" : true, "secs_running" : { "$gt" : 3 } } )

19. What is a benefit of enabling encryption at rest?

    Providing an additional layer of protection against unauthorized access.

20. What flag does the administrator use with mongodump during the backup process to capture operations for point-in-time recovery in a MongoDB replica set?

    
    mongodump --oplog