package com.marshmelo.fileserver.handlers;

import com.marshmelo.fileserver.exceptions.InternalServerException;
import com.marshmelo.fileserver.exceptions.RequestParsingException;
import com.marshmelo.fileserver.models.HttpRequest;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static com.marshmelo.fileserver.messages.LogMessages.*;

/**
 * A class responsible for handling client connection and providing a response.
 * Each connection will be handled in a separate thread.
 */
public class HTTPRequestHandler implements Runnable {

    private static final String GET_REQUEST = "GET";

    private static final Logger LOGGER = Logger.getLogger(HTTPRequestHandler.class);

    private final Socket socket;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public HTTPRequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        initSocketIOStream();
    }

    @Override
    public void run() {
        try {
            LOGGER.debug(REQUEST_HANDLING_STARTED.formatMessage());
            handleRequest();
            LOGGER.debug(REQUEST_HANDLING_FINISHED.formatMessage());
        } catch (InternalServerException | RequestParsingException | IOException | IllegalArgumentException e) {
            LOGGER.warn(e);
            closeSocket();
        }
    }

    private void handleRequest() throws InternalServerException, IOException, RequestParsingException {
        if (inputStream == null || outputStream == null) {
            return;
        }
        HttpRequest httpRequest;
        try {
            httpRequest = HttpRequestParser.parseRequest(inputStream);
        } catch (IOException e) {
            LOGGER.warn(ERROR_PARSING_HTTP_REQUEST.formatMessage());
            throw e;
        } catch (RequestParsingException e) {
            LOGGER.warn(ERROR_IN_HTTP_REQUEST_HEADER_FORMAT.formatMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            LOGGER.warn(ERROR_IN_HTTP_REQUEST_URL_ENCODING.formatMessage());
            throw e;
        }
        if (httpRequest != null) {
            String method = httpRequest.getMethod();
            RequestHandler handler = createSuitableRequestHandler(method, outputStream);
            handler.handleRequest(httpRequest);
        } else {
            closeSocket();
        }
    }

    /**
     * Force closing the socket connection to prevent client from being blocked.
     */
    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warn(ERROR_CLOSING_SOCKET_CONNECTION.formatMessage(), e);
        }
    }

    /**
     * This method can be used to create suitable request handler depending on the request method e.g. get, post, delete, put.
     * For simplicity and because of the scope of the assignment we can just have a default handler which is the {@link GetRequestHandler}.
     *
     * @param method e.g. GET, POST, DELETE, PUT...
     * @return appropriate {@link RequestHandler}
     */
    private RequestHandler createSuitableRequestHandler(String method, OutputStream outputStream) {
        if (method == null || method.equals(GET_REQUEST)) {
            return new GetRequestHandler(outputStream);
        }
        // Default request handler.
        return new GetRequestHandler(outputStream);
    }

    /**
     * Init socket input and output stream and set socket timeout.
     * Setting socket connection timeout to 1 second to prevent threads from being blocked for any unknown problem like (e.g. networks problems, server/client incompatibility).
     */
    private void initSocketIOStream() throws IOException {
        try {
            this.inputStream = socket.getInputStream();
        } catch (IOException e) {
            LOGGER.warn(ERROR_GETTING_INPUT_STREAM.formatMessage(e.getMessage()));
            throw e;
        }
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            LOGGER.warn(ERROR_GETTING_OUTPUT_STREAM.formatMessage(e.getMessage()));
            throw e;
        }
    }
}
