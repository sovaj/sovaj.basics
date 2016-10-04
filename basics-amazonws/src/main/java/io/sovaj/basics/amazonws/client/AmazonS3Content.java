package io.sovaj.basics.amazonws.client;

import java.io.InputStream;

/**
 * 
 * @author Mickael Dubois
 */
public class AmazonS3Content {

    private final String subDirectory;
    private final String mimeType;
    private final InputStream content;
    private final String contentLabel;

    /**
     *
     * @param subDirectory relative folder in S3 the bucket
     * @param mimeType   mime type of the object to transfer onto the S3 bucket
     * @param content    The content subject to the upload to S3 bucket as a InputStream Object
     * @param contentLabel name of the object to transfer
     */
    public AmazonS3Content(String subDirectory, String mimeType, InputStream content, String contentLabel) {
        this.subDirectory = subDirectory;
        this.mimeType = mimeType;
        this.content = content;
        this.contentLabel = contentLabel;
    }

    public String getSubDirectory() {
        return subDirectory;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getContent() {
        return content;
    }

    public String getContentLabel() {
        return contentLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AmazonS3Content that = (AmazonS3Content) o;
        if (contentLabel != null ? !contentLabel.equals(that.contentLabel) : that.contentLabel != null) return false;
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (mimeType != null ? !mimeType.equals(that.mimeType) : that.mimeType != null) return false;
        if (subDirectory != null ? !subDirectory.equals(that.subDirectory) : that.subDirectory != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = subDirectory != null ? subDirectory.hashCode() : 0;
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (contentLabel != null ? contentLabel.hashCode() : 0);
        return result;
    }
}
