package org.sovaj.basics.amazonws.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA. User: ZOuarab1 Date: 10/22/13 Time: 4:37 PM To
 * change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-amazonws.xml")
public class AmazonWSClientTestIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonWSClientTestIT.class);

    @Autowired
    private IAmazonWSClient amazonS3Client;

    @Test
    public void uploadObject() throws IOException, AmazonWSException {

        //       ObjectListing objects = conn.listObjects("static.cms.yp.ca");
//        do {
//            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
//                System.out.println(objectSummary.getKey() + "\t" +
//                        objectSummary.getSize() + "\t" +
//                        StringUtils.fromDate(objectSummary.getLastModified()));
//            }
//            objects = conn.listNextBatchOfObjects(objects);
//        } while (objects.isTruncated());
        BufferedImage image = ImageIO.read(AmazonWSClientTestIT.class.getResourceAsStream("/backgound180X180.jpg"));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray(), 0, os.size());

        //ObjectMetadata metadata=new ObjectMetadata();
        // metadata.setContentLength(os.toByteArray().length);
        // metadata.setContentType("image/jpeg");
        //PutObjectResult result= conn.putObject("static.cms.yp.ca/facebook/logo", "backgound180X180.jpg", is, metadata);
        AmazonS3Content content = new AmazonS3Content("facebook/logo", "image/jpeg", is, "backgound180X180.jpg");
        String s3Url = amazonS3Client.deployOnS3(content);
        LOGGER.info(s3Url);

        HttpURLConnection.setFollowRedirects(false);

        HttpURLConnection con
                = (HttpURLConnection) new URL(s3Url).openConnection();
        con.setRequestMethod("HEAD");
        Assert.assertTrue(con.getResponseCode() == HttpURLConnection.HTTP_OK);

    }
}
