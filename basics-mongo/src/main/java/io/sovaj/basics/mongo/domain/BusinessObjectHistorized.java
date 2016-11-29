package io.sovaj.basics.mongo.domain;

/**
 *
 * @author Mickael Dubois
 */
public class BusinessObjectHistorized<T> extends BusinessObject {

    private T historizedObject;
    
    public BusinessObjectHistorized(T historizedObject) {
        this.historizedObject = historizedObject;
    }
    
    public void setHistorizedObject(T historizedObject) {
        this.historizedObject = historizedObject;
    }
    
    public T setHistorizedObject() {
        return this.historizedObject;
    }

}
