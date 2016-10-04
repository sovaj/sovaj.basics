/**
 *
 */
package io.sovaj.basics.test.random;

import org.apache.commons.lang.RandomStringUtils;

/**
 * G�n�rateur al�atoire de {@link String}.
 * 

 */
public class RandomStringFactory implements Factory<String> {

    /**
     * {@inheritDoc}
     */
    public String create() {
        return RandomStringUtils.random(10, true, true);
    }

}
