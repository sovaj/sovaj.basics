package org.sovaj.basics.spring.batch.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 *
 * @author Mickael Dubois
 */
public class MultiThreadedJdbcItemReader<T> implements ItemReader<T>, InitializingBean, BeanNameAware {

    /**
     * {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadedJdbcItemReader.class);

    /**
     * {@link DataSource}
     */
    private DataSource dataSource;

    /**
     * SQL string
     */
    private String sql;

    /**
     * SQL string
     */
    private String fileName;

    /**
     * {@link RowMapper}
     */
    private RowMapper<T> rowMapper;

    /**
     * {@link PreparedStatementSetter}
     */
    private PreparedStatementSetter preparedStatementSetter;

    /**
     * {@link JdbcTemplate}
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * Permet d'activer le log des erreurs.
     */
    private boolean logErrors = true;

    /**
     * Queue locale des r�sultats
     */
    private final ThreadLocal<ResultsHolder<T>> resultsHolderTL = new ThreadLocal<ResultsHolder<T>>();

    /**
     * Liste des resultsHolders utilis�es
     */
    private final List<ResultsHolder<T>> resultsHolders = new ArrayList<ResultsHolder<T>>();

    /**
     * Disabled flag
     */
    private boolean disabled = false;

    /**
     * setting threadIsolation flag to true will disable threads looking for results from other thread queues.
     */
    private boolean threadIsolation = false;

    /**
     * Bean name
     */
    private String beanName;

    /**
     * Check mandatory properties.
     *
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.dataSource);
        Assert.isTrue(!(this.sql != null && this.fileName != null),
                "La propri�t� sql ou fileName doit �tre renseign� mais pas les deux en m�me temps");
        Assert.isTrue(this.sql != null || this.fileName != null, "La propri�t� sql ou fileName doit �tre renseign�");
        if (fileName != null) {
            final String codeSql = sqlFileToString();
            setSql(codeSql);
        }
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * {@inheritDoc}
     */
    public T read() throws Exception {
        if (disabled) {
            LOGGER.info("Reader {} is disabled --> returning null", beanName);
            return null;
        }

        // get next local result
        T result = localGet();
        if (result != null) {
            return result;
        }

        if (!threadIsolation) {
            // local resultsHolder is empty --> let's get some results from
            // other resultsHolders
            result = globalGet();
        }

        return result;
    }

    /**
     * @return @throws Exception
     */
    private T localGet() throws Exception {

        final boolean debugEnabled = LOGGER.isDebugEnabled();

        // get local results holder
        ResultsHolder<T> resultsHolder = this.resultsHolderTL.get();
        if (resultsHolder == null) {
            // creation of a new resultsHolder
            resultsHolder = new ResultsHolder<T>();
            this.resultsHolderTL.set(resultsHolder);
            synchronized (this.resultsHolders) {
                this.resultsHolders.add(resultsHolder);
            }
            if (debugEnabled) {
                LOGGER.debug("ResultsHolder {} created", resultsHolder);
            }

        } else if (resultsHolder.exhausted) {
            if (debugEnabled) {
                // no more results to get from this resultsHolder nor from db
                LOGGER.debug("ResultsHolder {} exhausted ; returning null", resultsHolder);
            }
            return null;
        }

        if (debugEnabled) {
            // resultsHolder might be polled by other threads --> let's
            // synchronize
            LOGGER.debug("Locally polling resultsHolder {}...", resultsHolder);
        }
        synchronized (resultsHolder) {

            // we may find some results there
            T result = resultsHolder.queue.poll();
            if (debugEnabled) {
                LOGGER.debug("Locally polled resultsHolder {} : {}", resultsHolder, result);
            }
            if (result == null) {
                // no results in local queue
                // let's get some objects from db
                final List<T> results = dbRead();

                if (results.isEmpty()) {
                    // no more data to read from db --> let's set our results
                    // holder as exhausted
                    resultsHolder.exhausted = true;
                    synchronized (this.resultsHolders) {
                        this.resultsHolders.remove(resultsHolder);
                    }
                    if (debugEnabled) {
                        LOGGER.debug("ResultsHolder {} has been set exhausted", resultsHolder);
                    }
                } else {
                    // enqueue results
                    resultsHolder.queue.addAll(results);

                    // get next enqueued result
                    result = resultsHolder.queue.poll();
                    if (debugEnabled) {
                        LOGGER.debug("Returning locally cached result {}", result);
                    }
                }
            }
            return result;
        }
    }

    /**
     * local read ; if queue is still empty, let's fetch results from database
     *
     * @param queue
     * @return
     * @throws Exception
     */
    private List<T> dbRead() throws Exception {
        // let's fetch some data from database
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Jdbc read : {}", sql);
            }
            List<T> results = null;
            long start = 0;
            if (LOGGER.isInfoEnabled()) {
                start = System.currentTimeMillis();
            }
            results = jdbcTemplate.query(sql, preparedStatementSetter, rowMapper);

            if (LOGGER.isInfoEnabled()) {
                final long stop = System.currentTimeMillis();
                LOGGER.info("Jdbc read took " + (stop - start) + " ms");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Jdbc read ok ({} results)", results.size());
            }
            return results;

        } catch (final Exception e) {
            if (logErrors) {
                LOGGER.error("Read error", e);
            }
            throw e;
        }
    }

    /**
     * @return a result from any {@link ResultsHolder}
     */
    private T globalGet() {
        ArrayList<ResultsHolder<T>> resultsHoldersCopy;
        synchronized (resultsHolders) {
            resultsHoldersCopy = new ArrayList<ResultsHolder<T>>(resultsHolders);
        }
        final boolean debugEnabled = LOGGER.isDebugEnabled();
        if (debugEnabled) {
            LOGGER.debug("Getting a result from all resultsHolders...");
        }

        // let's find out a result in resultsHolders
        for (final ResultsHolder<T> resultsHolder : resultsHoldersCopy) {
            if (resultsHolder.exhausted) {
                // this resultsHolder is exhausted
                // (it will be removed from resultsHolders list soon
                continue;
            }

            if (debugEnabled) {
                LOGGER.debug("Globally polling resultHolders {}...", resultsHolder);
            }
            synchronized (resultsHolder) {

                // let's poll from this resultsHolder
                final T result = resultsHolder.queue.poll();
                if (result != null) {
                    if (debugEnabled) {
                        LOGGER.debug("Globally polled resultHolders {} : {}", resultsHolder, result);
                    }
                    // get it !
                    return result;
                }
            }
        }

        if (debugEnabled) {
            LOGGER.debug("No results found from any resultHolders ; cleaning...");
        }
        // no results found from any resultsHolder
        // let's cleanup resultsHolders list & tl
        final ResultsHolder<T> resultsHolder = resultsHolderTL.get();
        resultsHolderTL.remove();
        synchronized (resultsHolders) {
            resultsHolders.remove(resultsHolder);
        }
        if (debugEnabled) {
            LOGGER.debug("Cleaned out");
        }
        return null;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }

    /**
     * @param rowMapper the rowMapper to set
     */
    public void setRowMapper(final RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * @param preparedStatementSetter the preparedStatementSetter to set
     */
    public void setPreparedStatementSetter(final PreparedStatementSetter preparedStatementSetter) {
        this.preparedStatementSetter = preparedStatementSetter;
    }

    /**
     * @param logErrors the logErrors to set
     */
    public void setLogErrors(final boolean logErrors) {
        this.logErrors = logErrors;
    }

    /**
     * @return the logErrors
     */
    public boolean isLogErrors() {
        return logErrors;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    public String sqlFileToString() {

        String codeSql = StringUtils.EMPTY;

        try {
            final InputStream inputStream = getClass().getResourceAsStream(fileName);
            final StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            codeSql = writer.toString();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return codeSql;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if (beanName == null) {
            return super.toString();
        } else {
            return ClassUtils.getShortName(getClass()) + ": [name=" + beanName + "]";
        }
    }

    /**
     * @author Fran�ois Lecomte
     * @param <T>
     */
    private static class ResultsHolder<T> {

        private final Queue<T> queue;

        private boolean exhausted;

        /**
         * Constructeur
         */
        public ResultsHolder() {
            queue = new ConcurrentLinkedQueue<T>();
        }
    }

    public boolean isThreadIsolation() {
        return threadIsolation;
    }

    public void setThreadIsolation(boolean threadIsolation) {
        this.threadIsolation = threadIsolation;
    }
}
