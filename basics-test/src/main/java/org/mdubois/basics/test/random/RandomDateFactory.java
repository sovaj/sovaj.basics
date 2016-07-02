/**
 *
 */
package org.mdubois.basics.test.random;

import java.util.Date;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Date}.
 * 

 */
public class RandomDateFactory implements Factory<Date> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("deprecation")
    public Date create() {
        final Date date = new Date();
        date.setDate(1 + RandomUtils.nextInt(365));
        date.setYear(1990 + RandomUtils.nextInt(151));
        return date;
    }

}
