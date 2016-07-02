package org.mdubois.basics.rule.engine;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by MFerlan1 on 11/3/2014.
 */
@Deprecated
//Use ClasspathRetriever
public class ClasspathRulesFileRetriever implements IRulesFileRetriever {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathRulesFileRetriever.class);

    private String fileClassPath;

    @Override
    public byte[] getRulesFile() throws Exception {
        try {
            return IOUtils.toByteArray(this.getClass().getResourceAsStream(fileClassPath));
        } catch (Exception e) {
            LOGGER.error("Unable to load file [{}]", fileClassPath);
            throw e;
        }
    }

    public String getFileClassPath() {

        return fileClassPath;
    }

    public void setFileClassPath(String fileClassPath) {

        this.fileClassPath = fileClassPath;
    }
}