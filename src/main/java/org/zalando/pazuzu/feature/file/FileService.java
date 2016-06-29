package org.zalando.pazuzu.feature.file;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Simplest implementation using filesystem for storage.
 * Does not provide referential integrity as file can be removed from filesystem even if referenced from a feature.
 * When used inside Docker container this would require volume mapping for storageRoot.
 * <p>
 * To be added to image the file should be referred with "COPY" command in Dockerfile.
 * TODO A Dockerfile preprocessor should then replace filenames with paths relative to storage root with references to REST service.
 * TODO Support for "ADD" command needs to be implemented in CLI: Necessary file should be copied to CLI's machine before build.
 * <p>
 * Further improvements:
 * TODO use a DB table containing either checksums and source file paths or file contents in BLOBS.
 * Latter simplifies deployment and is likely sufficient for all usecases (no big blobs, little number of records)
 */
@Service
public class FileService {

    private final Path storageRoot;

    public FileService(Path storageRoot) {
        this.storageRoot = storageRoot;
    }

    public FileService() {
        this(Paths.get("./pazuzu/files/"));
    }

    public Collection<Path> searchFiles(String shellPattern) throws IOException {
        // TODO (feature) Support nested paths, also maybe just looking for substrings might be sufficient.
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(storageRoot, shellPattern)) {
            return StreamSupport.stream(paths.spliterator(), false)
                    .map(p -> storageRoot.relativize(p))
                    .collect(Collectors.toList());
        }
    }

    public void putFile(Path path, InputStream content) throws IOException {
        Path realPath = getRealPath(path);
        // TODO (feature) Support nested paths // Files.createDirectories(realPath.getParent());
        Files.copy(content, realPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public InputStream getContentStream(Path fileName) throws IOException {
        return Files.newInputStream(getRealPath(fileName), StandardOpenOption.READ);
    }

    private Path getRealPath(Path filePath) {
        if (0 == filePath.getNameCount()) {
            throw new IllegalArgumentException("File path should not be empty.");
        }
        if (1 < filePath.getNameCount()) {
            // TODO (feature) Support nested paths (see #searchFiles)
            throw new IllegalArgumentException("Nested paths are not supported (yet).");
        }
        // TODO (security, review) Ensure that ".."s do not allow accessing parent dirs. (_Also_ should not allow them in calls to FileResource)
        return storageRoot.resolve(filePath);
    }
}
