package io.sovaj.basics.hibernate.dao;

import io.sovaj.basics.core.dao.IDao;
import java.io.Serializable;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

public class CMSHibernateGenericDAO<T, PK extends Serializable> implements IDao<T, PK> {

    private final Class< ? extends T> persistentClass;

    /**
     * {@link SessionFactory}
     */
    private SessionFactory sessionFactory;

    public CMSHibernateGenericDAO(Class<? extends T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public CMSHibernateGenericDAO(SessionFactory sessionFactory, Class<? extends T> persistentClass) {
        this(persistentClass);
        setSessionFactory(sessionFactory);
    }

    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Class< ? extends T> getType() {
        return persistentClass;
    }

    @Override
    public T load(PK id) {
        return (T) getSessionFactory().getCurrentSession().get(getType(), id);
    }

    @Override
    public PK save(T newInstance) {
        return (PK) getSessionFactory().getCurrentSession().save(newInstance);
    }

    @Override
    public void persist(T newInstance) {
        getSessionFactory().getCurrentSession().persist(newInstance);
    }

    @Override
    public void update(T o) {
        getSessionFactory().getCurrentSession().update(o);
    }

    @Override
    public void saveOrUpdate(T transientObject) {
        getSessionFactory().getCurrentSession().saveOrUpdate(transientObject);
    }

    @Override
    public void flush() {
        getSessionFactory().getCurrentSession().flush();
    }

    @Override
    public List<T> loadAll(int page, int size) {
        return getSessionFactory().getCurrentSession().createCriteria(getType()).setFirstResult((page - 1) * size).setMaxResults(size).list();
    }

    @Override
    public List<T> loadLike(int page, int size, T example) {
        return getSessionFactory().getCurrentSession().createCriteria(getType()).add(Example.create(example)).setFirstResult((page - 1) * size).setMaxResults(size).list();
    }

    @Override
    public Long count() {
        return (Long) getSessionFactory().getCurrentSession().createCriteria(getType()).setProjection(Projections.rowCount()).uniqueResult();
    }
}
