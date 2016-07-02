package org.mdubois.basics.test.random;

/**

 * @param <T>
 */
public interface Factory<T> extends org.apache.commons.collections.Factory {

    /**
     * {@inheritDoc}
     */
    T create();
}
