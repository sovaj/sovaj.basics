/**
 *
 */
package org.mdubois.basics.test.random;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.map.LinkedMap;

/**
 * G�n�rateur al�atoire de {@link Map}.
 * 

 * @param <O> {@link Map}
 */
@SuppressWarnings({"unchecked", "rawtypes" })
public class RandomMapFactory<O extends Map> extends AbstractGenericFactory<O> {

    /**
     * Constructeur
     * 
     * @param theClazz {@link Class} de {@link Map}
     */
    public RandomMapFactory(Class<O> theClazz) {
        super(theClazz);
    }

    /**
     * Constructeur
     * 
     * @param clazz {@link Class} de {@link Map}
     * @param genericTypeKey Type g�n�rique pour les cl�s de la {@link Map}
     * @param genericTypeValue Type g�n�rique pour les valeurs de la {@link Map}
     */
    public RandomMapFactory(Class<O> clazz, Class< ? > genericTypeKey, Class< ? > genericTypeValue) {
        super(clazz, genericTypeKey, genericTypeValue);
    }

    /**
     * Constructeur
     * 
     * @param clazz {@link Class} de {@link Map}
     * @param genericTypes Types g�n�riques
     */
    protected RandomMapFactory(Class<O> clazz, Class< ? >... genericTypes) {
        super(clazz, genericTypes);
    }

    /**
     * {@inheritDoc}
     */
    public O create() {
        // new instance
        final O map = newInstance();

        // generate values if possible
        final Class< ? >[] genericTypes = getGenericTypes();
        if (genericTypes != null && genericTypes.length > 1) {
            final Class< ? > genericTypeKey = genericTypes[0];
            final Class< ? > genericTypeValue = genericTypes[1];
            // random size
            final RandomIntegerFactory randomIntegerFactory = new RandomIntegerFactory(5);
            final int elementsCount = randomIntegerFactory.create() + 2;
            // random fill
            for (int i = 0; i < elementsCount; i++) {
                map.put(RandomFactoryToolkit.generate(genericTypeKey), RandomFactoryToolkit
                                .generate(genericTypeValue));
            }
        }
        return map;
    }

    /**
     * @return new instance of {@link O}
     */
    protected O newInstance() {
        if (!getObjectClass().isInterface()) {
            try {
                return getObjectClass().getConstructor().newInstance();
            } catch (final Exception e) {
                throw new RuntimeException("Impossible d'instantier un objet de type '" + getObjectClass().getName()
                                + "'", e);
            }
        }
        Object instance = null;
        if (SortedMap.class.isAssignableFrom(getObjectClass())) {
            instance = new TreeMap<Object, Object>();
        } else if (IterableMap.class.isAssignableFrom(getObjectClass())) {
            instance = new LinkedMap();
        } else {
            // default
            instance = new HashMap<Object, Object>();
        }

        return (O ) instance;
    }

}
