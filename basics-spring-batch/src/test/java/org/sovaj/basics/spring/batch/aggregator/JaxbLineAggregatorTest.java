package org.sovaj.basics.spring.batch.aggregator;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by MLi2 on 17/03/14, 12:58 PM To change this template use File |
 * Settings | File Templates.
 */
@RunWith(JMockit.class)
public class JaxbLineAggregatorTest {

    private static class Foo {
    }

    JaxbLineAggregator<Foo> jaxbLineAggregator;
    @Mocked
    MessageBodyWriter<Foo> messageBodyWriter;
    @Mocked
    ByteArrayOutputStream mockBaos;
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final Foo foo = new Foo();

    @Before
    public void setUp() throws Exception {
        jaxbLineAggregator = new JaxbLineAggregator();
        jaxbLineAggregator.setProvider(messageBodyWriter);
        jaxbLineAggregator.setOutputFormat(MediaType.APPLICATION_XML);
        jaxbLineAggregator.setClassToBeBound(String.class);
        jaxbLineAggregator.afterPropertiesSet();
    }

    @Test
    public void testAggregateWithDefaultOutputFormat() throws IOException {
        // when jaxbLineAggregator.aggregate(item) is invoked without explicitly specifying the output format, we expect
        //      1. new ByteArrayOutputStream() to be called, and
        //      2. messageBodyWriter.writeTo to be called using JSON media type (as below), and
        //      3. toString to be called from the newly constructed ByteArrayOutputStream;
        //
        // when that happen, the result of toString on the ByteArrayOutputStream instance is expected to be the return value
        // of the jaxbLineAggregator.aggregate(item) invocation.
        new Expectations() {
            {
                new ByteArrayOutputStream();
                result = baos;

                messageBodyWriter.writeTo(withSameInstance(foo), null, null, null, MediaType.APPLICATION_XML_TYPE, null, (OutputStream) any);

                baos.toString();
                result = "ABC";
            }
        };

        String result = jaxbLineAggregator.aggregate(foo);
        assertEquals("ABC", result);
    }

    @Test
    public void testAggregateWithSpecificOutputFormat() throws IOException {
        jaxbLineAggregator.setOutputFormat(MediaType.APPLICATION_JSON);

        // when jaxbLineAggregator.aggregate(item) is invoked with explicitly specified output format, we expect
        //      1. new ByteArrayOutputStream() to be called, and
        //      2. messageBodyWriter.writeTo to be called using the specified output format(as below), and
        //      3. toString to be called from the newly constructed ByteArrayOutputStream;
        //
        // when that happen, the result of toString on the ByteArrayOutputStream instance is expected to be the return value
        // of the jaxbLineAggregator.aggregate(item) invocation.
        new Expectations() {
            {
                new ByteArrayOutputStream();
                result = baos;

                messageBodyWriter.writeTo(withSameInstance(foo), null, null, null, MediaType.APPLICATION_JSON_TYPE, null, (OutputStream) any);

                baos.toString();
                result = "ABC";
            }
        };

        String result = jaxbLineAggregator.aggregate(foo);
        assertEquals("ABC", result);
    }

    @Test(expected = Exception.class)
    public void testAggregateWithInvalidOutputFormat() throws IOException {
        jaxbLineAggregator.setOutputFormat("invalid-media-type");
    }
}
