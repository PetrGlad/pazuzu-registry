package org.zalando.pazuzu.feature.file;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.zalando.pazuzu.exception.Error.INTERNAL_SERVER_ERROR;

/**
 * Serving list of file available for addition to images.
 * <p>
 * FIXME Filepaths are unsuitable generally as  "/" and filename extensions (suffixes) interfere with spring's request mapping.
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/files")
public class FileResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);

    @Autowired
    private final FileService fileService;

    @Autowired
    public FileResource(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/query/{queryString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> search(@PathVariable String queryString) throws ServiceException {
        try {
            return fileService.searchFiles(queryString).stream()
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.error("File search error", e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, "Error searching files");
        }
    }

    @RequestMapping(value = "/content/{pathString}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getContent(@PathVariable String pathString, OutputStream responseStream) throws ServiceException {
        try {
            // TODO (implementation) Return 404 if not found
            IOUtils.copy(fileService.getContentStream(Paths.get(pathString)), responseStream);
        } catch (IOException e) {
            LOG.error("File search error", e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, "Error getting file content.");
        }
    }

    @RequestMapping(value = "/content/{pathString}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void putContent(@PathVariable String pathString, InputStream inputStream) throws ServiceException {
        try {
            fileService.putFile(Paths.get(pathString), inputStream);
        } catch (IOException e) {
            LOG.error("File search error", e);
            throw new ServiceException(INTERNAL_SERVER_ERROR, "Error uploading file.");
        }
    }
}
