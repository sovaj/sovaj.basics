/**
 *
 */
package org.sovaj.basics.test.random;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link RandomBigIntegerFactory}.
 * 

 */
public class RandomBigIntegerFactoryTest {

    /**
     * Test method for
     * {@link RandomBigIntegerFactory#create()}
     * .
     */
    @Test
    public void test() {
        RandomBigIntegerFactory factory = new RandomBigIntegerFactory();
        BigInteger result = factory.create();
        Assert.assertNotNull(result);
    }

}
