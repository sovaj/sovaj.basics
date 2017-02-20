
package io.sovaj.basics.jaxb.xml.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Mickael Dubois
 */
public class DateTimeTypeAdapter extends XmlAdapter<String, Date> {

    private static final String FORMAT_DATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    @Override
    public Date unmarshal(String pStringDate) throws Exception {
        try {
            return new SimpleDateFormat(FORMAT_DATE).parse(pStringDate);
        } catch (ParseException pe){
            throw  new RuntimeException(pe);
        }    
    }

    @Override
    public String marshal(Date pDate) throws Exception {
        return new SimpleDateFormat(FORMAT_DATE).format(pDate);
    }
    
}
