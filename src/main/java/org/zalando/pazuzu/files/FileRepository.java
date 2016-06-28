package org.zalando.pazuzu.files;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Simplest implementation using filesystem for storage.
 * Does not provide referential integrity as files can be removed from filesystem even if referenced from a feature.
 * When used inside Docker container this would require volume mapping for storageRoot.
 * <p>
 * To be added to image the files should be referred with "COPY" command in Dockerfile.
 * TODO A Dockerfile preprocessor should then replace filenames with paths relative to storage root with references to REST service.
 * TODO Support for "ADD" command needs to be implemented in CLI: Necessary files should be copied to CLI's machine before build.
 * <p>
 * Further improvements:
 * TODO use a DB table containing either checksums and source file paths or file contents in BLOBS.
 * Latter simplifies deployment and is likely sufficient for all usecases (no big blobs, little number of records)
 */
public class FileRepository {

    private final Path storageRoot;

    public FileRepository(Path storageRoot) {
         this.storageRoot = storageRoot;
    }

    public Collection<Path> listFiles(String shellPattern) throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(storageRoot, shellPattern)) {
            return StreamSupport.stream(directoryStream.spliterator(), false)
                    .collect(Collectors.toList());
        }
    }
}
