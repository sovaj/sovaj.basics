package io.sovaj.basics.jaxb.xml.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerTypeXmlAdapter extends XmlAdapter<String, Integer> {

    public Integer unmarshal(String value) {
        if (value == null || value.length() < 1) {
            return null;
        }
        return Integer.valueOf(value);
    }

    public String marshal(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
