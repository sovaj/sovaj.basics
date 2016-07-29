/**
 *
 */
package org.sovaj.basics.test.random;

import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.LocalDate;

/**
 * G�n�rateur al�atoire de {@link LocalDate}.
 * 

 */
public class RandomLocalDateFactory implements Factory<LocalDate> {

    /**
     * {@inheritDoc}
     */
    public LocalDate create() {
        LocalDate localDate = new LocalDate();
        localDate = localDate.withDayOfYear(1 + RandomUtils.nextInt(365));
        localDate = localDate.withYear(1990 + RandomUtils.nextInt(151));
        return localDate;
    }

}
