package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.exceptions.RequestParsingException;
import com.marshmelo.fileserver.model.HttpRequest;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class HttpRequestParserTest {


    @Test
    public void testParsingHttpRequest() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        assertEquals(httpRequest.getMethod(), "GET");
        assertEquals(httpRequest.getHeaders().size(), 5);
        assertNotNull(httpRequest.getParams());
        assertEquals(httpRequest.getParams().size(), 0);
        assertEquals(httpRequest.getUrl(), "/index.html");
        assertEquals(httpRequest.getHeader("Host"), "www.marshmelo.com");
        assertEquals(httpRequest.getHeader("Accept"), "image/gif, image/jpeg, */*");
        assertEquals(httpRequest.getHeader("Accept-Language"), "en-us");
        assertEquals(httpRequest.getHeader("Accept-Encoding"), "gzip, deflate");
        assertEquals(httpRequest.getHeader("User-Agent"), "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
    }

    @Test(expected = RequestParsingException.class)
    public void testParsingHttpRequestWithoutBlankLineInTheEnd() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        // An exception should be thrown.
    }

    @Test(expected = RequestParsingException.class)
    public void testParsingHttpRequestWithHeaderMissingSemicolon() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language en-us\n" +   // should have semicolon
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        // An exception should be thrown.
    }

    @Test(expected = RequestParsingException.class)
    public void testParsingHttpRequestWithUnsupportedPostMethod() throws IOException, RequestParsingException {
        // Given
        String request =
                "POST /index.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
    }

    @Test(expected = RequestParsingException.class)
    public void testParsingHttpRequestWithIncompatibleVersion() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html HTTP/1.2\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
    }

    @Test(expected = RequestParsingException.class)
    public void testParsingHttpRequestWithMissMatchingNumberOfArgumentsInFirstLine() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html HTTP/1.1 EXTRA\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
    }


    @Test
    public void testParsingHttpRequestAndExtractParameters() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html?param=test&param2=test2&param3= HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        assertEquals(httpRequest.getMethod(), "GET");
        assertEquals(httpRequest.getHeaders().size(), 5);
        assertNotNull(httpRequest.getParams());
        assertEquals(httpRequest.getUrl(), "/index.html");
        assertEquals(httpRequest.getHeader("Host"), "www.marshmelo.com");
        assertEquals(httpRequest.getHeader("Accept"), "image/gif, image/jpeg, */*");
        assertEquals(httpRequest.getHeader("Accept-Language"), "en-us");
        assertEquals(httpRequest.getHeader("Accept-Encoding"), "gzip, deflate");
        assertEquals(httpRequest.getHeader("User-Agent"), "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        assertEquals(httpRequest.getParams().size(), 3);
        assertEquals(httpRequest.getParam("param"), "test");
        assertEquals(httpRequest.getParam("param2"), "test2");
        assertEquals(httpRequest.getParam("param3"), "");
    }

    /**
     * Parameter three does not have equal sign in the end, so it will not be considered a parameter.
     */
    @Test
    public void testParsingHttpRequestAndExtractParametersWithoutEqualForThirdParameter() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html?param=test&param2=test2&param3 HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        assertEquals(httpRequest.getMethod(), "GET");
        assertEquals(httpRequest.getHeaders().size(), 5);
        assertNotNull(httpRequest.getParams());
        assertEquals(httpRequest.getUrl(), "/index.html");
        assertEquals(httpRequest.getHeader("Host"), "www.marshmelo.com");
        assertEquals(httpRequest.getHeader("Accept"), "image/gif, image/jpeg, */*");
        assertEquals(httpRequest.getHeader("Accept-Language"), "en-us");
        assertEquals(httpRequest.getHeader("Accept-Encoding"), "gzip, deflate");
        assertEquals(httpRequest.getHeader("User-Agent"), "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        assertEquals(httpRequest.getParams().size(), 2);
        assertEquals(httpRequest.getParam("param"), "test");
        assertEquals(httpRequest.getParam("param2"), "test2");
        assertNull(httpRequest.getParam("param3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParsingHttpRequestAndExtractParametersWithUnAcceptedParameter() throws IOException, RequestParsingException {
        // Given
        String request =
                "GET /index.html?param=test&param2=% HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
        // Then
        // IllegalArgumentException exception when encoding the URL parameters.
    }

}
