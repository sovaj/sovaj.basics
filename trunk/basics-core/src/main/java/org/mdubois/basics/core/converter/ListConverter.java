package org.mdubois.basics.core.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Convert a class another class. This abstract class implement the list part.
 *
 * @author Mickael Dubois
 * @param <S> The source class
 * @param <T> The target class
 */
public abstract class ListConverter<S, T> extends BaseConverter<S, T> implements CollectionConverter<S, T> {

    @Override
    public List<T> convert(Collection<S> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>(0);
        } else {
            List<T> result = new ArrayList<>(source.size());
            for (S s : source) {
                result.add(this.convert(s));
            }
            return result;
        }
    }
}
