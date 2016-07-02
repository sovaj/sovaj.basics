/**
 *
 */
package org.mdubois.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Boolean}.
 * 

 */
public class RandomBooleanFactory implements Factory<Boolean> {

    /**
     * Constructeur
     */
    public RandomBooleanFactory() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean create() {
        return RandomUtils.nextBoolean();
    }

}
