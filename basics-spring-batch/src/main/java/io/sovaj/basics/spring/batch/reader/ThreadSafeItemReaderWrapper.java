package io.sovaj.basics.spring.batch.reader;

/**
 * @author Mickael Dubois
 * @param <T> The type of element
 */
public class ThreadSafeItemReaderWrapper<T> extends DisableableItemReader<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized T read() throws Exception {
        return super.read();
    }

}
