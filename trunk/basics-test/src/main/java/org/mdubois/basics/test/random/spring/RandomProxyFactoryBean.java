package org.mdubois.basics.test.random.spring;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.Assert;

/**
 * Factory Spring pour g�n�rer des objets al�atoires
 * 
 */
public class RandomProxyFactoryBean<T> extends AbstractFactoryBean<T> {

    /**
     * Le type des beans � instancier
     */
    private Class< ? > objectType;

    /**
//     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected T createInstance() throws Exception {
        Assert.notNull(getObjectType(), "objectType obligatoire");

        final InvocationHandler invocationHandler = new RandomInvocationHandler();

        // instantiate proxy
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{objectType }, invocationHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class< ? > getObjectType() {
        return objectType;
    }

    /**
     * @param objectType the objectType to set
     */
    @Required
    public void setObjectType(Class< ? > objectType) {
        this.objectType = objectType;
    }

}
