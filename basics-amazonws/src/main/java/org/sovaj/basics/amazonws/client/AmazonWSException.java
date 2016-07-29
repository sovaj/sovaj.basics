package org.sovaj.basics.amazonws.client;

/**
 * Created with IntelliJ IDEA.
 * User: ZOuarab1
 * Date: 11/4/13
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class AmazonWSException extends  Exception {


    public AmazonWSException() {
    }

    public AmazonWSException(String message) {
        super(message);
    }

    public AmazonWSException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmazonWSException(Throwable cause) {
        super(cause);
    }


}
