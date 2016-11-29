package io.sovaj.basics.mongo;

import io.sovaj.basics.mongo.domain.BusinessObjectHistorized;
import io.sovaj.basics.mongo.domain.VersionedBusinessObject;

/**
 *
 * @author mdubois2
 * @param <T>
 */
public abstract class VersionedMongoDAO<T extends VersionedBusinessObject> extends MongoDAO<T>{
    
    public String getVersionedCollection(){
        return getCollection()+ ".history";
    }
    
    @Override
    public void create(T p) {
        p.setVersion(1L);
        super.create(p);
    }
    

    @Override
    public void update(final T  p) {
        T oldVersion = this.getById(p.getId());
        p.setId(oldVersion.getId());
        p.setVersion(oldVersion.getVersion() + 1);
        super.update(p);
        this.mongoOps.save(new BusinessObjectHistorized(oldVersion), getVersionedCollection());
    }

    
}