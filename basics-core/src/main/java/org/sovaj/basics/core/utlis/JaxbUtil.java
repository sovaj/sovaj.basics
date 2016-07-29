package org.sovaj.basics.core.utlis;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: ZOuarab1
 * Date: 1/27/14
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class JaxbUtil {

    public static <T> String marshalToString(T obj, Class<T> clazz, QName qname) throws javax.xml.bind.JAXBException, java.io.IOException {

        java.io.StringWriter sw = new java.io.StringWriter();
        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(clazz.getPackage().getName());
        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(new JAXBElement(qname, clazz, obj), sw);
        sw.close();
        return sw.toString();

    }

    public static <T> T convertXMLToObject(byte[] data, Class<T> clazz) throws JAXBException {

        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(clazz.getPackage().getName());
        Unmarshaller um = jaxbCtx.createUnmarshaller();
        JAXBElement<T> element = (JAXBElement<T>) um.unmarshal(new ByteArrayInputStream(data));

        return element.getValue();
    }
}
