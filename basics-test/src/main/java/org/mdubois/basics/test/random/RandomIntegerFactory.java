/**
 *
 */
package org.mdubois.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Integer}.
 * 

 */
public class RandomIntegerFactory implements Factory<Integer> {

    /**
     * Maximum
     */
    private int max;

    /**
     * Constructeur
     * 
     * @param max maximum
     */
    public RandomIntegerFactory(int max) {
        this.max = max;
    }

    /**
     * Constructeur
     */
    public RandomIntegerFactory() {
        this(-1);
    }

    /**
     * {@inheritDoc}
     */
    public Integer create() {
        return max == -1 ? RandomUtils.nextInt() : RandomUtils.nextInt(max);
    }

}
