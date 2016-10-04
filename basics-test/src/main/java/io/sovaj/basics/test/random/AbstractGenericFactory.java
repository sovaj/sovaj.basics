package io.sovaj.basics.test.random;


/**
 * Impl�mentation abstraite de {@link IGenericFactory}.
 * 

 * @param <O> Type de l'objet g�n�r�
 */
public abstract class AbstractGenericFactory<O> extends AbstractFactory<O> implements IGenericFactory<O> {

    /**
     * Type de donn�es g�n�rique
     */
    private Class< ? >[] genericTypes;

    /**
     * @param clazz Type de l'objet g�n�r�
     */
    public AbstractGenericFactory(Class<O> clazz) {
        super(clazz);
    }

    /**
     * @param clazz Type de l'objet g�n�r�
     * @param genericTypes Types g�n�riques
     */
    public AbstractGenericFactory(Class<O> clazz, Class< ? >... genericTypes) {
        super(clazz);
        this.genericTypes = genericTypes;
    }

    /**
     * @return the genericTypes
     */
    public Class< ? >[] getGenericTypes() {
        return genericTypes;
    }

    /**
     * @param genericTypes the genericTypes to set
     */
    public void setGenericTypes(Class< ? >... genericTypes) {
        this.genericTypes = genericTypes;
    }

}
