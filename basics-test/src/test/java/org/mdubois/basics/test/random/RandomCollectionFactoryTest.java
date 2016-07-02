/**
 *
 */
package org.mdubois.basics.test.random;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link RandomCollectionFactory}.
 * 

 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class RandomCollectionFactoryTest {

    /**
     * Test method for
     * {@link RandomCollectionFactory#create()}
     * .
     */
    @Test
    public void testBoolean() {
        RandomCollectionFactory<List> factory = new RandomCollectionFactory<List>(List.class, Boolean.class);
        List<Boolean> results = factory.create();
        Assert.assertNotNull("null collection", results);
        Assert.assertFalse("empty collection", results.isEmpty());
    }

    /**
     * Test method for
     * {@link RandomCollectionFactory#create()}
     * .
     */
    @Test
    public void testFoo() {
        RandomCollectionFactory<List> factory = new RandomCollectionFactory<List>(List.class, Foo.class);
        List<Foo> results = factory.create();
        Assert.assertNotNull("null collection", results);
        Assert.assertFalse("empty collection", results.isEmpty());
    }
}
