package org.mdubois.basics.spring.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 
 * @author Mickael Dubois
 * @param <T>
 */
public class SynchronizedItemReader<T> implements ItemReader<T>, ItemStream {

    private ItemReader<T> delegate;

    /**
     * {@inheritDoc}
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    public synchronized T read() throws Exception, UnexpectedInputException, ParseException,
                    NonTransientResourceException {
        return delegate.read();
    }

    /**
     * {@inheritDoc}
     * @param executionContext
     */
    @Override
    public void open(final ExecutionContext executionContext) throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream ) this.delegate).open(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final ExecutionContext executionContext) throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream ) this.delegate).update(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws ItemStreamException {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream ) this.delegate).close();
        }
    }

    public ItemReader<T> getDelegate() {
        return delegate;
    }

    public void setDelegate(final ItemReader<T> delegate) {
        this.delegate = delegate;
    }
}
