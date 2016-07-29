package org.sovaj.basics.core.utlis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sovaj.basics.core.JSType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 *
 * @author Mickael Dubois
 */
public class ClassDescriptor {

    private static void describeArray(StringBuilder stringBuilder, Class<?> clazz, String pad, String leadin, Class<?> typeOfArray, final List<String> classesToStop, final List<String> packagesToStop) {
        if (clazz == null) {
            return;
        }

        stringBuilder.append(String.format("%s%s:{%n%s\"type\":\"array\", %n",
                pad, leadin, pad + "   "));

        StringBuilder newStringBuilder = new StringBuilder();
        JSType jsType = describe(newStringBuilder, typeOfArray, pad + "      ", "", classesToStop, packagesToStop);
        if (jsType.equals(JSType.OBJECT)) {
            stringBuilder.append(String.format("%s\"items\" : {%n", pad + "   "));
            newStringBuilder.delete(0, 2);
            newStringBuilder.deleteCharAt(newStringBuilder.length() - 1);
            stringBuilder.append(newStringBuilder);
            stringBuilder.append(String.format("%s}%n", pad + "   "));
            stringBuilder.append(String.format("%s}%n", pad));
        } else {
            stringBuilder.append(String.format("%s\"items\" : {%n %s    \"type\":\"%s\"%n%s}%n", pad + "   ",pad + "   ", jsType.toString(), pad + "   "));
            stringBuilder.append(String.format("%s}%n", pad));
        }
    }

    private static JSType describe(StringBuilder stringBuilder, Class<?> clazz, String pad, String leadin, final List<String> classesToStop, final List<String> packagesToStop) {
        if (clazz == null) {
            return JSType.UNDEFINED;
        }
        String type
                = clazz.isInterface() ? "interface"
                        : clazz.isArray() ? "array"
                                : clazz.isPrimitive() ? "primitive"
                                        : clazz.isEnum() ? "enum"
                                                : "class";
        JSType jsType = JSType.getJSType(clazz);
        if (leadin.equals("")) {
            stringBuilder.append(String.format("{%n%s%s\"type\":\"%s\", %n",
                    pad, leadin, jsType));
        } else if (jsType.equals(JSType.ARRAY)) {

        } else if (jsType.equals(JSType.OBJECT)) {
            stringBuilder.append(String.format("%s%s:{%n%s\"type\":\"%s\", %n",
                    pad, leadin, pad + "   ", jsType));
        } else {
            stringBuilder.append(String.format("%s%s:{\"type\":\"%s\", %n",
                    pad, leadin, jsType));
        }

        if (jsType.equals(JSType.OBJECT)) {
            stringBuilder.append(String.format("%s\"properties\" : {%n", pad));

        }
        for (Field field : clazz.getDeclaredFields()) {
            if (type.equals("class")
                    && !isPackageIn(clazz, packagesToStop)
                    && !classesToStop.contains(clazz.getSimpleName())) {

                if (field.getGenericType() instanceof ParameterizedTypeImpl) {
                    Class parametrizedClass = (Class) ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0];
                    describeArray(stringBuilder, clazz, pad + "   ", field.getName(), parametrizedClass, classesToStop, packagesToStop);
                } else {
                    describe(stringBuilder, field.getType(), pad + "   ", "\"" + field.getName() + "\"", classesToStop, packagesToStop);

                }
            }
        }
        if (jsType.equals(JSType.OBJECT)) {
            stringBuilder.append(String.format("%s}%n", pad));
        } else {
            int lastComma = stringBuilder.lastIndexOf(",");
            stringBuilder.deleteCharAt(lastComma);
            stringBuilder.insert(lastComma, "}");
        }

        if (leadin.equals("")) {
            stringBuilder.append("}");
        }
        return jsType;
    }

    private static boolean isPackageIn(Class<?> interfaze, final List<String> packagesToStop) {
        return packagesToStop.contains(interfaze.getPackage().getName());
    }

    static StringBuilder describe(Class<?> clazz, final String[] pStopAtClassName, final String[] pStopAtPackage) {
        if (pStopAtClassName == null) {
            throw new IllegalArgumentException("pStopAtClassName should not be null");
        }
        if (pStopAtPackage == null) {
            throw new IllegalArgumentException("pStopAtPackage should not be null");
        }
        List<String> lStopAtClassName = Arrays.asList(pStopAtClassName);
        List<String> lStopAtPackage = new ArrayList<>(Arrays.asList(pStopAtPackage));
        lStopAtPackage.add("java.lang");
        lStopAtPackage.add("java.io");
        lStopAtPackage.add("java.util");
        StringBuilder sb = new StringBuilder();
        describe(sb, clazz, "   ", "", lStopAtClassName, lStopAtPackage);
        return sb;
    }
    
}
