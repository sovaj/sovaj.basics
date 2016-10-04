package io.sovaj.basics.amazonws.client;

import java.io.InputStream;

/**
 * 
 * @author Mickael Dubois
 */
public interface IAmazonWSClient {

    /**
     * Push a file/object onto S3.
     *
     * @param contentType - The content type
     * @param content - The content
     * @param destKey - the destination key
     * @return String - The S3 URL for the content.
     * @throws AmazonWSException if deploying on amazon is problematic
     */
    String deploy(String contentType, InputStream content, String destKey) throws AmazonWSException;

    /**
     * Copy a file/object within S3.
     *
     * @param srcKey - the source key
     * @param destKey - the destination key
     * @throws AmazonWSException if deploying on amazon is problematic
     */
    void copy(String srcKey, String destKey) throws AmazonWSException;

    /**
     * Move a file/object within S3.
     *
     * @param srcKey - the source key
     * @param destKey - the destination key
     * @return String - The URL of the S3 location the file/object was moved to.
     * @throws AmazonWSException if deploying on amazon is problematic
     */
    String move(String srcKey, String destKey) throws AmazonWSException;

    /**
     * Delete a file/object from S3.
     *
     * @param key - The key to delete
     * @throws AmazonWSException if the content does not exist
     */
    void delete(String key) throws AmazonWSException;

    /**
     * Return an input stream connected to a file/object on S3.
     *
     * @param srcKey - the source key content to read
     * @return The content
     * @throws AmazonWSException if the content does not exist
     */
    InputStream read(String srcKey) throws AmazonWSException;

}
