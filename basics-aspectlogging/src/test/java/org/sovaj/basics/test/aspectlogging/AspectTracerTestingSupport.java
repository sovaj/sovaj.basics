package org.sovaj.basics.test.aspectlogging;

import org.springframework.stereotype.Component;

/**
 * Comments here.
 * <p/>
 * User: LZhang1
 * Date: 11/03/14
 * Time: 4:32 PM
 */
@Component
public class AspectTracerTestingSupport {

    public String testMethod(final String arg1, final Integer arg2) {
        return "haha";
    }
}
