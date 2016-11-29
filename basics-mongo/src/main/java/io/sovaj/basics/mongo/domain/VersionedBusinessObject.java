package io.sovaj.basics.mongo.domain;

/**
 * Created by PFernan1 on 2016/Nov/20.
 */
public abstract class VersionedBusinessObject extends BusinessObject {

    private Long version;

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion() {
        return version;
    }
    
}
