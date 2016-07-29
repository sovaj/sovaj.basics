package org.sovaj.basics.spring.batch.processor;

import org.sovaj.basics.core.converter.ListConverter;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertSame;

/**
 *
 * @author Mickael Dubois
 */
@Ignore
@RunWith(JMockit.class)
public class ConverterDelegateProcessorTest {

    private static class Foo {
    }

    private static class Bar {
    }

    @Mocked ListConverter<Foo, Bar> converter;

    public ConverterDelegateProcessorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of process method, of class ConverterDelegateProcessor.
     */
    @Test
    public void testProcess() throws Exception {
        final ConverterDelegateProcessor instance = new ConverterDelegateProcessor();
        final Foo foo = new Foo();
        final Bar bar = new Bar();

        // when instance.process(foo) is invoked as below, converter.convert is expected to be called
        // with foo as the argument;
        // when that happen, we return bar from the mocked converter, and expect it to be the return value
        // of the instance.process(foo) invocation.
        new Expectations() {{
            Deencapsulation.setField(instance, converter);
            converter.convert(foo); returns(bar);
        }};

        Object result = instance.process(foo);
        assertSame(bar, result);
    }

}
