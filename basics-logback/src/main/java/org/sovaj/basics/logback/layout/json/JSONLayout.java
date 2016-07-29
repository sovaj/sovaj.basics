package org.sovaj.basics.logback.layout.json;

import java.util.Map;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.pattern.MDCConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.html.DefaultThrowableRenderer;
import ch.qos.logback.core.html.IThrowableRenderer;
import ch.qos.logback.core.pattern.Converter;
import java.util.HashMap;

public class JSONLayout extends JSONLayoutBase<ILoggingEvent> {

    public static final Map<String , String> converterMap  = new HashMap<>(PatternLayout.defaultConverterMap);
    
    static {
        converterMap.put("m", MessageConverter.class.getName());
        converterMap.put("msg", MessageConverter.class.getName());
        converterMap.put("message", MessageConverter.class.getName());
    }

    /**
     * Default pattern string for log output.
     */
    static final String DEFAULT_CONVERSION_PATTERN = "%date%thread%level%logger%msg";

    IThrowableRenderer<ILoggingEvent> throwableRenderer;

    private String prefixList = "";

    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     *
     * The default pattern just produces the application supplied message.
     */
    public JSONLayout() {
        pattern = DEFAULT_CONVERSION_PATTERN;
        throwableRenderer = new DefaultThrowableRenderer();
    }

    @Override
    public void start() {
        int errorCount = 0;
        if (throwableRenderer == null) {
            addError("ThrowableRender cannot be null.");
            errorCount++;
        }
        if (errorCount == 0) {
            super.start();
        }
    }

    @Override
    protected Map<String, String> getDefaultConverterMap() {
        return converterMap;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        StringBuilder buf = new StringBuilder();

        buf.append(prefixList);
        prefixList = ",";
        buf.append("{");

        Converter<ILoggingEvent> c = head;
        String prefix = "";
        while (c != null) {
            buf.append(prefix);
            prefix = ",";
            buf.append("\"");
            buf.append(computeConverterName(c));
            buf.append("\" : \"");
            c.write(buf, event);
            buf.append("\"");

            c = c.getNext();
        }
        buf.append("}");

        if (event.getThrowableProxy() != null) {
            throwableRenderer.render(buf, event);
        }
        return buf.toString();
    }

    public IThrowableRenderer getThrowableRenderer() {
        return throwableRenderer;
    }

    public void setThrowableRenderer(IThrowableRenderer<ILoggingEvent> throwableRenderer) {
        this.throwableRenderer = throwableRenderer;
    }

    @Override
    protected String computeConverterName(Converter c) {
        if (c instanceof MDCConverter) {
            MDCConverter mc = (MDCConverter) c;
            String key = mc.getFirstOption();
            if (key != null) {
                return key;
            } else {
                return "MDC";
            }
        } else {
            return super.computeConverterName(c);
        }
    }
}
