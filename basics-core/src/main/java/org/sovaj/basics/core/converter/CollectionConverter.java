package org.sovaj.basics.core.converter;

import java.util.Collection;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert a class another class
 *
 * @author Mickael Dubois
 * @param <S> The source class of the conversion
 * @param <T> The target class of the conversion
 */
public interface CollectionConverter<S, T> extends Converter<S, T> {

    Collection<T> convert(Collection<S> source);
}
