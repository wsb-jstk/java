package com.campgemini.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Example of paths:
 * <ol>
 * <li>C:\WKSPC_local\Project_Java\sandbox\wsb\java</li>
 * <li>/mnt/c/WKSPC_local/Project_Java/sandbox/wsb/java</li>
 * </ol>
 * 
 * @see <a href="https://github.com/google/jimfs">Jimfs</a>
 * @see <a href="https://www.baeldung.com/jimfs-file-system-mocking">File System
 *      Mocking with Jimfs</a>
 *
 * @see Paths
 */
class PathsTest {

    @Test
    @Disabled("Example of wrong usage of assertions for Paths- dependent on Operation System")
    void wrongUsageOfAssertion_forPaths() {
        // given
        final Path path = Paths.get("WKSPC_local", "Project_Java", "sandbox", "wsb", "java");
        // assertEquals("WKSPC_local\\Project_Java\\sandbox\\wsb\\java",
        // path.toString()); // OK for Windows (not OK for Linux)
        assertEquals("WKSPC_local/Project_Java/sandbox/wsb/java", path.toString()); // OK for Linux (not OK for Windows)
    }

    @Test
    void fileName() {
        // given
        final Path path = Paths.get("c:\\code\\java\\PathTest.java");
        // when
        final String fileName = path.getFileName()
                                    .toString();
        // then
        assertThat(fileName).isEqualTo("PathTest.java")
                            .isEqualTo(path.getFileName()
                                           .toFile()
                                           .toString());
    }

	@Test
	void name_root_c() {
		// given
		final Path path = Paths.get("c:\\code\\java\\PathTest.java");
		// when
		assertEquals("c:\\", path.getRoot().toString());
		assertEquals("code", path.getName(0).toString());
		assertEquals("java", path.getName(1).toString());
		assertEquals("PathTest.java", path.getName(2).toString());
		assertThrows(IllegalArgumentException.class, () -> path.getName(3)); // not the null or NPE!!!
	}

	@Test
	void params_root_slash() {
		// given
		final Path path = Paths.get("/code/java/PathTest.java");
		// when
		assertEquals(3, path.getNameCount());
		assertEquals("\\", path.getRoot().toString());
		assertEquals("code", path.getName(0).toString());
		assertEquals("java", path.getName(1).toString());
		assertEquals("PathTest.java", path.getName(2).toString());
		assertThrows(IllegalArgumentException.class, () -> path.getName(3)); // not the null or NPE!!!
	}

	@Test
	void params_root_backslash() {
		// given
		final Path path = Paths.get("\\code\\java\\PathTest.java");
		// when
		assertEquals(3, path.getNameCount());
		assertEquals("\\", path.getRoot().toString());
		assertEquals("code", path.getName(0).toString());
		assertEquals("java", path.getName(1).toString());
		assertEquals("PathTest.java", path.getName(2).toString());
		assertThrows(IllegalArgumentException.class, () -> path.getName(3)); // not the null or NPE!!!
	}

	@Test
	@Disabled("on windows we will get: java.nio.file.InvalidPathException: UNC path is missing sharename: /\\test.txt")
	void get_pathWhichStartsWithSlash_onWindows() {
		Paths.get("/", "test.txt");
	}

	@Test
	void resolve_p2HasNoRoot() {
		// given
		final Path p1 = Paths.get("c:\\temp");
		final Path p2 = Paths.get("report.pdf");
		// when
		final Path resolved = p1.resolve(p2);
		// then
		assertEquals("c:\\temp\\report.pdf", resolved.toString());
	}

	@Test
	void resolve_p2HasRoot_p1HasRoot() {
		// given
		final Path p1 = Paths.get("C:\\temp\\test.txt");
		final Path p2 = Paths.get("C:\\temp\\report.pdf");
		// when
		final Path resolved = p1.resolve(p2);
		// then
		assertEquals("C:\\temp\\report.pdf", resolved.toString());
	}

	@Test
	void resolveSibling() {
		// given
		final Path p1 = Paths.get("c:\\temp\\test.txt");
		final Path p2 = Paths.get("report.pdf");
		// when
		final Path resolved = p1.resolveSibling(p2);
		// then
		assertEquals("c:\\temp\\report.pdf", resolved.toString());
	}

	@Test
	void normalize() {
		// given
		final Path p1 = Paths.get("c:\\personal\\.\\photos\\..\\readme.txt");
		// when
		final Path normalized = p1.normalize();
		// then
		assertEquals("c:\\personal\\readme.txt", normalized.toString());
	}

	@Test
	void normalize_noRoot_allElementsAreRedundant() {
		// given
		final Path p1 = Paths.get("\\.\\..\\.\\..");
		// when
		final Path normalized = p1.normalize();
		// then
		assertEquals("\\", normalized.toString());
	}

	@Test
	@Disabled("Result is different in different FileSystems and even on different JDK (JDK8 vs JDK11 (paths is normalized before relativize))")
	void relativize_p1IsNotNormalize_beforeRelativize() {
		// given
		final Path p1 = Paths.get("c:\\personal\\.\\photos\\..\\readme.txt");
		final Path p2 = Paths.get("c:\\personal\\index.html");
		// when
		final Path p3 = p1.relativize(p2);
		// then
		assertEquals("..\\index.html", p3.toString());
		// assertEquals("../c:\\personal\\index.html", p3.toString()); // when run from Ubuntu distro
		// assertEquals("..\\..\\..\\..\\index.html", p3.toString()); // Java 8 v1.8.0.21
	}

}
