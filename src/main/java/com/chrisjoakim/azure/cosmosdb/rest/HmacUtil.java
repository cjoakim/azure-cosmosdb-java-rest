package com.chrisjoakim.azure.cosmosdb.rest;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility class to generate HMAC values for calls to the CosmosDB REST API.
 * Chris Joakim, Microsoft, 2018/12/22
 */
public class HmacUtil {

    // Constants:
    public static String MAC_ALGORITHM = "HmacSHA256";

    // Instance variables:
    private String cosmosdbKey;
    private DateFormat dateFormatter;

    /**
     * Default constructor; do not use.
     */
    private HmacUtil() {

        super();
    }

    /**
     * The given arg is a valid key to your CosmosDB account.
     */
    public HmacUtil(String cosmosdbKey) {

        super();
        this.cosmosdbKey = cosmosdbKey;
        this.dateFormatter = this.rfc7231DateFormat();
    }

    public String generateHmac(String httpVerb, String resourceType, String resourceLink, Date date) {

        try {
            String message = this.generateMessage(httpVerb, resourceType, resourceLink, date);
            System.out.println("message:\n" + message);

            // See the new CosmosDB Java SDK code at https://github.com/Azure/azure-cosmosdb-java for examples
            byte[] key = Base64.decodeBase64(this.cosmosdbKey.trim().getBytes("UTF-8"));
            SecretKey hmacKey = new SecretKeySpec(key, MAC_ALGORITHM);
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(hmacKey);
            byte[] digest = mac.doFinal(message.getBytes());
            String signature = Base64.encodeBase64String(digest);
            return URLEncoder.encode("type=master&ver=1.0&sig=" + signature, "UTF-8");
        }
        catch (Exception e) {
            System.err.println("Exception in HmacUtil#generateHmac: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    protected String generateMessage(String httpVerb, String resourceType, String resourceLink, Date date) {

        StringBuilder sb = new StringBuilder();
        sb.append(httpVerb.toLowerCase());
        sb.append("\n");
        sb.append(resourceType.toLowerCase());
        sb.append("\n");
        sb.append(resourceLink.toLowerCase());
        sb.append("\n");
        sb.append(this.formatDate(date).toLowerCase().trim());
        sb.append("\n\n");
        return sb.toString();
    }

    /**
     * Return a String representation of the given Date object in RFC7231 format
     * like "Fri, 21 Dec 2018 20:51:02 GMT" (a Date from epoch value 1545425462822).
     */
    protected String formatDate(Date date) {

        return this.dateFormatter.format(date);
    }

    protected DateFormat rfc7231DateFormat() {

        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
        df.setLenient(false);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    protected String documentResourceLink(String dbName, String collName, String docId) {

        return String.format("dbs/%s/colls/%s/docs/%s", dbName, collName, docId);
    }

    /**
     * This main() method simply demonstrates the use of this class.
     */
    public static void main(String[] args) {

        try {
            String cosmosdbKey  = System.getenv("AZURE_COSMOSDB_SQLDB_KEY");
            String resourceLink = "dbs/dev/colls/airports/docs/72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8";

            HmacUtil util = new HmacUtil(cosmosdbKey);
            String hmac = null;
            Date date = null;

            // The same instance may be reused multiple times, as follows:
            // Notice how the generated hmac value changes over time with the different Date values.

            date = new Date();
            hmac = util.generateHmac("GET", "docs", resourceLink, date);
            System.out.println(String.format("date: %s  hmac: %s", util.formatDate(date), hmac));
            Thread.sleep(2000);

            date = new Date();
            hmac = util.generateHmac("GET", "docs", resourceLink, date);
            System.out.println(String.format("date: %s  hmac: %s", util.formatDate(date), hmac));
            Thread.sleep(2000);

            date = new Date();
            hmac = util.generateHmac("GET", "docs", resourceLink, date);
            System.out.println(String.format("date: %s  hmac: %s", util.formatDate(date), hmac));
            Thread.sleep(2000);

            // Output below:
            // date: Sat, 22 Dec 2018 14:03:47 GMT  hmac: type%3Dmaster%26ver%3D1.0%26sig%3D2dzWFgGaz4GXL4a34EohtgXiPndb6AQc7Luyfo1pk9U%3D
            // date: Sat, 22 Dec 2018 14:03:49 GMT  hmac: type%3Dmaster%26ver%3D1.0%26sig%3Dd0w8SAr2A9aGIjBc21Endi6yYsmJMWYZXm896qhre%2FE%3D
            // date: Sat, 22 Dec 2018 14:03:51 GMT  hmac: type%3Dmaster%26ver%3D1.0%26sig%3DBv6md1avG33XEdzgffMKke2SxD%2B96%2FrZqCY%2Bbh9pu%2BE%3D
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
