package io.sovaj.basics.test.random;

import org.apache.commons.collections.map.MultiKeyMap;

/**
 * G�n�rateur al�atoire pour les {@link MultiKeyMap}.
 * 

 */
public class RandomMultiKeyMapFactory extends AbstractGenericFactory<MultiKeyMap> {

    /**
     * type des valeurs
     */
    private Class< ? > valueType;

    /**
     * Constructeur
     * 
     * @param valueType type des valeurs
     * @param keyTypes types des class
     */
    public RandomMultiKeyMapFactory(Class< ? > valueType, Class< ? >... keyTypes) {
        super(MultiKeyMap.class, keyTypes);
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    public MultiKeyMap create() {
        // new instance
        final MultiKeyMap map = new MultiKeyMap();

        // random size
        final RandomIntegerFactory randomIntegerFactory = new RandomIntegerFactory(5);
        final int elementsCount = randomIntegerFactory.create() + 2;

        // random fill
        for (int i = 0; i < elementsCount; i++) {
            if (getGenericTypes().length == 1) {
                map.put(RandomFactoryToolkit.generate(getGenericTypes()[0]), RandomFactoryToolkit
                                .generate(valueType));
            } else if (getGenericTypes().length == 2) {
                map.put(RandomFactoryToolkit.generate(getGenericTypes()[0]), RandomFactoryToolkit
                                .generate(getGenericTypes()[1]), RandomFactoryToolkit.generate(valueType));
            } else if (getGenericTypes().length == 3) {
                map.put(RandomFactoryToolkit.generate(getGenericTypes()[0]), RandomFactoryToolkit
                                .generate(getGenericTypes()[1]),
                                RandomFactoryToolkit.generate(getGenericTypes()[2]), RandomFactoryToolkit
                                                .generate(valueType));
            } else if (getGenericTypes().length == 4) {
                map.put(RandomFactoryToolkit.generate(getGenericTypes()[0]), RandomFactoryToolkit
                                .generate(getGenericTypes()[1]),
                                RandomFactoryToolkit.generate(getGenericTypes()[2]), RandomFactoryToolkit
                                                .generate(getGenericTypes()[3]), RandomFactoryToolkit
                                                .generate(valueType));
            } else if (getGenericTypes().length == 5) {
                map.put(RandomFactoryToolkit.generate(getGenericTypes()[0]), RandomFactoryToolkit
                                .generate(getGenericTypes()[1]),
                                RandomFactoryToolkit.generate(getGenericTypes()[2]), RandomFactoryToolkit
                                                .generate(getGenericTypes()[3]), RandomFactoryToolkit
                                                .generate(getGenericTypes()[4]), RandomFactoryToolkit
                                                .generate(valueType));
            }
        }

        return map;
    }
}
