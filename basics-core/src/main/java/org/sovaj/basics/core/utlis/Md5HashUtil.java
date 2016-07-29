package org.sovaj.basics.core.utlis;

import org.springframework.util.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: ZOuarab1
 * Date: 1/28/14
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class Md5HashUtil {

    public static final String STRING_BYTE_ENCODING_UTF_8 = "UTF-8";

    public static String generateMd5Hash(String payLoad) throws UnsupportedEncodingException {
        if (payLoad!=null && payLoad.length()!=0)

            return DigestUtils.md5DigestAsHex(payLoad.getBytes(STRING_BYTE_ENCODING_UTF_8));
        else
            return null;

    }
}
