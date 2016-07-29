/**
 *
 */
package org.sovaj.basics.test.random;

import java.math.BigInteger;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link BigInteger}.
 * 

 */
public class RandomBigIntegerFactory implements Factory<BigInteger> {

    /**
     * {@inheritDoc}
     */
    public BigInteger create() {
        return new BigInteger(String.valueOf(RandomUtils.nextInt()));
    }

}
