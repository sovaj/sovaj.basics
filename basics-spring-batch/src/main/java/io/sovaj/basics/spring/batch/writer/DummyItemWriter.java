package io.sovaj.basics.spring.batch.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 *
 * @author Mina Dawoud
 * @param <T> The type of element
 */
public class DummyItemWriter<T> implements ItemWriter<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyItemWriter.class);
    private final boolean debugEnabled = LOGGER.isDebugEnabled();

    @Override
    public void write(List<? extends T> items) throws Exception {

        if (debugEnabled) {
            LOGGER.debug("Writer has no operations for items {}", items);
        }
    }

}
