/**
 *
 */
package io.sovaj.basics.test.random;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * G�n�rateur al�atoire de {@link Collection}.
 * 

 * @param <O> Type du tableau
 */
public class RandomArrayFactory<O> extends AbstractFactory<O> {

    /**
     * max elements count in array ; default is 5
     */
    private int maxElementsCount = 5;

    /**
     * Constructeur
     * 
     * @param arrayClass Type du tableau
     */
    public RandomArrayFactory(Class<O> arrayClass) {
        super(arrayClass);
        if (!arrayClass.isArray()) {
            throw new IllegalArgumentException("Class must be array-class : " + arrayClass);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public O create() {
        // random size
        final RandomIntegerFactory randomIntegerFactory = new RandomIntegerFactory(maxElementsCount);
        final int elementsCount = Math.max(randomIntegerFactory.create(), 2);
        final Object array = Array.newInstance(getObjectClass().getComponentType(), elementsCount);
        // random fill
        for (int i = 0; i < elementsCount; i++) {
            Array.set(array, i, RandomFactoryToolkit.generate(getObjectClass().getComponentType()));
        }
        return (O ) array;
    }

    /**
     * @param maxElementsCount the maxElementsCount to set
     */
    public void setMaxElementsCount(int maxElementsCount) {
        this.maxElementsCount = maxElementsCount;
    }
}
