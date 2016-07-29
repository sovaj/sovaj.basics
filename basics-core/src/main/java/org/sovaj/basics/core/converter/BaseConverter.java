
package org.sovaj.basics.core.converter;

import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Mickael Dubois
 */
public abstract class BaseConverter<S, T> implements Converter<S, T> {

    @Override
    public T convert(S source) {
        if(source != null){
            return doConvert(source);
        } else {
            return null;
        }
    }

    protected abstract T doConvert(S source) ;
    
}
