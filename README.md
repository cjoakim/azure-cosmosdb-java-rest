# azure-cosmosdb-java-rest

A REST client for CosmosDB, implemented in Java

## Environment Variables:

This example code uses the following:
```
AZURE_COSMOSDB_SQLDB_URI
AZURE_COSMOSDB_SQLDB_KEY
```

## Classes

- HmacUtil - generates the HMAC Authorization header values
- RestClient - executes the HTTP/REST call, using the generated HMAC Authorization header 

See the main() methods for each class to see example usage.

## Compile, Execute, and Sample Output

```
$ mvn clean compile package

$ mvn exec:java -Dexec.mainClass="com.chrisjoakim.azure.cosmosdb.rest.RestClient"

main - cosmosdbUri: https://cjoakim-cosmosdb-sql.documents.azure.com:443/
main - cosmosdbKey: 5F ... secret ... cw==
getDocument-resourceLink: dbs/dev/colls/airports/docs/72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8
getDocument-fullUrl: https://cjoakim-cosmosdb-sql.documents.azure.com:443/dbs/dev/colls/airports/docs/72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8
message:
get
docs
dbs/dev/colls/airports/docs/72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8
sat, 22 dec 2018 14:21:44 gmt


header: Authorization -> type%3Dmaster%26ver%3D1.0%26sig%3D7rWQHXKTx3nyuGIBMQ95UxinOyeL5jJ1jxtHxz757kE%3D
header: Accept -> application/json
header: x-ms-date -> Sat, 22 Dec 2018 14:21:44 GMT
header: x-ms-version -> 2017-02-22
header: x-ms-documentdb-partitionkey -> ["CLT"]

main-responseCode: 200
main-responseData: {"name":"Charlotte Douglas Intl","city":"Charlotte","country":"United States","iata_code":"CLT","latitude":"35.214","longitude":"-80.943139","altitude":"748","timezone_num":"-5","timezone_code":"America/New_York","location":{"type":"Point","coordinates":[-80.943139,35.214]},"pk":"CLT","seq":3778,"last_update":0,"temperature":20.35801744984134,"humidity":91.04754455082039,"id":"72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8","_rid":"8SxQAKvbYoXvAQAAAAAAAA==","_self":"dbs\/8SxQAA==\/colls\/8SxQAKvbYoU=\/docs\/8SxQAKvbYoXvAQAAAAAAAA==\/","_etag":"\"0000f550-0000-0100-0000-5c151b2e0000\"","_attachments":"attachments\/","_ts":1544887086}

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.670 s
[INFO] Finished at: 2018-12-22T09:21:44-05:00
[INFO] ------------------------------------------------------------------------
```

## Maven Dependencies

See the pom.xml file.  This project has only these runtime dependencies:

```
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.11</version>
        </dependency>

        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.6</version>
        </dependency>

        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.9.8</version>
        </dependency>
```

Or run:

```
$ mvn dependency:tree
```
