package com.marshmelo.fileserver.handlers;

import com.marshmelo.fileserver.exceptions.RequestParsingException;
import com.marshmelo.fileserver.models.HttpRequest;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static com.marshmelo.fileserver.messages.LogMessages.*;

/**
 * This class is responsible for parsing the http request which follows the correct format.
 * For more information goto: https://tools.ietf.org/html/rfc7231#section-4.3.1.
 * This class handles only GET and HEAD methods and ignore other methods.
 * In any case if the request header does not follow the correct format a {@link RequestParsingException} will be raised.
 * e.g. GET /index.html?test=true HTTP/1.1
 */
public class HttpRequestParser {

    private static final String GET_METHOD_NAME = "GET";
    private static final String HEAD_METHOD_NAME = "HEAD";
    private static final String DEFAULT_URL_ENCODING_CHARSET = "UTF-8";
    private static final String HTTP_PROTOCOL_VERSION = "HTTP/1.1";
    private BufferedReader reader;

    private HttpRequestParser(InputStream inputStream) {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    /**
     * Parse http request headers and return an {@link HttpRequest} model which contains all information about a request.
     *
     * @param inputStream socket input stream.
     * @return {@link HttpRequest} model.
     * @throws IOException             thrown when there is an IO exception.
     * @throws RequestParsingException thrown when there are parsing problems or there is a formatting miss match.
     */
    public static HttpRequest parseRequest(InputStream inputStream) throws IOException, RequestParsingException, IllegalArgumentException {
        return new HttpRequestParser(inputStream).parseRequest();
    }

    /**
     * Parse http request headers and return an {@link HttpRequest} model which contains all information about a request.
     *
     * @return {@link HttpRequest} model.
     * @throws IOException             thrown when there is an IO exception.
     * @throws RequestParsingException thrown when there are parsing problems or there is a formatting miss match.
     */
    private HttpRequest parseRequest() throws IOException, RequestParsingException, IllegalArgumentException {
        Map<String, String> urlParameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        String[] firstLineParts = parseFirstLine(reader.readLine());
        String method = firstLineParts[0];
        String url = parseURLAndExtractParameters(firstLineParts[1], urlParameters);
        parseHeaders(headers);
        return new HttpRequest(method, url, headers, urlParameters);
    }

    /**
     * Parse and extract parameters of the given URL.
     *
     * @param urlString     url string to parse e.g. /index.html?test=true, /index.html
     * @param urlParameters a hash map where the parameters will be added.
     * @return the url without parameters, the parameters will be added to the map.
     * @throws UnsupportedEncodingException throw this exception if an invalid character was found in the URL.
     */
    private String parseURLAndExtractParameters(String urlString, Map<String, String> urlParameters) throws UnsupportedEncodingException, IllegalArgumentException {
        int index = urlString.indexOf('?');
        if (index < 0) {
            return urlString;
        }
        String url = URLDecoder.decode(urlString.substring(0, index), DEFAULT_URL_ENCODING_CHARSET);
        String urlParametersString = urlString.substring(index + 1);
        String[] parameters = urlParametersString.split("&");

        for (int i = 0; i < parameters.length; i++) {
            String[] keyValue = parameters[i].split("=");
            String key = URLDecoder.decode(keyValue[0], DEFAULT_URL_ENCODING_CHARSET);
            if (keyValue.length == 2) {
                String value = URLDecoder.decode(keyValue[1], DEFAULT_URL_ENCODING_CHARSET);
                urlParameters.put(key, value);
            } else if (keyValue.length == 1 && parameters[i].indexOf('=') == parameters[i].length() - 1) {
                // Parameter without a value.
                urlParameters.put(key, "");
            }
        }
        return url;
    }

    /**
     * Parse and validate the first line of the header e.g. GET /index.html?test=true HTTP/1.1
     *
     * @param firstLine first line of the request which contains the method, URL and HTTP protocol version.
     * @return the three main parts of the first line
     * @throws RequestParsingException if the first line is not matching the standard format.
     */
    private String[] parseFirstLine(String firstLine) throws RequestParsingException {
        if (firstLine == null || firstLine.length() == 0 || Character.isWhitespace(firstLine.charAt(0))) {
            throw new RequestParsingException(ERROR_INITIAL_HEADER_LINE.formatMessage());
        }
        String[] splits = firstLine.split(" ");
        if (splits.length != 3) {
            throw new RequestParsingException(ERROR_ARGUMENTS_IN_FIRST_LINE_OF_REQUEST_SHOULD_BE_THREE.formatMessage());
        }
        if (!splits[2].equals(HTTP_PROTOCOL_VERSION)) {
            throw new RequestParsingException(ERROR_HTTP_VERSION_UNSUPPORTED.formatMessage());
        }
        if (!splits[0].equals(GET_METHOD_NAME) && !splits[0].equals(HEAD_METHOD_NAME)) {
            throw new RequestParsingException(ERROR_HTTP_METHOD_UNSUPPORTED.formatMessage());
        }
        return splits;
    }


    /**
     * Parse headers and add them to the header hash map.
     *
     * @throws IOException             thrown if there is a problem reading header lines.
     * @throws RequestParsingException thrown if the headers are not correctly formatted.
     */
    private void parseHeaders(Map<String, String> headers) throws IOException, RequestParsingException {
        String line;
        int i;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if ((i = line.indexOf(':')) < 0) {
                throw new RequestParsingException(ERROR_HEADER_INCORRECT_FORMAT.formatMessage());
            } else {
                headers.put(line.substring(0, i).toLowerCase(), line.substring(i + 1).trim());
            }
        }
        if (line == null) {
            throw new RequestParsingException(ERROR_HEADER_SHOULD_END_WITH_BLANK_LINE.formatMessage());
        }
    }
}
