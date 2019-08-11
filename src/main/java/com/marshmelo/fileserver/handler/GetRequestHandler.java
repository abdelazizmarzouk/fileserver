package com.marshmelo.fileserver.handler;

import com.marshmelo.fileserver.exceptions.InternalServerException;
import com.marshmelo.fileserver.model.HttpRequest;
import com.marshmelo.fileserver.model.HttpResponse;
import com.marshmelo.fileserver.model.Resource;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;

import static com.marshmelo.fileserver.messages.LogMessages.ERROR_FILE_NOT_FOUND;
import static com.marshmelo.fileserver.utils.HttpResponseBuilder.buildResponse;
import static com.marshmelo.fileserver.utils.ResourcesUtil.loadResource;

public class GetRequestHandler extends RequestHandler {

    private static final int OK_STATUS = 200;
    private static final Logger LOGGER = Logger.getLogger(GetRequestHandler.class);

    public GetRequestHandler(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public void handleRequest(HttpRequest httpRequest) throws InternalServerException, IOException {
        String requestURL = httpRequest.getUrl();
        Resource resource = loadResource(requestURL);
        if (resource != null) {
            HttpResponse response = buildResponse(resource, OK_STATUS);
            writeResponseHeader(response);
            writeResponseBody(response);
        } else {
            LOGGER.info(ERROR_FILE_NOT_FOUND.formatMessage(requestURL));
            handleBadRequest();
        }
    }
}
