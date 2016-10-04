package io.sovaj.basics.core.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Convert a class another class. This abstract class implement the list part.
 *
 * @author Mickael Dubois
 * @param <S> The source class
 * @param <T> The target class
 */
public abstract class SetConverter<S, T> extends BaseConverter<S, T> implements CollectionConverter<S, T>  {

    @Override
    public Set<T> convert(Collection<S> source) {
        if (source == null || source.isEmpty()) {
            return new HashSet<>(0);
        } else {
            Set<T> result = new HashSet<>(source.size());
            for (S s : source) {
                result.add(this.convert(s));
            }
            return result;
        }
    }
}
