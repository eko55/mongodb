MongoDB Database Metrics & Monitoring

Core metrics we should monitor:

- query targeting - measure read efficiency(ratio between document scanned and document returned,ideal 1:1).Very high ration impact perf,like 10000:1

	- storage - monitor disk space used by collections,databases and indexes
	
	once storage capacity is reached writes are refused and mongo could crash
	
	disk latency - the number of ms required to complete storage operation
	
	- CPU utilization - high cpu usage may indicate poor query performance
	
	- memory utilization
	- replication lag - delay between primary and secondary
	
	baseline values - metrics measured during steady workload
	
	burst values - occasional spikes are normal
	resource exhaustion - 90%+
	
	numRequests displays the average rate of requests sent to the database server per second over the selected sample period.
	------
Atlas Cloud manager is recommened to monitor self-managed deployment.
Percona MongoDB Exporter is an open source tool that can integrate with mongo and provide metrics.It can be configured as Prometheus target.Prometheus get metrics from Percona.And Grafana gets metrics from Prometheus.

Percona MongoDB Exporter connect to our mongo deployment as an user with clusterMonitor role

Mongo -> Percona -> Prometheus ->Grafana

We can use Grafana to ingest database metrics from a data source, like Prometheus, and display it on customizable charts for easy analysis.Grafana is an open-source web application that we can use to create interactive visualizations and analyze metrics from a self-managed MongoDB deployment.

--------------
command line metrics that are the source for metrics visualization tools:

	serverStatus - diagnostic command returing current instance state,includeing connection metrisc
	(Cloud Manager and Percona run this command at regular intervals) to collect statistics about instances
	
	db.runCommand({serverStatus:1})
	db.runCommand({serverStatus:1}).connections
	
	currentOp - command returning metrics about all active operations
	db.currentOp({ "msg": /Index Build/ }); //връща инфо за в момента протичащо създаване на индекс

	monitoring apps are using currentOp to identify slow operations
	
	db.adminCommand({
		currentOp: true,
		"$all": true, //all operations
		active: true
	})
	db.currentOp({ "cursor.originatingCommand.aggregate": "largeCollection" }, { "$all": true });
	
	killOp - administrative command used for (slow) operations termination
	
	db.adminCommand({
		killOp: 1,
		op: 16144387
	})
	db.killOp(2290)

Disk Queue Depth tells if the storage subsystem has become a bottleneck.
Disk Queue Depth tells us the average length of the queue of requests issued to the disk partition used by MongoDB. 
This metric will indicate whether or not operations are waiting to be serviced.

Opcounters measures the rate at which operations are performed in MongoDB

bytesIn displays the average rate of physical bytes (after any wire compression) sent to the database server per second over the selected sample period
bytesOut displays the average rate of physical bytes (after any wire compression) sent from the database server per second over the selected sample period.

You must have the Project Owner role in order to successfully configure new alerts for a specific host.

The atlas alerts acknowledge command requires id to successfully acknowledge an alert.
An alert’s status will only change to CLOSED once the condition that triggered the alert is resolved.

You can integrate Atlas with third-party monitoring services to receive Atlas alerts in external monitoring services, and to view and analyze performance metrics that Atlas collects about your cluster.