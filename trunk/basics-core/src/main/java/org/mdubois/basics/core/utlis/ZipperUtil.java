package org.mdubois.basics.core.utlis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.*;

public class ZipperUtil {

    public static final String CHARACTER_ENCODING_UTF_8 = "UTF-8";

    /**
     *
     * @param payload the payload to zip
     * @return the zipped payaload
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     */
    public static byte[] zip(String payload) throws UnsupportedEncodingException, IOException {

        // Encode a String into bytes
        byte[] data = payload.getBytes(CHARACTER_ENCODING_UTF_8);

        // Compress the bytes
        ByteArrayOutputStream  output = new ByteArrayOutputStream();

        Deflater d = new Deflater();
        try (DeflaterOutputStream dout = new DeflaterOutputStream(output, d)) {
            dout.write(data);
        }

        return output.toByteArray();

    }


    /**
     *
     * @param input The zip data
     * @return The unzip data
     * @throws UnsupportedEncodingException The Character Encoding is not supported.
     */
    public static String unZip(byte[] input) throws UnsupportedEncodingException,IOException {

        ByteArrayInputStream inputStream=new ByteArrayInputStream(input);

        InflaterInputStream in = new InflaterInputStream(inputStream);
        ByteArrayOutputStream bout =
                new ByteArrayOutputStream(512);

        byte[] buffer = new byte[2048];
        int len = 0;
        while ((len = in.read(buffer)) > 0)
        {
            bout.write(buffer, 0, len);
        }
        in.close();
        bout.close();
        return bout.toString(CHARACTER_ENCODING_UTF_8);


    }

}
