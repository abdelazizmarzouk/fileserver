package com.marshmelo.fileserver.model;

import java.util.Map;

/**
 * Http Response model which encapsulates all required data for an HTTP response.
 */
public class HttpResponse {

    private final String httpReplyHeader;
    private final int status;
    private final byte[] content;
    private final String mimeType;
    private final int length;
    private final Map<String, String> headers;

    /**
     * Http response which encapsulates all required data for an HTTP response.
     * None of the fields should be null and the headers map should not be empty.
     *
     * @param httpReplyHeader
     * @param status
     * @param resource
     * @param headers
     */
    public HttpResponse(String httpReplyHeader, int status, Resource resource, Map<String, String> headers) {
        assert httpReplyHeader != null && resource != null && headers != null && !headers.isEmpty();
        this.httpReplyHeader = httpReplyHeader;
        this.status = status;
        this.content = resource.getContent();
        this.mimeType = resource.getMimeType();
        this.length = resource.getLength();
        this.headers = headers;
    }

    /**
     * Add custom headers.
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public int getStatus() {
        return status;
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHttpReplyHeader() {
        return httpReplyHeader;
    }
}
