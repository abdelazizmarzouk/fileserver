package com.marshmelo.fileserver.messages;

public enum LogMessages {
    START_APPLICATION("............................Starting Application on %s......................"),
    UNKNOWN_HOST_NAME("Hostname can not be resolved."),
    SERVER_INITIALIZED("Server initialized on port: %s and can be reached via http:\\\\localhost:%s."),
    SERVER_INITIALIZATION_FAILURE("Failed to initialize server on port %s, check if this port is used by another application."),
    REQUEST_HANDLING_STARTED("Started handling request"),
    REQUEST_HANDLING_FINISHED("Finished handling request"),
    ERROR_ACCEPTING_SOCKET_CONNECTION("Error accepting socket connection, an action might be necessary if this exception is frequent."),
    ERROR_GETTING_INPUT_STREAM("Error getting input stream from connection."),
    ERROR_GETTING_OUTPUT_STREAM("Error getting output stream from connection."),
    ERROR_SETTING_SOCKET_TIMEOUT("Failed to set timeout."),
    ERROR_FINDING_CONTENT_TYPE("Error while finding the content type for the file with the path %s and the default path application/octet-stream will be set."),
    ERROR_READING_FILE("Error reading file %s."),
    ERROR_FILE_NOT_FOUND("File %s was not found."),
    ERROR_CLOSING_SOCKET_CONNECTION("Error closing socket connection."),
    MULTIPLE_ERROR_OCCURRED("Multiple problems occurred while handling user request."),
    ERROR_PARSING_HTTP_REQUEST("Error occurred while parsing http request header."),
    ERROR_IN_HTTP_REQUEST_HEADER_FORMAT("The request header is not correctly formatted."),
    ERROR_IN_HTTP_REQUEST_URL_ENCODING("The request URL contains not allowed characters."),
    ERROR_READING_FILE_CONTENT("Unable to read contents of the file with path %s."),
    ERROR_WRITING_RESPONSE_CONTENT("Unable to write contents to the response."),
    ERROR_INITIAL_HEADER_LINE("Initial line of HTTP request does not follow the correct format."),
    ERROR_ARGUMENTS_IN_FIRST_LINE_OF_REQUEST_SHOULD_BE_THREE("First line of HTTP request does not have 3 arguments."),
    ERROR_HTTP_VERSION_UNSUPPORTED("The HTTP version is not supported or header mismatch."),
    ERROR_HTTP_METHOD_UNSUPPORTED("Unsupported request method."),
    ERROR_HEADER_INCORRECT_FORMAT("Header not correctly formatted."),
    ERROR_HEADER_SHOULD_END_WITH_BLANK_LINE("Header is not ended with a blank line."),
    ERROR_LOADING_APPLICATION_PROPERTIES("An error occurred while loading application properties, the application will run with the default configurations."),
    ERROR_PARSING_PROPERTY_TO_INTEGER("Error happened when parsing value of the property %s to integer"),
    INFO_PROPERTY_IS_NOT_CONFIGURED("Default property %s is not configured in the properties file and the default %s will be set."),
    ;

    private String message;

    LogMessages(String message) {
        this.message = message;
    }

    public String formatMessage(Object... parameters) {
        return String.format(message, parameters);
    }
}
