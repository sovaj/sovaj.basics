package io.sovaj.basics.spring.batch.writer;

import io.sovaj.basics.core.utlis.SystemTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * {@link ItemWriter} support for Restfull service.
 *
 * This {@link ItemWriter} will send the item using a REST POST. We will send
 * the date of the call in the Date HTTP Header.
 *
 * A parameter will allow you to define if you want to send the item one at a
 * time or directly a list of item. The default behavior is to send the list
 * (for obvious performance reason)
 *
 * @author Mickael Dubois
 * @param <T> The object to send throw the wire
 */
public class RestItemWriter<T> implements ItemWriter<T>, InitializingBean {

    /**
     * Define the content to use to serialize the item.
     */
    private MediaType outputFormat;

    /**
     * The endpoint UTL of the Rest Service.
     */
    private String endpoinUrl;

    /**
     * The path of the rest service.
     */
    private String path;

    /**
     * The provider to use to serialize the item.
     */
    private List<MessageBodyWriter<T>> providers;

    /**
     * Either you want to send item one at the time or as a list.
     */
    private boolean singleCommit = false;

    /**
     * For logging purpose
     */
    private static final AtomicLong nbElementSent = new AtomicLong(0);
    private boolean debugEnabled;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.endpoinUrl);
        Assert.notNull(this.path);
        Assert.notNull(this.providers);
        Assert.notNull(this.outputFormat);
        debugEnabled = LOGGER.isDebugEnabled();
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        WebClient client = WebClient.create(endpoinUrl, providers);
        client = client.accept(outputFormat).type(outputFormat).path(path);
        client.header("Date", SDF.format(SystemTime.asDate()));
        if (singleCommit) {
            for (T item : items) {
                Response response = client.post(item);
                if (response.getStatus() >= 400) {
                    throw new RuntimeException("Batch fail to send data to :  " + endpoinUrl + path, new RuntimeException(response.getStatus() + " error code thrown by the service"));
                }
            }
        } else {
            Response response = client.post(items);
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Batch fail to send data to :  " + endpoinUrl + path, new RuntimeException(response.getStatus() + " error code thrown by the service"));
            }
        }
        if (debugEnabled) {
            LOGGER.debug("{} element(s) sent so far ", nbElementSent.addAndGet(items.size()));
        }
    }

    public void send(List<? extends T> items) {

    }

    public String getEndpoinUrl() {
        return endpoinUrl;
    }

    public void setEndpoinUrl(String endpoinUrl) {
        this.endpoinUrl = endpoinUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<MessageBodyWriter<T>> getProviders() {
        return providers;
    }

    public void setProviders(List<MessageBodyWriter<T>> providers) {
        this.providers = providers;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = MediaType.valueOf(outputFormat);
    }

    public boolean isSingleCommit() {
        return singleCommit;
    }

    public void setSingleCommit(boolean singleCommit) {
        this.singleCommit = singleCommit;
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
    private static final Logger LOGGER = LoggerFactory.getLogger(RestItemWriter.class);
}
