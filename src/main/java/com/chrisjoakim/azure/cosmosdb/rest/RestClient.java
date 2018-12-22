package com.chrisjoakim.azure.cosmosdb.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Utility class to execute a HTTP REST call to CosmosDB.
 * It uses sibling class HmacUtil to generate the HMAC value for the Authorization header.
 * Chris Joakim, Microsoft, 2018/12/22
 */
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
        System.out.println("getDocument-resourceLink: " + resourceLink);

        String fullUrl = this.cosmosdbUri + resourceLink;
        System.out.println("getDocument-fullUrl: " + fullUrl);

        String hmac = hmacUtil.generateHmac("get", resourceType, resourceLink, this.date);

        String[] partitionKeys = { partitionKey };
        ObjectMapper objectMapper = new ObjectMapper();
        String pkJsonArray = objectMapper.writeValueAsString(partitionKeys);

        HttpGet request = new HttpGet(fullUrl);
        request.setHeader("Authorization", hmac);
        request.setHeader("Accept", "application/json");
        request.setHeader("x-ms-date", hmacUtil.formatDate(this.date));
        request.setHeader("x-ms-version", "2017-02-22");
        request.setHeader("x-ms-documentdb-partitionkey", pkJsonArray); // <- a JSON Array!

        Header[] headers = request.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Header h = headers[i];
            System.out.println("header: " + h.getName() + " -> " + h.getValue());
        }
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
            System.out.println("main-responseData: " + responseData);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*
Program Output below:

*/
