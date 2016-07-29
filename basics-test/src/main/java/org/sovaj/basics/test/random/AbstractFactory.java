package org.sovaj.basics.test.random;


/**
 * Impl�mentation abstraite de {@link Factory}.
 * 

 * @param <O> Type de l'objet g�n�r�
 */
public abstract class AbstractFactory<O> implements Factory<O> {

    /**
     * {@link Class} de l'objet g�n�r�
     */
    private Class<O> objectClass;

    /**
     * Constructeur
     */
    public AbstractFactory() {
        super();
    }

    /**
     * Constructeur
     * 
     * @param clazz Type de l'objet g�n�r�
     */
    public AbstractFactory(Class<O> clazz) {
        super();
        this.objectClass = clazz;
    }

    /**
     * @return the objectClass
     */
    public Class<O> getObjectClass() {
        return objectClass;
    }

}
