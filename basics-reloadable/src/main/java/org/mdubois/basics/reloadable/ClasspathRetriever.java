package org.mdubois.basics.reloadable;

import org.apache.commons.io.IOUtils;

/**
 * Created by MFerlan1 on 11/3/2014.
 */
public class ClasspathRetriever implements IRetriever {

    private String fileClassPath;

    @Override
    public byte[] getBytes() throws Exception {
        return IOUtils.toByteArray(this.getClass().getResourceAsStream(fileClassPath));
    }

    public String getClassPath() {

        return fileClassPath;
    }

    public void setClassPath(String fileClassPath) {

        this.fileClassPath = fileClassPath;
    }
}
