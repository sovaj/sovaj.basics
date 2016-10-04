/**
 *
 */
package io.sovaj.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Byte}.
 * 

 */
public class RandomByteFactory implements Factory<Byte> {

    /**
     * {@inheritDoc}
     */
    public Byte create() {
        return (byte ) RandomUtils.nextInt(Byte.MAX_VALUE);
    }

}
