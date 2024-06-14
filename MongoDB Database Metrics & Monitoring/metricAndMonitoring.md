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

	serverStatus - diagnostic command returing current instance state
	(Cloud Manager and Percona run this command at regular intervals) to collect statistics about instances
	
	db.runCommand({serverStatus:1})
	db.runCommand({serverStatus:1}).connections
	
	currentOp -administrative command returning metrics about active operations
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