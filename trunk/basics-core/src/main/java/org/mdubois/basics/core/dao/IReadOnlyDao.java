package org.mdubois.basics.core.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mickael Dubois
 * @param <T> The business object
 * @param <PK> The database key
 *
 */
public interface IReadOnlyDao<T, PK extends Serializable> {

    public T load(PK id);

    public List<T> loadAll(int page, int size);

    public List<T> loadLike(int page, int size, T example);

    public Long count();
}
