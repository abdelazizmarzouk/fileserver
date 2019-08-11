package com.marshmelo.fileserver.handler;

import com.marshmelo.fileserver.exceptions.InternalServerException;
import com.marshmelo.fileserver.exceptions.RequestParsingException;
import com.marshmelo.fileserver.model.HttpRequest;
import com.marshmelo.fileserver.utils.HttpRequestParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertTrue;

public class GetRequestHandlerTest {

    @Test
    public void testGetRequestHandlerForIndexHtmlFile() throws IOException, InternalServerException, RequestParsingException {
        // Given
        OutputStream outputStream = new FileOutputStream("handler.txt");
        GetRequestHandler handler = new GetRequestHandler(outputStream);
        String request =
                "GET /index.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(new ByteArrayInputStream(request.getBytes()));
        handler.handleRequest(httpRequest);
        // Then
        FileInputStream inputStream = new FileInputStream("handler.txt");
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        String response = writer.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-type: text/html"));
        assertTrue(response.contains("Server: Marshmelo Http Server"));
        assertTrue(response.contains("Content-length: 127"));
        assertTrue(response.contains("<title>Taste Marshmelo</title>"));
    }

    @Test
    public void testGetRequestHandlerForNonExistingResourceFile() throws IOException, InternalServerException, RequestParsingException {
        // Given
        OutputStream outputStream = new FileOutputStream("handler2.txt");
        GetRequestHandler handler = new GetRequestHandler(outputStream);
        String request =
                "GET /index1.html HTTP/1.1\n" +
                        "Host: www.marshmelo.com\n" +
                        "Accept: image/gif, image/jpeg, */*\n" +
                        "Accept-Language: en-us\n" +
                        "Accept-Encoding: gzip, deflate\n" +
                        "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\n" +
                        "\n";
        // When
        HttpRequest httpRequest = HttpRequestParser.parseRequest(new ByteArrayInputStream(request.getBytes()));
        handler.handleRequest(httpRequest);
        // Then
        FileInputStream inputStream = new FileInputStream("handler2.txt");
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer);
        String response = writer.toString();
        assertTrue(response.contains("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("Content-type: text/html"));
        assertTrue(response.contains("Server: Marshmelo Http Server"));
        assertTrue(response.contains("Content-length: 134"));
        assertTrue(response.contains("<h1>404 - Page not found!</h1>"));
    }

}
