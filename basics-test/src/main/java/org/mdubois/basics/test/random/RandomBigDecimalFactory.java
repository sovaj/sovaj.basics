/**
 *
 */
package org.mdubois.basics.test.random;

import java.math.BigDecimal;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link BigDecimal}.
 * 

 */
public class RandomBigDecimalFactory implements Factory<BigDecimal> {

    /**
     * {@inheritDoc}
     */
    public BigDecimal create() {
        return new BigDecimal(String.valueOf(RandomUtils.nextInt(100) + RandomUtils.nextDouble()));
    }

}
