package io.sovaj.basics.reloadable;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Manuel MFerland
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
