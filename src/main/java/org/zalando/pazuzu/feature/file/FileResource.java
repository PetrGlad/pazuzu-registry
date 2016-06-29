package org.zalando.pazuzu.feature.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.zalando.pazuzu.exception.ServiceException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.zalando.pazuzu.exception.Error.INTERNAL_SERVER_ERROR;

/**
 * Serving list of file available for addition to images.
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
}
