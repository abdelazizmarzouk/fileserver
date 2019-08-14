package com.marshmelo.fileserver.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ApplicationPropertiesUtilTest {
    @Test
    public void testGettingPropertiesFromApplicationPropertiesFile() {
        // GIVEN
        ApplicationPropertiesUtil properties = new ApplicationPropertiesUtil("application.properties");

        // WHEN
        String computerName = properties.getComputerDefaultName();
        int poolSize = properties.getRequestHandlerPoolSize();
        int port = properties.getServerDefaultPort();
        int timeout = properties.getSocketConnectionTimeoutInMilliSec();

        // THEN
        assertEquals(computerName, "marshmelo1");
        assertEquals(poolSize, 2000);
        assertEquals(port, 8080);
        assertEquals(timeout, 3000);
    }

    @Test
    public void testGettingDefaultPropertiesWhenPropertiesFileDoesNotExists() {
        // GIVEN
        ApplicationPropertiesUtil properties = new ApplicationPropertiesUtil("not_existing.properties");

        // WHEN
        String computerName = properties.getComputerDefaultName();
        int poolSize = properties.getRequestHandlerPoolSize();
        int port = properties.getServerDefaultPort();
        int timeout = properties.getSocketConnectionTimeoutInMilliSec();

        // THEN
        assertEquals(computerName, "marshmelo");
        assertEquals(poolSize, 50);
        assertEquals(port, 8000);
        assertEquals(timeout, 10000);
    }

    @Test
    public void testGettingDefaultPropertiesWhenNotAllPropertiesAreProvided() {
        // GIVEN
        ApplicationPropertiesUtil properties = new ApplicationPropertiesUtil("missing_prop.properties");

        // WHEN
        String computerName = properties.getComputerDefaultName();
        int poolSize = properties.getRequestHandlerPoolSize();
        int port = properties.getServerDefaultPort();
        int timeout = properties.getSocketConnectionTimeoutInMilliSec();

        // THEN
        assertEquals(computerName, "marshmelo1");
        assertEquals(poolSize, 50);
        assertEquals(port, 8000);
        assertEquals(timeout, 10000);
    }
}
