package io.sovaj.basics.jms.utlis;

import java.util.Date;
import java.util.Map;

public class MessageHeaderExtractorTool {

    public static final String MESSAGE_ID = "jms_messageId";
    public static final String CORRELATION_ID = "jms_correlationId";
    public static final String REPLY_TO = "jms_replyTo";
    public static final String REDELIVERED = "jms_redelivered";
    public static final String TYPE = "jms_type";
    public static final String TIMESTAMP = "jms_timestamp";
    private final Map<String, Object> headerMap;

    public MessageHeaderExtractorTool(Map<String, Object> headerMap) {
        this.headerMap = headerMap;
    }

    public Date getJmsTimestamp() {
        Long timesStamp = (Long) headerMap.get(JmsHeaders.TIMESTAMP);
        return timesStamp != null ? new Date(timesStamp) : null;
    }

    public boolean getJmsRedelivered() {
        return (Boolean) headerMap.get(JmsHeaders.REDELIVERED);
    }

    public String getJmsReplyTo() {
        return (String) headerMap.get(JmsHeaders.REPLY_TO);
    }

    public String getType() {
        return (String) headerMap.get(JmsHeaders.TYPE);
    }

    public String getJmsMessageId() {
        return (String) headerMap.get(JmsHeaders.MESSAGE_ID);
    }

    public String getTrackingId() {
        Object obj = headerMap.get(JmsHeaders.CORRELATION_ID);
        return obj != null ? obj.toString() : null;
    }

}
