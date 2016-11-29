package io.sovaj.basics.mongo;

import io.sovaj.basics.mongo.domain.BusinessObject;


/**
 *
 * @author Mickael Dubois
 * @param <T>
 */
public abstract class MultitenantMongoDAO<T extends BusinessObject> extends MongoDAO<T> {

    abstract public String getDedicatedCollection();
    
    abstract public String getDomain();

    @Override
    public String getCollection() {
        return getDomain() + "." + getDedicatedCollection();
    }

}
