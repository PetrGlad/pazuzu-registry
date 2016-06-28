package org.zalando.pazuzu.files;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;

public class FileRepositoryTest {
    @Test
    public void listFiles() throws Exception {
        FileRepository fs = new FileRepository(Paths.get("/"));
        assertFalse("Can list a directory", fs.listFiles("*").isEmpty());
    }
}