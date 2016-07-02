/**
 *
 */
package org.mdubois.basics.test.random;


/**
 * Un g�n�rateur de donn�es supportant les generics.
 * 

 * @param <O> Type de l'objet g�n�r�
 */
public interface IGenericFactory<O> extends Factory<O> {

    /**
     * @param genericTypes Type de donn�es g�n�rique
     */
    void setGenericTypes(Class< ? >... genericTypes);
}
