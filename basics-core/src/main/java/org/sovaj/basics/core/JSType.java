/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sovaj.basics.core;

/**
 *
 * @author mdubois2
 */
public enum JSType {

    STRING, NUMBER, ARRAY, BOOLEAN, OBJECT, UNDEFINED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static JSType getJSType(final Class javaClass) {
        switch (javaClass.getSimpleName()) {
            case "String":
                return JSType.STRING;
            case "Integer":
            case "integer":
            case "Long":
            case "long":
            case "Double":
            case "double":
            case "Float":
            case "float":
                return JSType.NUMBER;
            case "List":
            case "Array":
                return JSType.ARRAY;
            case "Boolean":
            case "boolean":
                return JSType.BOOLEAN;
            default:
                return JSType.OBJECT;
        }
    }

   
}
