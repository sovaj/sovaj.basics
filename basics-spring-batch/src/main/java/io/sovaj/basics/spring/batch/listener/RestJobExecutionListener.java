package io.sovaj.basics.spring.batch.listener;

import io.sovaj.basics.core.utlis.SystemTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 *
 * @author Mickael Dubois
 */
public class RestJobExecutionListener implements JobExecutionListener, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestJobExecutionListener.class);

    private static final Date NOW = SystemTime.asDate();
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final DateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

    /**
     * The endpoint url find in #{jobParameters['output.endpoint.url']}
     */
    private String endpoinUrl;
    public final static String endpoinUrlJobParameterName = "output.endpoint.url";

    /**
     * The init path resource find in #{jobParameters['output.endpoint.initpath']}
     */
    private String initPath;
    public final static String initPathJobParameterName = "output.endpoint.initpath";
    /**
     * The destroy path resource find in #{jobParameters['output.endpoint.destroypath']}
     */
    private String destroyPath;
    public final static String destroyPathJobParameterName = "output.endpoint.destroypath";

    /**
     * The media type to use find in #{jobParameters['output.format']}
     */
    private MediaType outputFormat;
    public final static String outputFormatJobParameterName = "output.format";

    /**
     * The provider to use to serialize the item.
     */
    private List<MessageBodyWriter> providers;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.providers);
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        init(jobExecution);
        
        if (StringUtils.trimToNull(initPath) != null) {
            LOGGER.info("Calling init method on " + endpoinUrl + initPath);
            WebClient client = WebClient.create(endpoinUrl, providers);
            client = client.accept(outputFormat).type(outputFormat).path(initPath);
            client.header("Date", SDF.format(NOW));
            Response response = client.post(null);
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Batch fail to send data to : " + endpoinUrl + initPath, new RuntimeException(response.getStatusInfo().getReasonPhrase()));
            }
        } else {
            LOGGER.info("No init method called ");
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (StringUtils.trimToNull(destroyPath) != null) {
            LOGGER.info("Calling destroy method on " + endpoinUrl + initPath);
            WebClient client = WebClient.create(endpoinUrl, providers);
            client = client.accept(outputFormat).type(outputFormat).path(destroyPath);
            client.header("Date", SDF.format(NOW));
            Response response = client.post(null);
            if (response.getStatus() >= 400) {
                throw new RuntimeException("Batch fail to send data to : " + endpoinUrl + initPath, new RuntimeException(response.getStatusInfo().getReasonPhrase()));
            }
        } else {
            LOGGER.info("No destroy method called ");
        }
    }

    public String getEndpoinUrl() {
        return endpoinUrl;
    }

    public void setEndpoinUrl(String endpoinUrl) {
        this.endpoinUrl = endpoinUrl;
    }

    public String getInitPath() {
        return initPath;
    }

    public void setInitPath(String initPath) {
        this.initPath = initPath;
    }

    public String getDestroyPath() {
        return destroyPath;
    }

    public void setDestroyPath(String destroyPath) {
        this.destroyPath = destroyPath;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = MediaType.valueOf(outputFormat);
    }

    public List<MessageBodyWriter> getProviders() {
        return providers;
    }

    public void setProviders(List<MessageBodyWriter> providers) {
        this.providers = providers;
    }

    private void init(JobExecution jobExecution) {
        setEndpoinUrl(jobExecution.getJobParameters().getString(endpoinUrlJobParameterName, null));
        setDestroyPath(jobExecution.getJobParameters().getString(destroyPathJobParameterName, null));
        setInitPath(jobExecution.getJobParameters().getString(initPathJobParameterName, null));
        setOutputFormat(jobExecution.getJobParameters().getString(outputFormatJobParameterName, null));
        Assert.notNull(this.endpoinUrl);
        Assert.notNull(this.outputFormat);
    }

}
