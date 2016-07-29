package org.sovaj.basics.core.utlis;

import java.io.IOException;
import java.util.zip.DataFormatException;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class ZipperUtilTest {

    @Test
    public void testZipUnzip() throws IOException, DataFormatException {

        String StringToZip = "ceci est une chaine de caracter a compacter";

        byte[] zippedString = ZipperUtil.zip(StringToZip);
        String unzippedString = ZipperUtil.unZip(zippedString);
        Assert.assertEquals(StringToZip, unzippedString);
    }

    @Test
    public void testZipUnzipStringFromXMLFile() throws IOException, DataFormatException {

        String StringToZip = IOUtils.toString(getClass().getResourceAsStream("/acmessagesbusinessHours.xml"), "UTF-8");

        byte[] zippedString = ZipperUtil.zip(StringToZip);
        String unzippedString = ZipperUtil.unZip(zippedString);
        Assert.assertEquals(StringToZip, unzippedString);
    }

}
