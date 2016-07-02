/**
 *
 */
package org.mdubois.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Double}.
 * 

 */
public class RandomDoubleFactory implements Factory<Double> {

    /**
     * {@inheritDoc}
     */
    public Double create() {
        return RandomUtils.nextDouble();
    }

}
