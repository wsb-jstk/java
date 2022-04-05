package com.campgemini.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link Files} is an utility class which operates on {@link Path}
 * 
 * @see Files
 */
@Slf4j
class FilesTest {

    private static final String PATH_TO_EXISTING_DIR = "src/test/resources";

    private static final String PATH_TO_EXISTING_FILE = "src/test/resources/sampleFile.txt";

    private static final String PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT = "nonExistingFile.txt";

    private static final String PATH_TO_NON_EXISTING_FILE_WITH_ROOT1 = "c:\\temp\\test1.txt";

    private static final String PATH_TO_NON_EXISTING_FILE_WITH_ROOT2 = "c:\\temp\\test2.txt";

    @Test
    @Disabled("File C:\\temp.txt does not exists")
    void shouldReadContentOfFile_andSumValuesFromEachLine() throws IOException {
        // given
        final Path path = Paths.get("C:\\temp.txt");
        final List<String> strings = Files.readAllLines(path);
        // when
        final int sum = strings.stream()
                               .mapToInt(Integer::parseInt)
                               .sum();
        // then
        assertEquals(10, sum);
    }

    @Test
    void list_existingPath() throws IOException {
        // given
        final Path path = Paths.get(PATH_TO_EXISTING_DIR);
        // when
        final List<Path> files = Files.list(path)
                                      .collect(Collectors.toList());
        // then
        log.debug("Files: {}", files);
        assertThat(files.size(), equalTo(2));
    }

    @Test
    void list_nonExistingPath() {
        // given
        final Path path = Paths.get("/not-existing-path");
        // when
        assertThrows(NoSuchFileException.class, () -> Files.list(path));
    }

    @Test
    void find_existingPath() throws IOException {
        // given
        final Path path = Paths.get(PATH_TO_EXISTING_DIR);
        final String searchPattern = ".properties";
        final int maxDepth = 2;
        // when
        final List<Path> files = Files.find(path, maxDepth, (p, a) -> {
            // Path.endsWith() checks the whole file name; I will use the
            // Path.toString().endsWith() instead
            return p.toString()
                    .endsWith(searchPattern) && a.isRegularFile();
        })
                                      .collect(Collectors.toList());
        // then
        log.debug("Files: {}", files);
        assertThat(files.size(), equalTo(1));
    }

    @Test
    void find_notExistingPath() {
        // given
        final Path path = Paths.get("/not-existing-path");
        final int maxDepth = 3;
        // when
        assertThrows(NoSuchFileException.class, () -> Files.find(path, maxDepth, (p, a) -> true));
    }

    @Test
    void walk_existingPath() throws IOException {
        // given
        final Path path = Paths.get(PATH_TO_EXISTING_DIR);
        final String searchPattern = ".properties";
        final int maxDepth = 3;
        // when
        final List<Path> files = Files.walk(path, maxDepth)//
                                      .filter(p -> p.toString()
                                                    .endsWith(searchPattern))//
                                      .collect(Collectors.toList());
        // then
        log.debug("Files: {}", files);
        assertThat(files.size(), equalTo(1));
    }

    @Test
    void move_existingFile_destFilesDoesNotExists() throws IOException {
        // given
        final Path srcFile = Files.createTempFile("source", "UnitTestFiles");
        final Path dstDir = Files.createTempDirectory("dstDir");
        final Path dstFile = dstDir.resolve("destinationUnitTestFiles.txt");
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(dstDir));
        assertFalse(Files.exists(dstFile));
        try {
            // when
            final Path moved = Files.move(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
            // then
            assertTrue(Files.exists(moved));
            assertTrue(Files.isSameFile(moved, dstFile));
            assertFalse(Files.exists(srcFile));
        } finally {
            // alternative to File.deleteOnExit - just an example
            Files.deleteIfExists(srcFile);
            Files.deleteIfExists(dstFile); // remove new file (otherwise there will be DirectoryNotEmptyException when
                                           // trying to remove dir)
            Files.deleteIfExists(dstDir);
        }
    }

    @Test
    void move_existingFile_destFileExists_replaceExisting() throws IOException {
        // given
        final Path srcFile = Files.createTempFile("source", "UnitTestFiles");
        final Path dstFile = Files.createTempFile("destination", "UnitTestFiles");
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(dstFile));
        // plan actions
        srcFile.toFile()
               .deleteOnExit();
        dstFile.toFile()
               .deleteOnExit();
        // when
        final Path moved = Files.move(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
        // Files.delete(srcFile); // NoSuchFileException (enthuware.ocpjp.v8.2.1805)
        // then
        assertTrue(Files.exists(moved));
        assertTrue(Files.isSameFile(moved, dstFile));
        assertFalse(Files.exists(srcFile));
    }

    @Test
    void move_existingFile_destFileExists_differentName_atomicMove() throws IOException {
        // given
        final Path srcFile = Files.createTempFile("source", "UnitTestFiles");
        final Path dstFile = Files.createTempFile("destination", "UnitTestFiles");
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(dstFile));
        // plan actions
        srcFile.toFile()
               .deleteOnExit();
        dstFile.toFile()
               .deleteOnExit();
        // when
        final Path moved = Files.move(srcFile, dstFile, StandardCopyOption.ATOMIC_MOVE);
        // Files.delete(srcFile); // NoSuchFileException (enthuware.ocpjp.v8.2.1805)
        // then
        assertTrue(Files.exists(moved));
        assertTrue(Files.isSameFile(moved, dstFile));
        assertFalse(Files.exists(srcFile));
    }

    /**
     * enthuware.ocpjp.v8.2.1805 No difference than with
     * {@link #move_existingFile_destFileExists_differentName_atomicMove}
     */
    @Test
    void move_existingFile_destFileExists_sameName_atomicMove() throws IOException {
        // given
        final Path srcFile = Files.createTempFile("source", "UnitTestFiles");
        final Path dstDir = Files.createTempDirectory("dstDir");
        final Path dstFile = dstDir.resolve(srcFile.getFileName());
        Files.copy(srcFile, dstFile, StandardCopyOption.REPLACE_EXISTING);
        assertTrue(Files.exists(srcFile));
        assertTrue(Files.exists(dstFile));
        // plan actions
        srcFile.toFile()
               .deleteOnExit();
        dstDir.toFile()
              .deleteOnExit();
        dstFile.toFile()
               .deleteOnExit();
        // when
        final Path moved = Files.move(srcFile, dstFile, StandardCopyOption.ATOMIC_MOVE);
        // Files.delete(srcFile); // NoSuchFileException (enthuware.ocpjp.v8.2.1805)
        // then
        assertTrue(Files.exists(moved));
        assertTrue(Files.isSameFile(moved, dstFile));
        assertFalse(Files.exists(srcFile));
    }

    /**
     * enthuware.ocpjp.v8.2.1524
     */
    @Test
    void move_nonExistingFile() {
        // given
        final Path src = Paths.get(PATH_TO_NON_EXISTING_FILE_WITH_ROOT1);
        final Path dst = Paths.get(PATH_TO_NON_EXISTING_FILE_WITH_ROOT2);
        assertFalse(Files.exists(src));
        assertFalse(Files.exists(dst));
        // when
        final NoSuchFileException ex = assertThrows(NoSuchFileException.class,
                                                    () -> Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING));
        // then
        assertEquals("c:\\temp\\test1.txt", ex.getMessage());
    }

    /**
     * enthuware.ocpjp.v8.2.1524
     */
    @Test
    void copy_nonExistingFile() {
        // given
        final Path src = Paths.get(PATH_TO_NON_EXISTING_FILE_WITH_ROOT1);
        final Path dst = Paths.get(PATH_TO_NON_EXISTING_FILE_WITH_ROOT2);
        assertFalse(Files.exists(src));
        assertFalse(Files.exists(dst));
        // when
        final NoSuchFileException ex = assertThrows(java.nio.file.NoSuchFileException.class, () -> {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            // Files.delete(src);
        });
        // then
        assertEquals("c:\\temp\\test1.txt", ex.getMessage());
    }

    /**
     * {@link Files#isSameFile} checks if two files resolves to the same file. It
     * does not check content of the file
     */
    @Test
    void isSameFile_onTheSamePath() throws IOException {
        // given
        final Path p1 = Paths.get(PATH_TO_EXISTING_FILE);
        // then
        assertTrue(Files.exists(p1));
        assertTrue(Files.isSameFile(p1, p1));
    }

    /**
     * {@link Files#isSameFile} checks if two files resolves to the same file. It
     * does not check content of the file
     */
    @Test
    void isSameFile_afterCopy() throws IOException {
        // given
        final Path srcFile = Files.createTempFile("source", "UnitTestFiles");
        final Path dstDir = Files.createTempDirectory("dstDir");
        final Path dstFile = dstDir.resolve("destinationUnitTestFiles.txt");
        assertTrue(Files.exists(srcFile));
        assertFalse(Files.exists(dstFile));
        try {
            // when
            final Path copied = Files.copy(srcFile,
                                           dstFile,
                                           StandardCopyOption.REPLACE_EXISTING,
                                           StandardCopyOption.COPY_ATTRIBUTES);
            // then
            assertTrue(Files.isSameFile(copied, dstFile));
            assertFalse(Files.isSameFile(srcFile, dstFile));
        } finally {
            Files.deleteIfExists(srcFile);
            Files.deleteIfExists(dstFile); // remove new file (otherwise there will be DirectoryNotEmptyException when
                                           // trying to remove dir)
            Files.deleteIfExists(dstDir);
        }
    }

    /**
     * {@link Files#lines} reads content of the file into stream
     */
    @Test
    void lines_existingFile() throws IOException {
        // given
        final Path myFile = Paths.get(PATH_TO_EXISTING_FILE);
        // when
        final List<String> lines = Files.lines(myFile, StandardCharsets.UTF_8)
                                        .collect(Collectors.toList());
        // then
        log.debug("Read content of file: {}", lines);
        assertThat(lines, contains("content of first line", "content of second line"));
    }

    /**
     * {@link Files#readAllLines} calls beneath {@link Files#newBufferedReader} so
     * it will behave in the same way when the file won't exists.
     *
     * @throws IOException
     * @see #readAllLines_nonExistingFile_read
     * @see #newBufferedReader_existingFile_read
     * @see #newBufferedReader_nonExistingFile
     */
    @Test
    void readAllLines_existingFile_read() throws IOException {
        // given
        final Path myFile = Paths.get(PATH_TO_EXISTING_FILE);
        final Charset charset = StandardCharsets.US_ASCII; // Charset.forName("US-ASCII")
        // when
        final List<String> lines = Files.readAllLines(myFile, charset);
        // then
        log.debug("Read content of file: {}", lines);
        assertThat(lines, contains("content of first line", "content of second line"));
    }

    /**
     * {@link Files#readAllLines} calls beneath {@link Files#newBufferedReader} so
     * it will behave in the same way when the file won't exists.
     *
     * @see #readAllLines_existingFile_read
     * @see #newBufferedReader_existingFile_read
     * @see #newBufferedReader_nonExistingFile
     */
    @Test
    void readAllLines_nonExistingFile_read() {
        // given
        final Path myFile = Paths.get(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT);
        final Charset charset = StandardCharsets.US_ASCII; // Charset.forName("US-ASCII")
        // when
        final java.nio.file.NoSuchFileException ex = assertThrows(java.nio.file.NoSuchFileException.class,
                                                                  () -> Files.readAllLines(myFile, charset));
        // then
        assertEquals(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT, ex.getMessage());
    }

    /**
     * Here is an example of what will happened when file would not exists:
     * {@link #newBufferedReader_nonExistingFile}
     */
    @Test
    void newBufferedReader_existingFile_read() throws IOException {
        // given
        final Path myFile = Paths.get(PATH_TO_EXISTING_FILE);
        final Charset charset = StandardCharsets.US_ASCII; // Charset.forName("US-ASCII")
        final List<String> lines = new ArrayList<>();
        // when
        try (BufferedReader bfr = Files.newBufferedReader(myFile, charset)) {
            String line;
            while ((line = bfr.readLine()) != null) {
                lines.add(line);
            }
        }
        // then
        log.debug("Read content of file: {}", lines);
        assertThat(lines, contains("content of first line", "content of second line"));
    }

    @Test
    void newBufferedWriter_createNewOption() throws IOException {
        // given
        final Path file = Files.createTempFile("source", "UnitTestFiles");
        // when
        assertThrows(FileAlreadyExistsException.class, () -> {
            Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        });
    }

    /**
     * enthuware.ocpjp.v8.2.1538
     */
    @Test
    void newBufferedWriter_withReadOption() throws IOException {
        // given
        final Path path = Files.createTempFile("source", "UnitTestFiles");
        // when
        assertThrows(IllegalArgumentException.class,
                     () -> Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.READ));
    }

    @SuppressWarnings("resource")
    @Test
    void newByteChannel_nonExistingFile() {
        // given
        final Path myFile = Paths.get(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT);
        // when
        final java.nio.file.NoSuchFileException ex = assertThrows(java.nio.file.NoSuchFileException.class,
                                                                  () -> Files.newByteChannel(myFile));
        // then
        assertEquals(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT, ex.getMessage());
    }

    /**
     * {@link Files#newBufferedReader} throws
     * {@link java.nio.file.NoSuchFileException} as opposite to
     * {@link #constructFileReader_nonExistingFile()} or
     * {@link #constructFileInputStream_nonExistingFile()}. It calls
     * {@link Files#newByteChannel} beneath and thus it will behave like it. See
     * {@link #newByteChannel_nonExistingFile} for example
     * <p>
     * enthuware.ocpjp.v8.2.1207 This specific method
     * ({@link Files#newBufferedReader}) is important for OCP exam
     *
     * @see OcpPlayground#testDaemonThread()
     * @see <a href=
     *      "https://stackoverflow.com/questions/27424237/filenotfoundexception-vs-nosuchfileexception">FileNotFoundException
     *      vs. NoSuchFileException</a>
     * @see <a href=
     *      "https://docs.oracle.com/javase/7/docs/api/java/nio/file/NoSuchFileException.html">NoSuchFileException</a>
     * @see <a href=
     *      "https://docs.oracle.com/javase/7/docs/api/java/io/FileNotFoundException.html">FileNotFoundException</a>
     */
    @SuppressWarnings("resource")
    @Test
    void newBufferedReader_nonExistingFile() {
        // given
        final Path myFile = Paths.get(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT);
        final Charset charset = StandardCharsets.US_ASCII;
        // when
        final java.nio.file.NoSuchFileException ex = assertThrows(java.nio.file.NoSuchFileException.class,
                                                                  () -> Files.newBufferedReader(myFile, charset));
        // then
        assertEquals(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT, ex.getMessage());
    }

    @SuppressWarnings("resource")
    @Test
    void constructFileReader_nonExistingFile() {
        // given
        final Path myFile = Paths.get(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT);
        final java.io.FileNotFoundException ex = assertThrows(java.io.FileNotFoundException.class,
                                                              () -> new FileReader(myFile.toFile()));
        // then
        assertThat(ex.getMessage(), containsString(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT));
    }

    @SuppressWarnings("resource")
    @Test
    void constructFileInputStream_nonExistingFile() {
        // given
        final Path myFile = Paths.get(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT);
        final java.io.FileNotFoundException ex = assertThrows(java.io.FileNotFoundException.class,
                                                              () -> new FileInputStream(myFile.toFile()));
        // then
        assertThat(ex.getMessage(), containsString(PATH_TO_NON_EXISTING_FILE_WITHOUT_ROOT));
    }

}
