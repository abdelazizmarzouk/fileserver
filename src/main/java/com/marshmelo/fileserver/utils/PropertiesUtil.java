package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.messages.LogMessages;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Read properties from application.properties file. If the properties can not be read the default values will be returned.
 */
public class PropertiesUtil {

    private static final int SERVER_DEFAULT_PORT = 8000;
    private static final String SERVER_DEFAULT_PORT_PROPERTY = "file.server.port";
    private static final String UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL = "marshmelo";
    private static final String UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL_PROPERTY = "file.server.default.computer.name";
    private static final int REQUEST_HANDLER_POOL_SIZE = 50;
    private static final String REQUEST_HANDLER_POOL_SIZE_PROPERTY = "file.server.pool.size";
    private static final int SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC = 10000;
    private static final String SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY = "file.server.connection.timeout.milliseconds";
    private static final Logger LOGGER = Logger.getLogger(PropertiesUtil.class);

    private Properties properties;


    public PropertiesUtil(String fileName) {
        URL resource = getClass().getClassLoader().getResource(fileName);
        if (resource != null) {
            String filePath = resource.getFile();
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                LOGGER.info(LogMessages.ERROR_GETTING_APPLICATION_PROPERTIES.formatMessage());
            }
            properties = new Properties();
            try {
                if (inputStream != null) {
                    properties.load(inputStream);
                }
            } catch (IOException e) {
                LOGGER.info(LogMessages.ERROR_LOADING_APPLICATION_PROPERTIES.formatMessage());
            }
        }
    }

    public int getServerDefaultPort() {
        if (properties == null) {
            return SERVER_DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(properties.getProperty(SERVER_DEFAULT_PORT_PROPERTY));
        } catch (NumberFormatException e) {
            return SERVER_DEFAULT_PORT;
        }
    }

    public String getComputerDefaultName() {
        return properties == null ?
                UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL :
                properties.getProperty(UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL_PROPERTY);
    }

    public int getRequestHandlerPoolSize() {
        if (properties == null) {
            return REQUEST_HANDLER_POOL_SIZE;
        }
        try {
            return Integer.parseInt(properties.getProperty(REQUEST_HANDLER_POOL_SIZE_PROPERTY));
        } catch (NumberFormatException e) {
            return REQUEST_HANDLER_POOL_SIZE;
        }
    }

    public int getSocketConnectionTimeoutInMilliSec() {
        if (properties == null) {
            return SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC;
        }
        try {
            return Integer.parseInt(properties.getProperty(SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY));
        } catch (NumberFormatException e) {
            return SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC;
        }
    }
}
