package com.marshmelo.fileserver.utils;

import com.marshmelo.fileserver.messages.LogMessages;
import com.marshmelo.fileserver.models.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.marshmelo.fileserver.messages.LogMessages.ERROR_READING_FILE;

public class ResourcesUtil {

    private static final String INDEX_HTML_PAGE = "index.html";

    private static final Logger LOGGER = Logger.getLogger(ResourcesUtil.class);
    private static final Map<String, Resource> resources = new ConcurrentHashMap<>();
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final Map<String, String> fileToMimeTypeMap = new HashMap<>();
    private static final String STATIC_RESOURCE_FOLDER_PATH = "static/";

    // Reference https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types
    static {
        fileToMimeTypeMap.put("css", "text/css");
        fileToMimeTypeMap.put("csv", "text/csv");
        fileToMimeTypeMap.put("htm", "text/html");
        fileToMimeTypeMap.put("html", "text/html");
        fileToMimeTypeMap.put("jpeg", "image/jpeg");
        fileToMimeTypeMap.put("jpg", "image/jpeg");
        fileToMimeTypeMap.put("js", "text/javascript");
        fileToMimeTypeMap.put("json", "application/json");
        fileToMimeTypeMap.put("jsonld", "application/ld+json");
        fileToMimeTypeMap.put("png", "image/png");
        fileToMimeTypeMap.put("pdf", "application/pdf");
        fileToMimeTypeMap.put("svg", "image/svg+xml");
        fileToMimeTypeMap.put("swf", "application/x-shockwave-flash");
        fileToMimeTypeMap.put("webp", "image/webp");
        fileToMimeTypeMap.put("xhtml", "application/xhtml+xml");
        fileToMimeTypeMap.put("xml", "application/xml");
        fileToMimeTypeMap.put("zip", "application/zip");
        fileToMimeTypeMap.put("7z", "application/x-7z-compressed");
    }

    /**
     * Loads a resource from the filesystem if it is not cached, o.w. load from cache.
     * Null is returned if resource is not found.
     *
     * @param requestURL
     * @return {@link Resource}
     * @throws IOException if the resource file is not found.
     */
    public static Resource loadResource(String requestURL) throws IOException {
        if (resources.containsKey(requestURL)) {
            return resources.get(requestURL);
        }
        String resourcePath = buildResourcePath(requestURL);
        if (resourcePath == null) {
            return null;
        }
        File file = new File(resourcePath);
        if (!file.exists()) {
            return null;
        }
        String mimeType = findMimeType(file);
        byte[] content;
        try {
            content = readFileAsByteArray(file);
        } catch (IOException e) {
            LOGGER.warn(LogMessages.ERROR_READING_FILE_CONTENT.formatMessage(resourcePath));
            throw e;
        }
        Resource resource = new Resource(content, mimeType);
        resources.put(requestURL, resource);
        return resource;
    }

    /**
     * Build the actual resource path on the file system from the given request URL.
     *
     * @param requestURL the URL in the request.
     * @return actual path of the resource on the file system.
     */
    public static String buildResourcePath(String requestURL) {
        String filePath = requestURL.endsWith("/") ? requestURL + INDEX_HTML_PAGE : requestURL;
        int length = filePath.length();
        filePath = filePath.startsWith("/") ? filePath.substring(1, length) : filePath;
        filePath = STATIC_RESOURCE_FOLDER_PATH + filePath;
        URL resource = ResourcesUtil.class.getClassLoader().getResource(filePath);
        return resource == null ? null : resource.getFile();
    }

    /**
     * Read file content as an array of bytes.
     *
     * @param file the file to read its content.
     * @return an array of bytes representing the content of the file.
     * @throws IOException thrown when the content of a given file can not be read.
     */
    public static byte[] readFileAsByteArray(File file) throws IOException {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            LOGGER.warn(ERROR_READING_FILE.formatMessage(file.getName()));
            throw e;
        }
    }

    /**
     * Find resource mime type of the file using the file extension.
     *
     * @param file file to find its content type.
     * @return content type e.g. text/html
     */
    public static String findMimeType(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        String mimeType = fileToMimeTypeMap.get(extension);
        if (mimeType == null) {
            LOGGER.error(LogMessages.ERROR_FINDING_CONTENT_TYPE.formatMessage(file.getName()));
            return APPLICATION_OCTET_STREAM;
        }
        return mimeType;
    }
}
