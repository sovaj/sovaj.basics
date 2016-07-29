package org.sovaj.basics.spring.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * 
 * @author Mickael Dubois
 * @param <T>
 */
public class ItemReaderWrapper<T> implements ItemStreamReader<T>, InitializingBean, BeanNameAware {

    /**
     * Delegate {@link ItemReader}
     */
    private ItemReader<T> itemReader;

    /**
     * Bean name
     */
    private String beanName;

    /**
     * {@inheritDoc}
     * @throws java.lang.Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(itemReader, "itemReader is mandatory");
    }

    /**
     * {@inheritDoc}
     * @param executionContext
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (itemReader instanceof ItemStream) {
            ((ItemStream ) itemReader).open(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (itemReader instanceof ItemStream) {
            ((ItemStream ) itemReader).update(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws ItemStreamException {
        if (itemReader instanceof ItemStream) {
            ((ItemStream ) itemReader).close();
        }
    }

    /**
     * {@inheritDoc}
     * @return 
     * @throws java.lang.Exception
     */
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return itemReader.read();
    }

    /**
     * @param itemReader the itemReader to set
     */
    public void setItemReader(ItemReader<T> itemReader) {
        this.itemReader = itemReader;
    }

    /**
     * {@inheritDoc}
     * @param name
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
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
}
