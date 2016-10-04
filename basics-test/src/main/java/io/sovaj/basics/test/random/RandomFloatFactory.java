/**
 *
 */
package io.sovaj.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Float}.
 * 

 */
public class RandomFloatFactory implements Factory<Float> {

    /**
     * {@inheritDoc}
     */
    public Float create() {
        return RandomUtils.nextFloat();
    }

}
