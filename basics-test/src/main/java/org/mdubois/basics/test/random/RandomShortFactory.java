/**
 *
 */
package org.mdubois.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Short}.
 * 

 */
public class RandomShortFactory implements Factory<Short> {

    /**
     * {@inheritDoc}
     */
    public Short create() {
        return (short ) RandomUtils.nextInt(Short.MAX_VALUE);
    }

}
