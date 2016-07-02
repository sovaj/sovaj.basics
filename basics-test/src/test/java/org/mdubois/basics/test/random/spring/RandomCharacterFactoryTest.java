package org.mdubois.basics.test.random.spring;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author tLuuHuu1 on 2015-06-22.
 */
public class RandomCharacterFactoryTest {

    private final RandomCharacterFactory factory = new RandomCharacterFactory();

    @Test
    public void createCharacter(){
        Character character = factory.create();
        Assert.notNull(character);
    }
}
