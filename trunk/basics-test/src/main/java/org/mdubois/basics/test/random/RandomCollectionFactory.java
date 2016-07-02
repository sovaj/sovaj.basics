/**
 *
 */
package org.mdubois.basics.test.random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * G�n�rateur al�atoire de {@link Collection}.
 * 

 * @param <O> {@link Collection}
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class RandomCollectionFactory<O extends Collection> extends AbstractGenericFactory<O> {

    /**
     * max elements count in collection ; default is 5
     */
    private int maxElementsCount = 5;

    /**
     * Constructeur
     * 
     * @param theClazz {@link Class} de {@link Collection}
     */
    public RandomCollectionFactory(Class<O> theClazz) {
        super(theClazz);
    }

    /**
     * Constructeur
     * 
     * @param clazz {@link Class} de {@link Collection}
     * @param genericType Type g�n�rique des donn�es de la {@link Collection}.
     */
    public RandomCollectionFactory(Class<O> clazz, Class< ? > genericType) {
        super(clazz, genericType);
    }

    /**
     * {@inheritDoc}
     */
    public O create() {
        // new instance
        final O collection = newInstance();

        // generate values if possible
        final Class< ? >[] genericTypes = getGenericTypes();
        if (genericTypes != null && genericTypes.length > 0) {
            final Class< ? > genericType = genericTypes[0];
            // random size
            final RandomIntegerFactory randomIntegerFactory = new RandomIntegerFactory(maxElementsCount);
            final int elementsCount = Math.max(randomIntegerFactory.create(), 2);
            // random fill
            for (int i = 0; i < elementsCount; i++) {
                collection.add(RandomFactoryToolkit.generate(genericType));
            }
        }
        return collection;
    }

    /**
     * @return new instance of {@link O}
     */
    protected O newInstance() {
        if (!getObjectClass().isInterface()) {
            try {
                return getObjectClass().getConstructor().newInstance();
            } catch (final Exception e) {
                throw new RuntimeException("Impossible d'instantier un objet de type '" + getObjectClass().getName() + "'", e);
            }
        }
        Object instance = null;
        if (SortedSet.class.isAssignableFrom(getObjectClass())) {
            instance = new TreeSet<Object>();
        } else if (Set.class.isAssignableFrom(getObjectClass())) {
            instance = new HashSet<Object>();
        } else if (List.class.isAssignableFrom(getObjectClass())) {
            instance = new ArrayList<Object>();
        } else if (Queue.class.isAssignableFrom(getObjectClass())) {
            instance = new LinkedList<Object>();
        } else {
            // default
            instance = new ArrayList<Object>();
        }

        return (O ) instance;
    }

    /**
     * @param maxElementsCount the maxElementsCount to set
     */
    public void setMaxElementsCount(int maxElementsCount) {
        this.maxElementsCount = maxElementsCount;
    }
}
