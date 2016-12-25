package io.sovaj.basics.jms.utlis;

/**
 *
 * @author Mickael Dubois
 */
public class JmsHeaders {
    
    public static final String MESSAGE_ID = "jms_messageId";
    public static final String CORRELATION_ID = "jms_correlationId";
    public static final String REPLY_TO = "jms_replyTo";
    public static final String REDELIVERED = "jms_redelivered";
    public static final String TYPE = "jms_type";
    public static final String TIMESTAMP = "jms_timestamp";

    private JmsHeaders() {
    }
    
    
}
