package org.mdubois.basics.amazonws.client;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA. User: ZOuarab1 Date: 11/4/13 Time: 9:29 AM To
 * change this template use File | Settings | File Templates.
 */
public interface IAmazonWSClient {

    /**
     * Push a file/object to S3.
     *
     * @param content
     * @return
     * @throws AmazonWSException
     * @deprecated Renamed to deploy and changed so AmazonS3Content is
     * constructed inside the function.
     */
    String deployOnS3(AmazonS3Content content) throws AmazonWSException;

    /**
     * Push a file/object onto S3.
     *
     * @param contentType
     * @param content
     * @param destKey
     * @return String - The S3 URL for the content.
     * @throws AmazonWSException
     */
    String deploy(String contentType, InputStream content, String destKey) throws AmazonWSException;

    /**
     * Copy a file/object within S3.
     *
     * @param srcKey
     * @param destKey
     * @return
     * @throws AmazonWSException
     */
    void copy(String srcKey, String destKey) throws AmazonWSException;

    /**
     * Move a file/object within S3.
     *
     * @param srcKey
     * @param destKey
     * @return String - The URL of the S3 location the file/object was moved to.
     * @throws AmazonWSException
     */
    String move(String srcKey, String destKey) throws AmazonWSException;

    /**
     * Delete a file/object from S3.
     *
     * @param key
     * @throws AmazonWSException
     */
    void delete(String key) throws AmazonWSException;

    /**
     * Return an input stream connected to a file/object on S3.
     *
     * @param srcKey
     * @return
     * @throws AmazonWSException
     */
    InputStream read(String srcKey) throws AmazonWSException;

}
