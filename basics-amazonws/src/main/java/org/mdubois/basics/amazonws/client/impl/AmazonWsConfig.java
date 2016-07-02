package org.mdubois.basics.amazonws.client.impl;

/**
 * Created with IntelliJ IDEA.
 * User: ZOuarab1
 * Date: 10/22/13
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonWsConfig {
    private final String accessKey;
    private final String secretKey;
    private final String s3Bucket;
    private final String S3AmazonawsComWebUrl;

    public AmazonWsConfig(String accessKey, String secretKey, String s3Bucket, String s3AmazonawsComWebUrl) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.s3Bucket = s3Bucket;
        S3AmazonawsComWebUrl = s3AmazonawsComWebUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }
    public String getSecretKey() {
        return secretKey;
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public String getS3AmazonawsComWebUrl() {
        return S3AmazonawsComWebUrl;
    }
}
