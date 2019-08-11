package com.marshmelo.fileserver.model;

/**
 * A resource model for a file read from the file system and can be used in the resources cache.
 */
public class Resource {
    private byte[] content;
    private String mimeType;
    private int length;

    /**
     * Create a resource model, content and mime type should not be {@code null}
     *
     * @param content  resource content as an array of bytes
     * @param mimeType depending on the file extension
     */
    public Resource(byte[] content, String mimeType) {
        assert content != null && mimeType != null;
        this.content = content;
        this.mimeType = mimeType;
        this.length = content.length;
    }

    public byte[] getContent() {
        return content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getLength() {
        return length;
    }
}
