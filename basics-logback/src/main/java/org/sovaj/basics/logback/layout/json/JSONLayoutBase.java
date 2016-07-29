package org.sovaj.basics.logback.layout.json;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.ConverterUtil;
import ch.qos.logback.core.pattern.parser.Node;
import ch.qos.logback.core.pattern.parser.Parser;
import ch.qos.logback.core.spi.ScanException;

public abstract class JSONLayoutBase<E> extends LayoutBase<E> {

    protected String pattern;

    protected Converter<E> head;

    /**
     * Set the <b>ConversionPattern </b> option. This is the string which
     * controls formatting and consists of a mix of literal content and
     * conversion specifiers.
     *
     * @param conversionPattern
     */
    public void setPattern(String conversionPattern) {
        pattern = conversionPattern;
    }

    /**
     * Returns the value of the <b>ConversionPattern </b> option.
     *
     * @return
     */
    public String getPattern() {
        return pattern;
    }

    @Override
    public void start() {
        int errorCount = 0;

        try {
            Parser<E> p = new Parser<>(pattern);
            p.setContext(getContext());
            Node t = p.parse();
            this.head = p.compile(t, getEffectiveConverterMap());
            ConverterUtil.startConverters(this.head);
        } catch (ScanException ex) {
            addError("Incorrect pattern found", ex);
            errorCount++;
        }

        if (errorCount == 0) {
            super.started = true;
        }
    }

    protected abstract Map<String, String> getDefaultConverterMap();

    /**
     * Returns a map where the default converter map is merged with the map
     * contained in the context.
     *
     * @return
     */
    public Map<String, String> getEffectiveConverterMap() {
        Map<String, String> effectiveMap = new HashMap<>();

        // add the least specific map fist
        Map<String, String> defaultMap = getDefaultConverterMap();
        if (defaultMap != null) {
            effectiveMap.putAll(defaultMap);
        }

        // contextMap is more specific than the default map
        Context lContext = getContext();
        if (lContext != null) {
            @SuppressWarnings("unchecked")
            Map<String, String> contextMap = (Map<String, String>) lContext
                    .getObject(CoreConstants.PATTERN_RULE_REGISTRY);
            if (contextMap != null) {
                effectiveMap.putAll(contextMap);
            }
        }
        return effectiveMap;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public String getFileHeader() {
        return "";
    }

    @Override
    public String getPresentationHeader() {
        return "[";
    }

    @Override
    public String getPresentationFooter() {
        return "]";
    }

    @Override
    public String getFileFooter() {
        return "";
    }

    protected String computeConverterName(Converter c) {
        String className = c.getClass().getSimpleName();
        int index = className.indexOf("Converter");
        if (index == -1) {
            return className;
        } else {
            return className.substring(0, index);
        }
    }

}
