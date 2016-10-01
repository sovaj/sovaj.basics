package org.sovaj.basics.spring.batch.listener;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A listener who puts at disposal a LOGGER phase (job, step, chunk, � read,
 * process, write). Default � must loggers are built on the model � following :
 * <code>getClass().getName() + '.' + getClass().getSimpleName() + '#' + phase</code>
 * .<br>
 * It is possible to add phases and change the names of loggers � for each
 * phase.
 *
 * @author Fran�ois Lecomte
 *
 */
public abstract class AbstractLoggingExecutionListener {

    /**
     * {@link Map} des noms des LOGGERs
     */
    private Map<String, String> loggerNames = new HashMap<>();

    /**
     * {@link Map} des {@link Logger}
     */
    private final Map<String, Logger> loggers = new HashMap<>();

    /**
     * {@link Logger}
     */
    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * Constructeur
     */
    public AbstractLoggingExecutionListener() {
        String loggerPrefix = getClass().getName() + '.' + getClass().getSimpleName();
        setLoggerName("job", loggerPrefix + "#job");
        setLoggerName("step", loggerPrefix + "#step");
        setLoggerName("chunk", loggerPrefix + "#chunk");
        setLoggerName("read", loggerPrefix + "#read");
        setLoggerName("process", loggerPrefix + "#process");
        setLoggerName("write", loggerPrefix + "#write");
    }

    /**
     * @param phase
     * @param loggerName
     */
    private void setLoggerName(String phase, String loggerName) {
        loggerNames.put(phase, loggerName);
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getJobLogger() {
        return getLogger("job");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getStepLogger() {
        return getLogger("step");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getChunkLogger() {
        return getLogger("chunk");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getReadLogger() {
        return getLogger("read");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getProcessLogger() {
        return getLogger("process");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getWriteLogger() {
        return getLogger("write");
    }

    /**
     * @param phase - The phase
     * @return {@link Logger}
     */
    protected Logger getLogger(String phase) {
        Logger logger = loggers.get(phase);
        if (logger == null) {
            synchronized (phase) {
                logger = loggers.get(phase);
                if (logger == null) {
                    String loggerName = loggerNames.get(phase);
                    if (loggerName == null) {
                        loggerName = getClass().getName() + '.' + getClass().getSimpleName() + '#' + phase;
                        this.LOGGER
                                .warn("No 'loggerName' found for phase '{}' ; using default : {}", phase,
                                        loggerName);
                    }
                    logger = LoggerFactory.getLogger(loggerName);
                    loggers.put(phase, logger);
                }
            }
        }
        return logger;
    }

    /**
     * @param loggerNames the LOGGERNames to set
     */
    public void setLoggerNames(Map<String, String> loggerNames) {
        this.loggerNames = loggerNames;
    }
}
