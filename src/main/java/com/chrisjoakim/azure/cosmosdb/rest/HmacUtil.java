package com.chrisjoakim.azure.cosmosdb.rest;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;
// https://eclipsesource.com/blogs/2016/07/06/keyed-hash-message-authentication-code-in-rest-apis/
// byte[] hmacSha256 = org.apache.commons.codec.digest.HmacUtils.hmacSha256(secretAccessKey, message);
// String hmacSha256Base64 = Base64.getEncoder().encodeToString(hmacSha256);

/**
 *
 */
public class HmacUtil {

    // Constants:
    public static String MAC_ALGORITHM = "HmacSHA256";

    // Instance variables:
    private String cosmosdbKey;
    private DateFormat dateFormatter;


    public HmacUtil(String cosmosdbKey) {

        super();
        this.cosmosdbKey = cosmosdbKey;
        this.dateFormatter = this.rfc7231DateFormat();
    }

    public String generateHmac(String httpVerb, String resourceType, String resourceLink, Date date) {

        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(Base64.decodeBase64(this.cosmosdbKey), MAC_ALGORITHM));
            String message = this.generateMessage(httpVerb, resourceType, resourceLink, date);
            byte[] digest = mac.doFinal(message.getBytes("UTF-8"));
            String signature =  Base64.encodeBase64String(digest);
            String encodable = "type=master&ver=1.0&sig=" + signature;
            return URLEncoder.encode(encodable, "UTF-8");
        }
        catch (Exception e) {
            System.err.println("Exception in HmacUtil#generateHmac: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    protected String generateMessage(String httpVerb, String resourceType, String resourceLink, Date date) {

        StringBuilder sb = new StringBuilder();
        sb.append("" + httpVerb.toLowerCase() + "\n");
        sb.append("" + resourceType.toLowerCase() + "\n");
        sb.append("" + resourceLink.toLowerCase() + "\n");
        sb.append("" + this.formatDate(date) + "\n\n");
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

    protected static void displayTimezoneIds() {

        String[] timeZoneIds = TimeZone.getAvailableIDs();
        for (int i = 0; i < timeZoneIds.length; i++) {
            System.out.println(timeZoneIds[i]);
        }
    }

    /**
     * This main() method is for ad-hoc testing purposes only.
     */
    public static void main(String[] args) {

        String cosmosdbKey = System.getenv("AZURE_COSMOSDB_SQLDB_KEY");
        System.out.println("main - cosmosdbKey: " + cosmosdbKey);

        long epoch = System.currentTimeMillis();
        System.out.println("main - epoch: " + epoch);
        Date date = new Date(epoch);

        HmacUtil util = new HmacUtil(cosmosdbKey);
        String s = util.formatDate(date);
        System.out.println("main - date: " + util.formatDate(date));

        String httpVerb = "get";
        String resourceType = "docs";
        String dbName = "dev";
        String collName = "airports";
        String docId = "72d3d5e7-313d-4c03-ae6c-f6a330e9fcb8";

        String resourceLink = util.documentResourceLink(dbName, collName, docId);
        System.out.println("main - resourceLink: " + resourceLink);

        String hmac = util.generateHmac(httpVerb, resourceType, resourceLink, date);
        System.out.println("main - hmac: " + hmac);
    }

}