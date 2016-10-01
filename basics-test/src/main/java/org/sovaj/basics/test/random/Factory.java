package org.sovaj.basics.test.random;

/**

 * @param <T> The type of element
 */
public interface Factory<T> extends org.apache.commons.collections.Factory {

    /**
     * {@inheritDoc}
     */
    @Override
    T create();
}
