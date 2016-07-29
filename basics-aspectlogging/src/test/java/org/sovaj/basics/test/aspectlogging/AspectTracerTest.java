package org.sovaj.basics.test.aspectlogging;

import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA. User: LZhang1 Date: 3/11/14 Time: 5:50 PM
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
