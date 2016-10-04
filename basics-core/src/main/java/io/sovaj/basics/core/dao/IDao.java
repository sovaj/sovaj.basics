package io.sovaj.basics.core.dao;

import java.io.Serializable;

/**
 *
 * @author Mickael Dubois
 * @param <T> The business object
 * @param <PK> The database key
 */
public interface IDao<T, PK extends Serializable> extends IReadOnlyDao<T, PK> {

    PK save(T newInstance);

    void persist(T newInstance);

    void update(T detachedInstance);

    void saveOrUpdate(T transientObject);

    void flush();
}
