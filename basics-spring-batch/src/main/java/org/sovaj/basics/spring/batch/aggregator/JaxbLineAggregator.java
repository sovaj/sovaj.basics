package org.sovaj.basics.spring.batch.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 *
 * @author Mickael Dubois
 * @param <T>
 */
public class JaxbLineAggregator<T> implements LineAggregator<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxbLineAggregator.class);

    private MessageBodyWriter<T> provider;

    private MediaType outputFormat;

    private Class<?> classToBeBound;

    private String encoding;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(provider);
        Assert.notNull(outputFormat);
        Assert.notNull(classToBeBound);
    }

    @Override
    public String aggregate(T item) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            provider.writeTo(item, classToBeBound, null, null, outputFormat, null, baos);
            return encoding == null ? baos.toString() : baos.toString(encoding);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    public MessageBodyWriter<T> getProvider() {
        return provider;
    }

    public void setProvider(MessageBodyWriter<T> provider) {
        this.provider = provider;
    }

    public void setOutputFormat(String ouputFormat) {
        try {
            this.outputFormat = MediaType.valueOf(ouputFormat);
        } catch (IllegalArgumentException e) {
            LOGGER.error(String.format("Unable to create JaxbLineAggregator with outpout format{}", outputFormat), e);
            throw e;
        }
    }

    /**
     * Set the list of Java classes to be recognized by a newly created
     * JAXBContext.
     * <p>
     * Setting either this property, {@link #setContextPath "contextPath"} or
     * {@link #setPackagesToScan "packagesToScan"} is required.
     */
    public void setClassToBeBound(Class<?> classesToBeBound) {
        this.classToBeBound = classesToBeBound;
    }

    /**
     * Return the list of Java classes to be recognized by a newly created
     * JAXBContext.
     */
    public Class<?> getClassToBeBound() {
        return this.classToBeBound;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
