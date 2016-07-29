package org.sovaj.basics.spring.batch.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 *
 * @param <T>
 * @author Mickael Dubois
 */
public class MultiThreadedHibernateItemReader<T> implements ItemReader<T>, InitializingBean, BeanNameAware {

    /**
     * {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiThreadedHibernateItemReader.class);

    /**
     * SQL string
     */
    private String sql;

    /**
     * SQL string
     */
    private String fileName;

    /**
     * {@link SessionFactory}
     */
    private SessionFactory sessionFactory;

    /**
     * {@link HibernatePrepareQuery}
     */
    private HibernatePrepareQuery hibernatePrepareQuery;

    /**
     * Queue locale des résultats
     */
    private final ThreadLocal<ResultsHolder<T>> resultsHolderTL = new ThreadLocal<>();

    /**
     * Liste des resultsHolders utilisées
     */
    private final List<ResultsHolder<T>> resultsHolders = new ArrayList<>();

    /**
     * Bean name
     */
    private String beanName;

    /**
     * Check mandatory properties.
     *
     * @throws java.lang.Exception
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.sessionFactory);
        Assert.notNull(this.hibernatePrepareQuery);
        Assert.isTrue(!(this.sql != null && this.fileName != null),
                "La propriété sql ou fileName doit être renseigné mais pas les deux en même temps");
        Assert.isTrue(this.sql != null || this.fileName != null, "La propriété sql ou fileName doit être renseigné");
        if (fileName != null) {
            final String codeSql = sqlFileToString();
            setSql(codeSql);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws java.lang.Exception
     */
    @Override
    public T read() throws Exception {
        // get next local result
        T result = localGet();
        if (result != null) {
            return result;
        }

        // local resultsHolder is empty --> let's get some results from
        // other resultsHolders
        result = globalGet();

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
            resultsHolder = new ResultsHolder<>();
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

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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

            long start = 0;
            if (LOGGER.isInfoEnabled()) {
                start = System.currentTimeMillis();
            }

            List<T> results = hibernatePrepareQuery.prepareQuery(sessionFactory.getCurrentSession().createQuery(sql))
                    .list();

            if (LOGGER.isInfoEnabled()) {
                final long stop = System.currentTimeMillis();
                LOGGER.info("Jdbc read took " + (stop - start) + " ms");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Jdbc read ok ({} results)", results.size());
            }
            return results;

        } catch (final HibernateException e) {
            LOGGER.error("Read error", e);
            throw e;
        }
    }

    /**
     * @return a result from any {@link ResultsHolder}
     */
    private T globalGet() {
        ArrayList<ResultsHolder<T>> resultsHoldersCopy;
        synchronized (resultsHolders) {
            resultsHoldersCopy = new ArrayList<>(resultsHolders);
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
     * @param sql the sql to set
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }

    /**
     * {@inheritDoc}
     *
     * @param name
     */
    @Override
    public void setBeanName(final String name) {
        this.beanName = name;
    }

    public String sqlFileToString() throws IOException {

        String codeSql = StringUtils.EMPTY;

        try {
            final InputStream inputStream = getClass().getResourceAsStream(fileName);
            final StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            codeSql = writer.toString();
        } catch (final IOException e) {
            throw e;
        }

        return codeSql;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public HibernatePrepareQuery getHibernatePrepareQuery() {
        return hibernatePrepareQuery;
    }

    public void setHibernatePrepareQuery(HibernatePrepareQuery hibernatePrepareQuery) {
        this.hibernatePrepareQuery = hibernatePrepareQuery;
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
     * @author François Lecomte
     * @param <T>
     */
    private static class ResultsHolder<T> {

        private final Queue<T> queue;

        private boolean exhausted;

        /**
         * Constructeur
         */
        public ResultsHolder() {
            queue = new ConcurrentLinkedQueue<>();
        }
    }
}
