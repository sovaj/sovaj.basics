package io.sovaj.basics.test.random.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.builder.HashCodeBuilder;

import io.sovaj.basics.test.random.RandomFactoryToolkit;
import org.springframework.aop.support.AopUtils;

/**
 * Un {@link InvocationHandler} qui renvoie des objets al�atoires.
 * 

 */
public class RandomInvocationHandler implements InvocationHandler {

    /**
     * {@link Logger}
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomInvocationHandler.class);

    /**
     * Indique s'il faut mettre en cache les r�sultats des invocations
     */
    private boolean useResultsCache = true;

    /**
     * results cache
     */
    private Map<Integer, Object> resultsCache = new HashMap<Integer, Object>();

    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (AopUtils.isEqualsMethod(method)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Invocation of 'equals' method");
            }
            // The target does not implement the equals(Object) method itself.
            return equals(args[0]);
        }
        if (AopUtils.isHashCodeMethod(method)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Invocation of 'hashcode' method");
            }
            // The target does not implement the hashCode() method itself.
            return hashCode();
        }

        // void method
        Class< ? > returnType = method.getReturnType();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ReturnType is {}", returnType);
        }
        if (Void.TYPE.equals(returnType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Invocation of method returning void");
            }
            return null;
        }

        // build invocation hash code
        int invocationHashCode = new HashCodeBuilder().append(method).append(args).toHashCode();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Invocation hashcode is {}", invocationHashCode);
        }

        Object result;
        if (useResultsCache) {
            // lookup for cached result
            result = resultsCache.get(invocationHashCode);
            if (result == null) {
                // generate new result
                result = generate(returnType);
                resultsCache.put(invocationHashCode, result);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Random result returned from cache");
            }
        } else {
            // generate new result
            result = generate(returnType);
        }

        return result;
    }

    /**
     * @param returnType {@link Class}
     * @return objet al�atoire
     */
    private Object generate(Class< ? > returnType) {
        Object result = RandomFactoryToolkit.generate(returnType);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Random result generated");
        }
        return result;
    }

    /**
     * Efface les r�sultats du cache
     */
    public void clearResultsCache() {
        this.resultsCache.clear();
    }

    /**
     * @param useResultsCache the useResultsCache to set
     */
    public void setUseResultsCache(boolean useResultsCache) {
        this.useResultsCache = useResultsCache;
    }
}
