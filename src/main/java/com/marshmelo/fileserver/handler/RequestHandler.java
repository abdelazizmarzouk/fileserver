package com.marshmelo.fileserver.handler;

import com.marshmelo.fileserver.exceptions.InternalServerException;
import com.marshmelo.fileserver.model.HttpRequest;
import com.marshmelo.fileserver.model.HttpResponse;
import com.marshmelo.fileserver.model.Resource;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import static com.marshmelo.fileserver.messages.LogMessages.ERROR_WRITING_RESPONSE_CONTENT;
import static com.marshmelo.fileserver.messages.LogMessages.MULTIPLE_ERROR_OCCURRED;
import static com.marshmelo.fileserver.utils.HttpResponseBuilder.buildResponse;
import static com.marshmelo.fileserver.utils.ResourcesUtil.loadResource;

public abstract class RequestHandler {

    private static final String PAGE_NOT_FOUND_HTML_PAGE = "404.html";
    private static final String INTERNAL_ERROR_HTML_PAGE = "internal_error.html";
    private static final int INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int NOT_FOUND_STATUS = 404;

    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class);

    private static final String HEADER_ENTRY_FORMATTER = "%s: %s";

    private final PrintWriter writer;
    private final BufferedOutputStream bufferedOutputStream;

    RequestHandler(OutputStream outputStream) {
        writer = new PrintWriter(outputStream);
        bufferedOutputStream = new BufferedOutputStream(outputStream);
    }

    public abstract void handleRequest(HttpRequest httpRequest) throws InternalServerException, IOException;

    /**
     * This response is used whenever any exception, rather than page not found, occurs.
     *
     * @throws InternalServerException thrown to indicate the server is not able to handle bad request and internal server error.
     * @throws IOException             thrown when response can not be written.
     */
    private void handleInternalError() throws InternalServerException, IOException {
        Resource resource = loadResource(INTERNAL_ERROR_HTML_PAGE);
        if (resource != null) {
            HttpResponse response = buildResponse(resource, INTERNAL_SERVER_ERROR_STATUS);
            writeResponseHeader(response);
            writeResponseBody(response);
        } else {
            LOGGER.error(MULTIPLE_ERROR_OCCURRED.formatMessage());
            throw new InternalServerException(MULTIPLE_ERROR_OCCURRED.formatMessage());
        }
    }

    /**
     * If the requested resource is not found, a 404 page not found response will be sent as a response.
     *
     * @throws IOException             thrown when static resource can not be loaded or response can not be written.
     * @throws InternalServerException thrown when server is not able to handle bad request or recover from internal problem.
     */
    protected void handleBadRequest() throws IOException, InternalServerException {
        Resource resource = loadResource(PAGE_NOT_FOUND_HTML_PAGE);
        if (resource != null) {
            HttpResponse httpResponse = buildResponse(resource, NOT_FOUND_STATUS);
            writeResponseHeader(httpResponse);
            writeResponseBody(httpResponse);
        } else {
            handleInternalError();
        }
    }

    /**
     * Write response header.
     *
     * @param response the {@link HttpResponse}.
     */
    protected void writeResponseHeader(HttpResponse response) {
        writer.println(response.getHttpReplyHeader());
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            writer.println(String.format(HEADER_ENTRY_FORMATTER, entry.getKey(), entry.getValue()));
        }
        writer.println();
        writer.flush();
    }

    /**
     * Write response body.
     *
     * @param response the {@link HttpResponse} model.
     * @throws IOException thrown when response body can not be written.
     */
    protected void writeResponseBody(HttpResponse response) throws IOException {
        try {
            bufferedOutputStream.write(response.getContent(), 0, response.getLength());
            bufferedOutputStream.flush();
        } catch (IOException e) {
            LOGGER.warn(ERROR_WRITING_RESPONSE_CONTENT.formatMessage());
            throw e;
        }
    }
}
