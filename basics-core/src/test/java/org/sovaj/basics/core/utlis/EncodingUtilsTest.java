package org.sovaj.basics.core.utlis;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Mickael Dubois
 */
public class EncodingUtilsTest {

    @Test
    public void testContainsURLSpecificCharacter() {
        Assert.assertTrue(EncodingUtils.containsURLSpecificCharacter("asd,dfg"));
        Assert.assertTrue(EncodingUtils.containsURLSpecificCharacter("asd&dfg"));
        Assert.assertFalse(EncodingUtils.containsURLSpecificCharacter("asd.dfg"));
        Assert.assertFalse(EncodingUtils.containsURLSpecificCharacter("asd_dfg"));
        Assert.assertFalse(EncodingUtils.containsURLSpecificCharacter("asd-dfg"));
    }
}
