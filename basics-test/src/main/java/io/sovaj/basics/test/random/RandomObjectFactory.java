package io.sovaj.basics.test.random;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * G�n�rateur al�atoire d'objets.
 * 

 * @param <O> {@link Object}
 */
public class RandomObjectFactory<O> extends AbstractFactory<O> {

    /**
     * {@link Factory}
     */
    private Factory<O> factory;

    /**
     * recursive
     */
    private boolean recursive;

    /**
     * Generation stack (prevent from {@link StackOverflowError})
     */
    private static final ThreadLocal<Map<Class< ? >, Object>> GENERATED_OBJECTS = new ThreadLocal<Map<Class< ? >, Object>>();

    /**
     * Constructeur
     * 
     * @param clazz {@link Class}
     */
    public RandomObjectFactory(Class<O> clazz) {
        this(clazz, true);
    }

    /**
     * Constructeur
     * 
     * @param clazz {@link Class}
     * @param factory {@link Factory}
     */
    public RandomObjectFactory(Class<O> clazz, Factory<O> factory) {
        this(clazz, factory, true);
    }

    /**
     * Constructeur
     * 
     * @param clazz {@link Class}
     * @param recursive recursive
     */
    public RandomObjectFactory(Class<O> clazz, boolean recursive) {
        super(clazz);
        this.recursive = recursive;
    }

    /**
     * Constructeur
     * 
     * @param clazz The class to create
     * @param factory {@link Factory}
     * @param recursive recursive
     */
    public RandomObjectFactory(Class<O> clazz, Factory<O> factory, boolean recursive) {
        this(clazz, recursive);
        this.factory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public O create() {
        // recursive check
        Class<O> objectClass = getObjectClass();
        Map<Class< ? >, Object> generatedObjects = GENERATED_OBJECTS.get();
        O generatedObject;
        if (generatedObjects != null) {
            generatedObject = (O) generatedObjects.get(objectClass);
            if (generatedObject != null) {
                return generatedObject;
            }
        }
        // generate a new object
        generatedObject = newInstance();

        // add this object to stack
        if (generatedObjects == null) {
            generatedObjects = new HashMap<Class< ? >, Object>();
            GENERATED_OBJECTS.set(generatedObjects);
        }
        generatedObjects.put(objectClass, generatedObject);

        try {
            // r�cup�ration de tous les champs de l'objet
            final Field[] fields = getAllFields(generatedObject);
            for (final Field field : fields) {
                final Class< ? > propertyClass = field.getType();

                // pour �viter les boucles infinies : on n'initialise pas les
                // propri�t�s du meme type que le parent
                if (ObjectUtils.equals(propertyClass, objectClass)) {
                    continue;
                }

                // generation
                generate(generatedObject, field);
            }
        } finally {
            // remove this object to stack
            generatedObjects.remove(objectClass);
            if (generatedObjects.isEmpty()) {
                GENERATED_OBJECTS.set(null);
            }
        }

        return generatedObject;
    }

    /**
     * G�n�ration de la valeur d'un champ.
     * 
     * @param object {@link Object}
     * @param field {@link Field}
     */
    protected void generate(Object object, Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            // impossible d'initialiser un champ final
            return;
        }

        // on r�cup�re un generateur pour la propri�t�
        final Factory< ? > generateur = getGenerateur(field);

        if (!recursive && (generateur instanceof RandomObjectFactory< ? > 
                                || generateur instanceof RandomCollectionFactory< ? >)) {
            // pas de g�n�ration des objets imbriqu�s
            return;
        }

        // on g�n�re une valeur al�atoire
        final Object propertyValue = generateur.create();

        Exception exception = null;
        try {
            // on initialise le champ
            FieldUtils.writeField(field, object, propertyValue, true);
        } catch (final IllegalAccessException | IllegalArgumentException e) {
            exception = e;
        }

        if (exception != null) {
            // on essaie par le writer s'il existe
            try {
                PropertyUtils.setProperty(object, field.getName(), propertyValue);
            } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                throw new RuntimeException(exception.getMessage(), e1);
            }
        }
    }

    /**
     * @param field {@link Field}
     * @return un {@link Factory} pour le champs <code>field</code>
     */
    protected Factory< ? > getGenerateur(Field field) {
        final Factory< ? > generateur = RandomFactoryToolkit.getInstance(field.getType());
        if (generateur instanceof IGenericFactory< ? >) {
            final Class< ? >[] genericTypes = getGenericTypes(field);
            ((IGenericFactory< ? > ) generateur).setGenericTypes(genericTypes);
        }
        return generateur;
    }

    /**
     * @return une nouvelle instance
     */
    @SuppressWarnings({"rawtypes", "unchecked" })
    protected O newInstance() {
        O instance = null;
        if (this.factory != null) {
            instance = this.factory.create();
        }
        Exception lastEx = null;
        if (instance == null) {
            try {
                return this.getObjectClass().newInstance();
            } catch (final InstantiationException e) {
                // no op
                lastEx = e;
            } catch (final IllegalAccessException e) {
                // no op
                lastEx = e;
            }
        }

        if (instance == null) {
            final Constructor[] constructors = this.getObjectClass().getConstructors();
            for (final Constructor constructor : constructors) {

                final Class< ? >[] parametreTypes = constructor.getParameterTypes();
                final Object[] parametres = RandomFactoryToolkit.generate(parametreTypes);

                try {
                    instance = (O ) constructor.newInstance(parametres);
                    break;
                } catch (final IllegalArgumentException e) {
                    // no op
                    lastEx = e;
                } catch (final InstantiationException e) {
                    // no op
                    lastEx = e;
                } catch (final IllegalAccessException e) {
                    // no op
                    lastEx = e;
                } catch (final InvocationTargetException e) {
                    // no op
                    lastEx = e;
                }
            }
        }

        if (instance == null) {
            throw new RuntimeException("Cannot instantiate an object of type '" + getObjectClass().getName()
                            + "'", lastEx);
        }
        return instance;
    }

    /**
     * Types g�n�riques
     * 
     * @param field {@link Field}
     * @return tableau de {@link Class}
     */
    protected Class< ? >[] getGenericTypes(Field field) {
        if (!(field.getGenericType() instanceof ParameterizedType)) {
            return null;
        }
        Type[] types = ((ParameterizedType ) field.getGenericType()).getActualTypeArguments();
        if (types == null) {
            return null;
        }
        Class< ? >[] classes = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            classes[i] = (Class< ? > ) type;
        }
        return classes;
    }

    /**
     * @param bean {@link Object}
     * @return Tous les {@link Field} de l'objet, y compris ceux des classes
     *         m�res.
     */
    private static Field[] getAllFields(Object bean) {
        final Class< ? extends Object> beanClass = bean.getClass();
        return getAllFields(beanClass);
    }

    /**
     * @param beanClass {@link Class}
     * @return Tous les {@link Field} de la classe, y compris ceux des classes
     *         m�res.
     */
    private static Field[] getAllFields(Class< ? extends Object> beanClass) {
        final Field[] fields = beanClass.getDeclaredFields();
        final Class< ? > superclass = beanClass.getSuperclass();
        if (superclass != null) {
            return (Field[] ) ArrayUtils.addAll(fields, getAllFields(superclass));
        } else {
            return fields;
        }
    }
}
