package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.models.Resource;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResourcesUtilTest {

    @Test
    public void test_LoadExistingResource_GivenCorrectURL() throws IOException {
        // Given
        String requestURL = "/index.html";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNotNull(resource);
        assertTrue(new String(resource.getContent()).contains("Marshmelo Test"));
        assertEquals(resource.getMimeType(), "text/html");
    }

    @Test
    public void test_LoadNonExistingResource_GivenIncorrectURL() throws IOException {
        // Given
        String requestURL = "/not_existing.html";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNull(resource);
    }

    /**
     * Default mime type should be set which is text/html
     */
    @Test
    public void test_LoadExistingResource_WithNewFileExtension_ToVerifyMimeType() throws IOException {
        // Given
        String requestURL = "strange_mime.mimo";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNotNull(resource);
        assertTrue(new String(resource.getContent()).contains("Marshmelo Test"));
        assertEquals(resource.getMimeType(), "application/octet-stream");
    }

    @Test
    public void test_LoadExistingResource_WithJsonExtension() throws IOException {
        // Given
        String requestURL = "example.json";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNotNull(resource);
        assertTrue(new String(resource.getContent()).contains("Sample Konfabulator Widget"));
        assertEquals(resource.getMimeType(), "application/json");
    }

    @Test
    public void test_LoadExistingResource_WithJPGExtension() throws IOException {
        // Given
        String requestURL = "test.jpg";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNotNull(resource);
        assertEquals(resource.getMimeType(), "image/jpeg");
    }

    @Test
    public void test_LoadExistingResource_WithJSExtension() throws IOException {
        // Given
        String requestURL = "test.js";
        // When
        Resource resource = ResourcesUtil.loadResource(requestURL);
        // Then
        assertNotNull(resource);
        assertTrue(new String(resource.getContent()).contains("var v = 3;"));
        assertEquals(resource.getMimeType(), "text/javascript");
    }
}
