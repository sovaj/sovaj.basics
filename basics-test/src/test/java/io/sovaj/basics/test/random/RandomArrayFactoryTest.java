/**
 *
 */
package io.sovaj.basics.test.random;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link RandomArrayFactory}.
 * 

 */
public class RandomArrayFactoryTest {

    /**
     * Test method for {@link RandomArrayFactory#create()}.
     */
    @Test
    public void testPrimitiveBoolean() {
        RandomArrayFactory<boolean[]> factory = new RandomArrayFactory<boolean[]>(boolean[].class);
        boolean[] results = factory.create();
        Assert.assertNotNull("null array", results);
        Assert.assertFalse("empty array", results.length == 0);
    }

    /**
     * Test method for
     * {@link RandomArrayFactory#create()}
     * .
     */
    @Test
    public void testBoolean() {
        RandomArrayFactory<Boolean[]> factory = new RandomArrayFactory<Boolean[]>(Boolean[].class);
        Boolean[] results = factory.create();
        Assert.assertNotNull("null array", results);
        Assert.assertFalse("empty array", results.length == 0);
    }

    /**
     * Test method for
     * {@link RandomArrayFactory#create()}
     * .
     */
    @Test
    public void testString() {
        RandomArrayFactory<String[]> factory = new RandomArrayFactory<String[]>(String[].class);
        String[] results = factory.create();
        Assert.assertNotNull("null array", results);
        Assert.assertFalse("empty array", results.length == 0);
    }

    /**
     * Test method for
     * {@link RandomArrayFactory#create()}
     * .
     */
    @Test
    public void testFoo() {
        RandomArrayFactory<Foo[]> factory = new RandomArrayFactory<Foo[]>(Foo[].class);
        Foo[] results = factory.create();
        Assert.assertNotNull("null array", results);
        Assert.assertFalse("empty array", results.length == 0);
    }

}
