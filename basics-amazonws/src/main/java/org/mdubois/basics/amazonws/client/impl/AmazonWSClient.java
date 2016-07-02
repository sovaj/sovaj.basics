package org.mdubois.basics.amazonws.client.impl;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.mdubois.basics.amazonws.client.AmazonS3Content;
import org.mdubois.basics.amazonws.client.AmazonWSException;
import org.mdubois.basics.amazonws.client.IAmazonWSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: ZOuarab1 Date: 10/22/13 Time: 1:53 PM To
 * change this template use File | Settings | File Templates.
 */
public class AmazonWSClient implements IAmazonWSClient {

    public static final String URL_SUB_DIRECTORY_SEPARATOR = "/";
    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonWSClient.class);

    private ClientConfiguration clientConfig;
    private AWSCredentials credentials;
    private AmazonS3 conn;
    private String s3Bucket;
    private String S3AmazonawsComWebUrl;

    /**
     * @param wsConfig
     */
    public AmazonWSClient(AmazonWsConfig wsConfig) {
        this.credentials = new BasicAWSCredentials(wsConfig.getAccessKey(), wsConfig.getSecretKey());
        this.s3Bucket = wsConfig.getS3Bucket();
        this.clientConfig = new ClientConfiguration();
        this.S3AmazonawsComWebUrl = wsConfig.getS3AmazonawsComWebUrl();
        clientConfig.setProtocol(Protocol.HTTPS);
        conn = new AmazonS3Client(credentials, clientConfig);
    }

    private String buildUrl(String subDirectory, String contentLabel) {

        StringBuilder urlSb = new StringBuilder(S3AmazonawsComWebUrl);

        if (!StringUtils.startsWith(subDirectory, URL_SUB_DIRECTORY_SEPARATOR)) {
            urlSb.append('/');
        }
        urlSb.append(subDirectory);
        if (!StringUtils.endsWith(subDirectory, URL_SUB_DIRECTORY_SEPARATOR)) {
            urlSb.append('/');
        }
        if (StringUtils.startsWith(contentLabel, URL_SUB_DIRECTORY_SEPARATOR)) {
            urlSb.append(contentLabel.substring(1));
        } else {
            urlSb.append(contentLabel);
        }
        return urlSb.toString();
    }

    private String buildUrl(String key) {
        String url = S3AmazonawsComWebUrl;
        if (!StringUtils.startsWith(key, URL_SUB_DIRECTORY_SEPARATOR)) {
            url += '/';
        }
        return url + key;
    }

    private boolean validateContent(AmazonS3Content content) {
        if (StringUtils.isBlank(content.getSubDirectory())) {
            return false;
        }
        if (StringUtils.isBlank(content.getMimeType())) {
            return false;
        }
        if (content.getContent() == null) {
            return false;
        }
        if (StringUtils.isBlank(content.getContentLabel())) {
            return false;
        }
        return true;
    }

    @Override
    @Deprecated
    public String deployOnS3(AmazonS3Content content) throws AmazonWSException {
        LOGGER.trace("Deploying Content: {}", ToStringBuilder.reflectionToString(content, ToStringStyle.MULTI_LINE_STYLE));

        if (!validateContent(content)) {
            throw new AmazonWSException("Invalid content");
        }

        AmazonS3 conn = new AmazonS3Client(credentials, clientConfig);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(content.getMimeType());
        StringBuilder targetFolder = new StringBuilder(this.s3Bucket);
        if (!StringUtils.endsWith(targetFolder.toString(), URL_SUB_DIRECTORY_SEPARATOR) & !StringUtils.startsWith(content.getSubDirectory(), URL_SUB_DIRECTORY_SEPARATOR)) {
            targetFolder.append(URL_SUB_DIRECTORY_SEPARATOR);

        }
        targetFolder.append(content.getSubDirectory());
        try {
            PutObjectResult result = conn.putObject(targetFolder.toString(), content.getContentLabel(), content.getContent(), metadata);
            if (result != null) {
                return buildUrl(content.getSubDirectory(), content.getContentLabel());
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new AmazonWSException(e.getMessage(), e);
        }
    }

    @Override
    public String deploy(String contentType, InputStream contentStream, String destKey) throws AmazonWSException {

        AmazonS3Content content = new AmazonS3Content(s3Bucket, contentType, contentStream, destKey);

        LOGGER.trace("Deploying Content: {}", ToStringBuilder.reflectionToString(content, ToStringStyle.MULTI_LINE_STYLE));

        if (!validateContent(content)) {
            throw new AmazonWSException("Invalid content");
        }

        AmazonS3 conn = new AmazonS3Client(credentials, clientConfig);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(content.getMimeType());
        try {
            PutObjectResult result = conn.putObject(content.getSubDirectory(), content.getContentLabel(), content.getContent(), metadata);
            if (result != null) {
                return buildUrl(content.getSubDirectory(), content.getContentLabel());
            } else {
                return null;
            }

        } catch (Exception e) {
            throw new AmazonWSException(e.getMessage(), e);
        }
    }

    @Override
    public void copy(String srcKey, String destKey) throws AmazonWSException {
        try {
            CopyObjectResult result = conn.copyObject(new CopyObjectRequest(s3Bucket, srcKey, s3Bucket, destKey));
        } catch (Exception ex) {
            LOGGER.warn("Could not copy from key '" + srcKey + "' to key '" + destKey + "'", ex);
            throw new AmazonWSException(ex.getMessage(), ex);
        }
    }

    @Override
    public String move(String srcKey, String destKey) throws AmazonWSException {
        try {
            copy(srcKey, destKey);
        } catch (AmazonWSException ex) {
            LOGGER.warn("Move failed at copy from key '" + srcKey + "' to key '" + destKey + "'");
            throw ex;
        }
        try {
            delete(srcKey);
        } catch (AmazonWSException ex) {
            LOGGER.warn("Move failed at delete key '" + srcKey + "'");
            throw ex;
        }
        return buildUrl(destKey);
    }

    @Override
    public void delete(String srcKey) throws AmazonWSException {
        try {
            conn.deleteObject(new DeleteObjectRequest(s3Bucket, srcKey));
        } catch (Exception ex) {
            LOGGER.warn("Could not delete key '" + srcKey + "'", ex);
            throw new AmazonWSException(ex.getMessage(), ex);
        }
    }

    @Override
    public InputStream read(String srcKey) throws AmazonWSException {
        try {
            S3Object object = conn.getObject(new GetObjectRequest(s3Bucket, srcKey));
            return object.getObjectContent();
        } catch (Exception ex) {
            LOGGER.warn("Could not get stream for S3 object with key '" + srcKey + "'", ex);
            return null;
        }
    }
}
