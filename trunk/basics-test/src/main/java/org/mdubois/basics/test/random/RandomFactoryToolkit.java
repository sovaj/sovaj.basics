/**
 *
 */
package org.mdubois.basics.test.random;

import org.mdubois.basics.test.random.spring.RandomCharacterFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Outils pour les {@link Factory} al�atoires.
 * 

 */
public final class RandomFactoryToolkit {

    /**
     * {@link RandomBigDecimalFactory}
     */
    public static final RandomBigDecimalFactory BIG_DECIMAL = new RandomBigDecimalFactory();

    /**
     * {@link RandomBooleanFactory}
     */
    public static final RandomBooleanFactory BOOLEAN = new RandomBooleanFactory();

    /**
     * {@link RandomCalendarFactory}
     */
    public static final RandomCalendarFactory CALENDAR = new RandomCalendarFactory();

    /**
     * {@link RandomDateFactory}
     */
    public static final RandomDateFactory DATE = new RandomDateFactory();

    /**
     * {@link RandomBigIntegerFactory}
     */
    public static final RandomBigIntegerFactory BIG_INTEGER = new RandomBigIntegerFactory();

    /**
     * {@link RandomIntegerFactory}
     */
    public static final RandomIntegerFactory INTEGER = new RandomIntegerFactory();

    /**
     * {@link RandomLongFactory}
     */
    public static final RandomLongFactory LONG = new RandomLongFactory();

    /**
     * {@link RandomShortFactory}
     */
    public static final RandomShortFactory SHORT = new RandomShortFactory();

    /**
     * {@link RandomByteFactory}
     */
    public static final RandomByteFactory BYTE = new RandomByteFactory();

    /**
     * {@link RandomFloatFactory}
     */
    public static final RandomFloatFactory FLOAT = new RandomFloatFactory();

    /**
     * {@link RandomDoubleFactory}
     */
    public static final RandomDoubleFactory DOUBLE = new RandomDoubleFactory();

        /**
     * {@link RandomStringFactory}
     */
    public static final RandomStringFactory STRING = new RandomStringFactory();

    /**
     * {@link RandomCharacterFactory}
     */
    public static final RandomCharacterFactory CHARACTER = new RandomCharacterFactory();

    /**
     * {@link RandomLocalDateFactory}
     */
    public static final RandomLocalDateFactory LOCAL_DATE = new RandomLocalDateFactory();

    /**
     * {@link RandomLocalDateTimeFactory}
     */
    public static final RandomLocalDateTimeFactory LOCAL_DATE_TIME = new RandomLocalDateTimeFactory();

    /**
     * {@link RandomDateTimeFactory}
     */
    public static final RandomDateTimeFactory DATE_TIME = new RandomDateTimeFactory();

    /**
     * {@link RandomXMLGregorianCalendarFactory}
     */
    public static final RandomXMLGregorianCalendarFactory XMLGREGORIANCALENDAR = new RandomXMLGregorianCalendarFactory();

    /**
     * User-defined generators
     */
    private static final Map<Class< ? >, Factory< ? >> registry = new HashMap<Class< ? >, Factory< ? >>();

    /**
     * Constructeur
     */
    private RandomFactoryToolkit() {
    }

    /**
     * @param <O> Type de la donn�e � g�n�rer
     * @param type Type de la donn�e � g�n�rer
     * @return un {@link Factory} pour le type de donn�es sp�cifi�
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    public static <O> Factory<O> getInstance(Class<O> type) {
        Factory<O> generateur = getFromRegistry(type);
        if (generateur != null) {
            return generateur;
        }
        if (BigDecimal.class.equals(type)) {
            generateur = (Factory<O> ) BIG_DECIMAL;
        } else if (Boolean.class.equals(type) || boolean.class.equals(type)) {
            generateur = (Factory<O> ) BOOLEAN;
        } else if (Calendar.class.equals(type)) {
            generateur = (Factory<O> ) CALENDAR;
        } else if (Date.class.equals(type)) {
            generateur = (Factory<O> ) DATE;
        } else if (BigInteger.class.equals(type)) {
            generateur = (Factory<O> ) BIG_INTEGER;
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            generateur = (Factory<O> ) INTEGER;
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            generateur = (Factory<O> ) LONG;
        } else if (Short.class.equals(type) || short.class.equals(type)) {
            generateur = (Factory<O> ) SHORT;
        } else if (Byte.class.equals(type) || byte.class.equals(type)) {
            generateur = (Factory<O> ) BYTE;
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            generateur = (Factory<O> ) DOUBLE;
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            generateur = (Factory<O> ) FLOAT;
        } else if (String.class.equals(type)) {
            generateur = (Factory<O> ) STRING;
        } else if (Character.class.equals(type)) {
            generateur = (Factory<O> ) CHARACTER;
        } else if (LocalDate.class.equals(type)) {
            generateur = (Factory<O> ) LOCAL_DATE;
        } else if (LocalDateTime.class.equals(type)) {
            generateur = (Factory<O> ) LOCAL_DATE_TIME;
        } else if (DateTime.class.equals(type)) {
            generateur = (Factory<O> ) DATE_TIME;
        } else if (XMLGregorianCalendar.class.isAssignableFrom(type)) {
            generateur = (Factory<O> ) XMLGREGORIANCALENDAR;
        } else if (Enum.class.isAssignableFrom(type)) {
            generateur = new RandomEnumFactory(type);
        } else if (type.isArray()) {
            generateur = new RandomArrayFactory(type);
        } else if (Collection.class.isAssignableFrom(type)) {
            generateur = new RandomCollectionFactory(type);
        } else if (Map.class.isAssignableFrom(type)) {
            generateur = new RandomMapFactory(type);
        } else {
            generateur = new RandomObjectFactory<O>(type);
        }
        return generateur;
    }

    @SuppressWarnings("unchecked")
    private static <O> Factory<O> getFromRegistry(Class<O> type) {
        return (Factory<O> ) registry.get(type);
    }

    /**
     * Ajoute un {@link Factory} au registre.
     * 
     * @param type
     * @param generateur
     */
    public static <O> void register(Class<O> type, Factory<O> generateur) {
        registry.put(type, generateur);
    }

    /**
     * @param <O> type
     * @param type {@link Class}
     * @return {@link Object}
     */
    public static <O> O generate(Class<O> type) {
        try {
            final Factory<O> generateur = getInstance(type);
            return generateur.create();
        } catch (final RuntimeException e) {
            throw new RuntimeException("Erreur lors de la g�n�ration de l'objet de type '" + type.getName() + "'", e);
        }
    }

    /**
     * @param types tableau de {@link Class}
     * @return tableau d'{@link Object}
     */
    public static Object[] generate(Class< ? >[] types) {

        final Object[] values = new Object[types.length];

        // initialisation de tous les parametres
        for (int i = 0; i < values.length; i++) {
            final Class< ? > parametreType = types[i];

            final Factory< ? > generateur = getInstance(parametreType);
            try {
                final Object value = generateur.create();
                values[i] = value;
            } catch (final RuntimeException e) {
                throw new RuntimeException("Erreur lors de la g�n�ration de l'objet " + (i + 1) + " de type " + types[i], e);
            }
        }
        return values;
    }
}
