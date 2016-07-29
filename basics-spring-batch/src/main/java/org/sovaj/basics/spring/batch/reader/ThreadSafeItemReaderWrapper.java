package org.sovaj.basics.spring.batch.reader;

import org.springframework.batch.item.ItemReader;

/**
 * @author Mickael Dubois
 * @param <T>
 */
public class ThreadSafeItemReaderWrapper<T> extends DisableableItemReader<T> {

    /**
     * Synchronized {@link ItemReader#read()}<br> {@inheritDoc}
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    public synchronized T read() throws Exception {
        return super.read();
    }

}
