package com.chrisjoakim.azure.cosmosdb.rest;

import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RestClient {

    // Instance variables:
    private String     cosmosdbUri;
    private String     cosmosdbKey;
    private HttpClient httpClient;
    private Date       date;
    private int        responseCode;
    private String     responseData;


    public RestClient(String cosmosdbUri, String cosmosdbKey) {

        super();
        this.cosmosdbUri  = cosmosdbUri;
        this.cosmosdbKey  = cosmosdbKey;
        this.httpClient   = HttpClientBuilder.create().build();
        this.date         = new Date();
        this.responseCode = -1;
    }

    private String getDocument(String dbName, String collName, String partitionKey, String docId) throws Exception {

        HmacUtil hmacUtil = new HmacUtil(this.cosmosdbKey);
        String resourceType = "docs";
        String resourceLink = documentResourceLink(dbName, collName, docId);
        String fullUrl = this.cosmosdbUri + resourceLink;
        System.out.println("getDocument-fullUrl: " + fullUrl);

        String hmac = hmacUtil.generateHmac("get", resourceType, resourceLink, this.date);
        System.out.println("getDocument-hmac: " + hmac);

        String pkJsonArray = "[\"" + partitionKey + "\"]";
        System.out.println("getDocument-pkJsonArray: " + pkJsonArray); // ["CLT"]

        HttpGet request = new HttpGet(fullUrl);
        request.setHeader("Authorization", hmac);
        request.setHeader("x-ms-date", hmacUtil.formatDate(this.date));
        request.setHeader("x-ms-version", "2017-02-22");
        request.setHeader("x-ms-documentdb-partitionkey", pkJsonArray);

        HttpResponse response = httpClient.execute(request);
        this.responseCode = response.getStatusLine().getStatusCode();

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    protected String documentResourceLink(String dbName, String collName, String docId) {

        return String.format("dbs/%s/colls/%s/docs/%s", dbName, collName, docId);
    }

    /**
     * This main() method is for ad-hoc testing purposes only.
     */
    public static void main(String[] args) {

        try {
            String cosmosdbUri = System.getenv("AZURE_COSMOSDB_SQLDB_URI");
            String cosmosdbKey = System.getenv("AZURE_COSMOSDB_SQLDB_KEY");
            System.out.println("main - cosmosdbUri: " + cosmosdbUri);
            System.out.println("main - cosmosdbKey: " + cosmosdbKey);

            String dbName = "dev";
            String collName = "airports";
            String partitionKey = "CLT";
            String docId = "72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8";

            RestClient client = new RestClient(cosmosdbUri, cosmosdbKey);

            String responseData = client.getDocument(dbName, collName, partitionKey, docId);
            System.out.println("main-responseCode: " + client.responseCode);
            System.out.println("main-responseData: " + client.responseData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//[
//        {
//        "name": "Charlotte Douglas Intl",
//        "city": "Charlotte",
//        "country": "United States",
//        "iata_code": "CLT",
//        "latitude": "35.214",
//        "longitude": "-80.943139",
//        "altitude": "748",
//        "timezone_num": "-5",
//        "timezone_code": "America/New_York",
//        "location": {
//        "type": "Point",
//        "coordinates": [
//        -80.943139,
//        35.214
//        ]
//        },
//        "pk": "CLT",
//        "seq": 3778,
//        "last_update": 0,
//        "temperature": 20.35801744984134,
//        "humidity": 91.04754455082039,
//        "id": "72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8",
//        "_rid": "8SxQAKvbYoXvAQAAAAAAAA==",
//        "_self": "dbs/8SxQAA==/colls/8SxQAKvbYoU=/docs/8SxQAKvbYoXvAQAAAAAAAA==/",
//        "_etag": "\"0000f550-0000-0100-0000-5c151b2e0000\"",
//        "_attachments": "attachments/",
//        "_ts": 1544887086
//        }
//        ]