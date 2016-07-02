package org.mdubois.basics.spring.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

/**
 * {@link ItemProcessor} delegate transformation to a {@link Converter}
 *
 * Simple delegation of the translation to a spring converter.
 * 
 * @author Mickael Dubois
 * @param <S> The object source
 * @param <D> The object destination
 */
public class ConverterDelegateProcessor<S, D> implements ItemProcessor<S, D>, InitializingBean {

    /**
     * The {@link Converter} to use.
     */
    private Converter<S,D> delageteConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(delageteConverter);
    }

    @Override
    public D process(S item) throws Exception {
        return delageteConverter.convert(item);
    }

    public Converter getDelageteConverter() {
        return delageteConverter;
    }

    public void setDelageteConverter(Converter delageteConverter) {
        this.delageteConverter = delageteConverter;
    }

}
