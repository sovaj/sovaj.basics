/**
 *
 */
package org.sovaj.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Long}.
 * 

 */
public class RandomLongFactory implements Factory<Long> {

    /**
     * {@inheritDoc}
     */
    public Long create() {
        return RandomUtils.nextLong();
    }

}
