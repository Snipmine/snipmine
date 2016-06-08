# Running snipcloud
### Building from source
To build the snipcloud server first install the snipcloud-core package locally using:
```
cd snipcloud/
mvn install
```
Then compile the snipcloud server using:
```
cd snipcloud-server
mvn package
```
### Prerequisites
To run snipcloud you need a running instance of the mysql server.

### Configuration
The snipcloud server reads a file called `snipcloud.conf` in the execution directory when started to load its configuration. The file is in json format and can contain the following parameters:
```json
{
"protocol" : "http",
"host" : "localhost",
"port" : 8000,
"dbname" : "snipcloud",
"dbuser" : "snipcloud",
"dbpass" : "",
}
```
### Starting the server
Simply run `java -jar snipcloud.jar`.

#Running snipmine
### Building from source
To build snipmine first install the snipcloud-client package locally (see building snipcloud)
Then compile snipmine using:
```
cd snipmine
mvn package
```

### Configuration
Snipmine reads a file called `snipmine.conf` in the execution directory when started to load its configuration. The file is in json format and can contain the following parameters:
```json
{
    "snipcloudUser" : "snipmine",
    "snipcloudPw" : "",
    "snipcloudUrl" : "http://localhost:8000/api",
    "classpathFileName" : "snipmine.classpath",
    "concurrentConnections" : 4,
    "maxAvgWidth" : 5,
    "maxDepth" : 15,
}
```
### Mining a project
Run `java -Dmaven.home=/path/to/your/maven -jar snipmine.jar <path to pom.xml file of project to mine>`.
