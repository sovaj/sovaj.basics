package io.sovaj.basics.jaxb.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerTypeXmlAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(String value) {
        if (value == null || value.length() < 1) {
            return null;
        }
        return Integer.valueOf(value);
    }

    @Override
    public String marshal(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
