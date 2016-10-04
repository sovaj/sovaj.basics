package io.sovaj.basics.test.aspectlogging;

import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Mickael Dubois
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-test-aspectLogging.xml"})
public class AspectTracerTest {

    @Resource
    private AspectTracerTestingSupport testingSupport;

    @Test
    public void testAspectTracer() {
        testingSupport.testMethod("whatever", Integer.MAX_VALUE);
    }
}
