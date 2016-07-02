/**
 *
 */
package org.mdubois.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire d'enum.
 * 

 * @param <E> {@link Enum}
 */
public class RandomEnumFactory<E extends Enum<E>> extends AbstractFactory<E> {

    /**
     * Constructeur
     * 
     * @param clazz {@link Class}
     */
    public RandomEnumFactory(Class<E> clazz) {
        super(clazz);
    }

    /**
     * {@inheritDoc}
     */
    public E create() {

        // r�cup�ration des valeurs possibles
        final E[] enumConstants = getObjectClass().getEnumConstants();

        // g�n�ration d'un index al�atoire
        final int index = RandomUtils.nextInt(enumConstants.length);

        return enumConstants[index];
    }
}
