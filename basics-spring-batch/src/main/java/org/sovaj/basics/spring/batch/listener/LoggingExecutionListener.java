package org.sovaj.basics.spring.batch.listener;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * Polyvalent Spring batch listener for logging.
 * 
 * @author François Lecomte
 */
public class LoggingExecutionListener<T, S> extends AbstractLoggingExecutionListener implements StepExecutionListener,
                ChunkListener, ItemReadListener<T>, ItemProcessListener<T, S>, ItemWriteListener<S>, SkipListener<T, S> {


    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        Logger stepLogger = getStepLogger();
        if (stepLogger.isInfoEnabled()) {
            stepLogger.info("beforeStep ; {}", stepExecution);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        Logger stepLogger = getStepLogger();
        if (stepLogger.isInfoEnabled()) {
            stepLogger.info("afterStep ; {}", stepExecution);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterRead(T item) {
        Logger readLogger = getReadLogger();
        if (readLogger.isInfoEnabled()) {
            readLogger.info("afterRead ; {}", item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRead() {
        Logger readLogger = getReadLogger();
        if (readLogger.isInfoEnabled()) {
            readLogger.info("beforeRead");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReadError(Exception ex) {
        Logger readLogger = getReadLogger();
        if (readLogger.isInfoEnabled()) {
            readLogger.info("onReadError ; {}", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterWrite(List< ? extends S> items) {
        Logger writeLogger = getWriteLogger();
        if (writeLogger.isInfoEnabled()) {
            writeLogger.info("afterWrite ; {}", items);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeWrite(List< ? extends S> items) {
        Logger writeLogger = getWriteLogger();
        if (writeLogger.isInfoEnabled()) {
            writeLogger.info("beforeWrite ; {}", items);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWriteError(Exception exception, List< ? extends S> items) {
        Logger writeLogger = getWriteLogger();
        if (writeLogger.isInfoEnabled()) {
            writeLogger.info("onWriteError ; {} ; {}", items, exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterProcess(T item, S result) {
        Logger processLogger = getProcessLogger();
        if (processLogger.isInfoEnabled()) {
            processLogger.info("afterProcess ; {} ; {}", item, result);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeProcess(T item) {
        Logger processLogger = getProcessLogger();
        if (processLogger.isInfoEnabled()) {
            processLogger.info("beforeProcess ; {}", item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onProcessError(T item, Exception e) {
        Logger processLogger = getProcessLogger();
        if (processLogger.isWarnEnabled()) {
            processLogger.warn("onProcessError ; {} ; {}", item, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSkipInProcess(T item, Throwable t) {
        Logger processLogger = getProcessLogger();
        if (processLogger.isWarnEnabled()) {
            processLogger.warn("onSkipInProcess ; {} ; {}", item, t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSkipInRead(Throwable t) {
        Logger readLogger = getReadLogger();
        if (readLogger.isWarnEnabled()) {
            readLogger.warn("onSkipInRead ; {}", t);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSkipInWrite(S item, Throwable t) {
        Logger writeLogger = getWriteLogger();
        if (writeLogger.isWarnEnabled()) {
            writeLogger.warn("onSkipInWrite ; {} ; {}", item, t);
        }
    }

    @Override
    public void beforeChunk(ChunkContext context) {
          Logger chunkLogger = getChunkLogger();
        if (chunkLogger.isInfoEnabled()) {
            chunkLogger.info("beforeChunk");
        }
    }

    @Override
    public void afterChunk(ChunkContext context) {
         Logger chunkLogger = getChunkLogger();
        if (chunkLogger.isInfoEnabled()) {
            chunkLogger.info("afterChunk");
        }
    }

    @Override
    public void afterChunkError(ChunkContext context) {
         Logger chunkLogger = getChunkLogger();
        if (chunkLogger.isInfoEnabled()) {
            chunkLogger.info("afterChunkError");
        }
    }
}