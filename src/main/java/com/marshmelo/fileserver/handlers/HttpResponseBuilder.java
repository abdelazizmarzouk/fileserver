package com.marshmelo.fileserver.handlers;

import com.marshmelo.fileserver.models.HttpResponse;
import com.marshmelo.fileserver.models.Resource;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is used to build an HTTP Response model which contains all necessary information of the response.
 * e.g. Headers, Content, Response Status.
 */
public class HttpResponseBuilder {

    private static final String HTTP_1_1_PROTOCOL_HEADER = "HTTP/1.1 %s %s";
    private static final int DEFAULT_FOUND_STATUS = 302;

    private final Resource resource;
    private final int status;
    private final Map<String, String> headers = new HashMap<>();

    private static final Map<Integer, String> replies = new HashMap<>();

    static {
        replies.put(200, "OK");
        replies.put(500, "Internal Server Error");
        replies.put(404, "Not Found");
        replies.put(302, "Found");
    }

    private HttpResponseBuilder(Resource resource, int status) {
        this.resource = resource;
        this.status = status;
    }

    /**
     * Build the {@link HttpResponse}.
     *
     * @param resource {@link Resource} model.
     * @param status   the status code of the response.
     * @return an {@link HttpResponse}.
     */
    public static HttpResponse buildResponse(Resource resource, int status) {
        HttpResponseBuilder builder = new HttpResponseBuilder(resource, status);
        return builder.build();
    }

    private HttpResponse build() {
        String replyHeader = getHttpReplyHeader(status);
        buildHeaders();
        HttpResponse response = new HttpResponse(replyHeader, status, resource, headers);
        return response;
    }

    /**
     * The first line which contains the http protocol and the status code e.g. HTTP/1.1 404 Not Found.
     *
     * @param codeValue the code of the response.
     * @return the first line of the header of the response e.g. HTTP/1.1 404 Not Found.
     */
    private String getHttpReplyHeader(int codeValue) {
        if (replies.containsKey(codeValue)) {
            return String.format(HTTP_1_1_PROTOCOL_HEADER, codeValue, replies.get(codeValue));
        } else {
            return String.format(HTTP_1_1_PROTOCOL_HEADER, DEFAULT_FOUND_STATUS, replies.get(codeValue));
        }
    }

    /**
     * Add all headers to a hash map.
     */
    private void buildHeaders() {
        headers.put("Date", getDate());
        headers.put("Server", "Marshmelo Http Server");
        headers.put("Content-type", resource.getMimeType());
        headers.put("Content-length", Integer.toString(resource.getLength()));
    }

    /**
     * Get formatted date in GMT time zone.
     *
     * @return a string of the date in GMT time zone.
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date()) + " GMT";
    }
}
