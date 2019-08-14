package com.marshmelo.fileserver;

import com.marshmelo.fileserver.handlers.HTTPRequestHandler;
import com.marshmelo.fileserver.utils.PropertiesUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.marshmelo.fileserver.messages.LogMessages.*;

public class FileServer {

    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final Logger LOGGER = Logger.getLogger(FileServer.class);
    private static PropertiesUtil properties;

    public static void main(String[] args) {
        FileServer starter = new FileServer();
        try {
            starter.startServer();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void startServer() throws IOException {
        properties = new PropertiesUtil(APPLICATION_PROPERTIES);
        ExecutorService executor = Executors.newFixedThreadPool(properties.getRequestHandlerPoolSize());
        ServerSocket socket;
        int port = properties.getServerDefaultPort();
        int socketTimeout = properties.getSocketConnectionTimeoutInMilliSec();
        try {
            LOGGER.info(START_APPLICATION.formatMessage(findHostName()));
            socket = new ServerSocket(port);
            LOGGER.info(SERVER_INITIALIZED.formatMessage(port, port));
        } catch (IOException e) {
            LOGGER.error(SERVER_INITIALIZATION_FAILURE.formatMessage(port), e);
            throw e;
        }
        while (true) {
            try {
                Socket accept = socket.accept();
                accept.setSoTimeout(socketTimeout);
                HTTPRequestHandler requestHandler = new HTTPRequestHandler(accept);
                executor.execute(requestHandler);
            } catch (SocketException e) {
                LOGGER.warn(ERROR_SETTING_SOCKET_TIMEOUT.formatMessage(e.getMessage()));
            } catch (IOException e) {
                LOGGER.warn(ERROR_ACCEPTING_SOCKET_CONNECTION.formatMessage(), e);
            }
        }
    }

    /**
     * Find the name of the computer where the server is running, if computer name can not be resolved an "unknown" string will be returned.
     *
     * @return computer name.
     */
    private String findHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.warn(UNKNOWN_HOST_NAME.formatMessage(), e);
            return properties.getComputerDefaultName();
        }
    }
}
