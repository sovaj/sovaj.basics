package org.sovaj.basics.spring.batch.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 *
 * @author François Lecomte
 * @param <T> The type of element
 */
public class DisableableItemReader<T> extends ItemReaderWrapper<T> {

    /**
     * {@link Logger}
     */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Disabled flag
     */
    private boolean disabled = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (disabled) {
            LOGGER.info("Reader is disabled ; skip open");
        } else {
            super.open(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     * @return T
     * @throws java.lang.Exception In case of error
     */
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (disabled) {
            LOGGER.info("Reader is disabled ; skip read");
            return null;
        }

        // execute
        return super.read();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws ItemStreamException {
        if (disabled) {
            LOGGER.info("Reader is disabled ; skip close");
        } else {
            super.close();
        }
    }

    /**
     * @param disable - to disable or not
     */
    public void setDisabled(boolean disable) {
        this.disabled = disable;
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }
}
