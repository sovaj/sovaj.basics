/**
 *
 */
package org.sovaj.basics.test.random;

import java.util.Calendar;

import org.apache.commons.lang.math.RandomUtils;

/**
 * G�n�rateur al�atoire de {@link Calendar}.
 * 

 */
public class RandomCalendarFactory implements Factory<Calendar> {

    /**
     * {@inheritDoc}
     */
    public Calendar create() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1 + RandomUtils.nextInt(365));
        calendar.set(Calendar.YEAR, 1990 + RandomUtils.nextInt(51));
        return calendar;
    }

}
