package io.sovaj.basics.logback.layout.json;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Mickael Dubois
 */
public class MessageConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return new String(Base64.encodeBase64(event.getFormattedMessage().getBytes()));
    }

}
