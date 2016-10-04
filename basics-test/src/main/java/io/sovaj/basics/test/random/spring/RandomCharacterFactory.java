package io.sovaj.basics.test.random.spring;

import io.sovaj.basics.test.random.Factory;
import org.apache.commons.lang.RandomStringUtils;

/**
 * @author tLuuHuu1 on 2015-06-22.
 */
public class RandomCharacterFactory implements Factory<Character> {

    @Override
    public Character create() {
        return RandomStringUtils.random(1, true, true).charAt(0);
    }
}
