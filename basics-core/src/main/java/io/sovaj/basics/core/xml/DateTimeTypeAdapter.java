
package io.sovaj.basics.core.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Mickael Dubois
 */
public class DateTimeTypeAdapter {

    private static final String FORMAT_DATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static Date parseDateTime(String pStringDate)  {
        try {
            return new SimpleDateFormat(FORMAT_DATE).parse(pStringDate);
        } catch (ParseException pe){
            throw  new RuntimeException(pe);
        }
    }

    public static String printDateTime(Date pDate) {
        return new SimpleDateFormat(FORMAT_DATE).format(pDate);
    }
    
}
