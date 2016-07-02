package org.mdubois.basics.core.utlis;

import java.util.Date;
import java.util.Map;
import org.springframework.integration.jms.JmsHeaders;

public class MessageHeaderExtractorTool {

    public static final String APPLICATION_ID = "ApplicationId";

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

    public String getApplicationId() {
        return (String) headerMap.get(APPLICATION_ID);
    }

    public String getJmsMessageId() {
        return (String) headerMap.get(JmsHeaders.MESSAGE_ID);
    }

    public String getTrackingId() {
        Object obj = headerMap.get(JmsHeaders.CORRELATION_ID);
        return obj != null ? obj.toString() : null;
    }

}
