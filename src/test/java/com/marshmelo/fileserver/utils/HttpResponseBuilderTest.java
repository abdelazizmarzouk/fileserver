package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.handlers.HttpResponseBuilder;
import com.marshmelo.fileserver.models.HttpResponse;
import com.marshmelo.fileserver.models.Resource;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HttpResponseBuilderTest {

    @Test
    public void testBuildHttpResponseGivenResource() throws IOException {
        // Given
        Resource resource = ResourcesUtil.loadResource("/index.html");

        // When
        HttpResponse httpResponse = HttpResponseBuilder.buildResponse(resource, 200);

        // Then
        assertEquals(httpResponse.getContent(), resource.getContent());
        assertEquals(httpResponse.getLength(), resource.getContent().length);
        assertEquals(httpResponse.getStatus(), 200);
        assertEquals(httpResponse.getHttpReplyHeader(), "HTTP/1.1 200 OK");
        assertEquals(httpResponse.getMimeType(), "text/html");
        assertEquals(httpResponse.getHeaders().size(), 4);
        assertEquals(httpResponse.getHeaders().get("Server"), "Marshmelo Http Server");
        assertEquals(httpResponse.getHeaders().get("Content-type"), "text/html");
        assertEquals(httpResponse.getHeaders().get("Content-length"), Integer.toString(resource.getContent().length));
        assertTrue(httpResponse.getHeaders().get("Date").contains("GMT"));
    }
}
