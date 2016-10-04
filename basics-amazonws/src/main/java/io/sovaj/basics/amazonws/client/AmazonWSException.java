package io.sovaj.basics.amazonws.client;

/**
 * 
 * @author Mickael Dubois
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
