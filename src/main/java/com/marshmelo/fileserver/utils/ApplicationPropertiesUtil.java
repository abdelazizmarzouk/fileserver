package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.messages.LogMessages;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Read properties from application.properties file. If the properties can not be read the default values will be returned.
 */
public class ApplicationPropertiesUtil {

    private static final int SERVER_DEFAULT_PORT = 8000;
    private static final String SERVER_DEFAULT_PORT_PROPERTY = "file.server.port";
    private static final String UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL = "marshmelo";
    private static final String UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL_PROPERTY = "file.server.default.computer.name";
    private static final int REQUEST_HANDLER_POOL_SIZE = 50;
    private static final String REQUEST_HANDLER_POOL_SIZE_PROPERTY = "file.server.pool.size";
    private static final int SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC = 10000;
    private static final String SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY = "file.server.connection.timeout.milliseconds";
    private static final Logger LOGGER = Logger.getLogger(ApplicationPropertiesUtil.class);

    private Properties properties;

    public ApplicationPropertiesUtil(String fileName) {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        properties = new Properties();
        try {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            LOGGER.info(LogMessages.ERROR_LOADING_APPLICATION_PROPERTIES.formatMessage());
        }
    }

    public int getServerDefaultPort() {
        if (properties == null) {
            return SERVER_DEFAULT_PORT;
        }
        String portProperty = properties.getProperty(SERVER_DEFAULT_PORT_PROPERTY);
        if (portProperty == null) {
            LOGGER.info(LogMessages.INFO_PROPERTY_IS_NOT_CONFIGURED.formatMessage(SERVER_DEFAULT_PORT_PROPERTY, SERVER_DEFAULT_PORT));
            return SERVER_DEFAULT_PORT;
        }
        try {
            return Integer.parseInt(portProperty);
        } catch (NumberFormatException e) {
            LOGGER.warn(LogMessages.ERROR_PARSING_PROPERTY_TO_INTEGER.formatMessage(SERVER_DEFAULT_PORT_PROPERTY));
            return SERVER_DEFAULT_PORT;
        }
    }

    public String getComputerDefaultName() {
        if (properties == null) {
            return UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL;
        }
        String defaultName = properties.getProperty(UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL_PROPERTY);
        if (defaultName == null) {
            LOGGER.info(LogMessages.INFO_PROPERTY_IS_NOT_CONFIGURED.formatMessage(UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL_PROPERTY, UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL));
            return UNRESOLVED_COMPUTER_NAME_DEFAULT_LABEL;
        }
        return defaultName;
    }

    public int getRequestHandlerPoolSize() {
        if (properties == null) {
            return REQUEST_HANDLER_POOL_SIZE;
        }
        String poolSize = properties.getProperty(REQUEST_HANDLER_POOL_SIZE_PROPERTY);
        if (poolSize == null) {
            LOGGER.info(LogMessages.INFO_PROPERTY_IS_NOT_CONFIGURED.formatMessage(REQUEST_HANDLER_POOL_SIZE_PROPERTY, REQUEST_HANDLER_POOL_SIZE));
        }
        try {
            return Integer.parseInt(poolSize);
        } catch (NumberFormatException e) {
            LOGGER.warn(LogMessages.ERROR_PARSING_PROPERTY_TO_INTEGER.formatMessage(REQUEST_HANDLER_POOL_SIZE_PROPERTY));
            return REQUEST_HANDLER_POOL_SIZE;
        }
    }

    public int getSocketConnectionTimeoutInMilliSec() {
        if (properties == null) {
            return SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC;
        }
        String timeout = properties.getProperty(SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY);
        if (timeout == null) {
            LOGGER.info(LogMessages.INFO_PROPERTY_IS_NOT_CONFIGURED.formatMessage(SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY, SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC));
            return SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC;
        }
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            LOGGER.warn(LogMessages.ERROR_PARSING_PROPERTY_TO_INTEGER.formatMessage(SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC_PROPERTY));
            return SOCKET_CONNECTION_TIMEOUT_IN_MILLI_SEC;
        }
    }
}
