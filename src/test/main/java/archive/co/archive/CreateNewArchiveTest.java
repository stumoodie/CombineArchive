/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package main.java.archive.co.archive;

import static org.apache.poi.openxml4j.opc.ZipFileAssert.assertEquals;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.mbine.co.archive.ArtifactInfo;
import org.mbine.co.archive.CombineArchiveFactory;
import org.mbine.co.archive.ICombineArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Stuart Moodie
 * @author Mihai Glont
 *
 */
public class CreateNewArchiveTest {
    private static final String PSFILE_NAME = "test_sheet.ps";
    /*
     * This file is used in a byte-by-byte comparison with the OMEX file produced by the
     * testCreateArtifact method below.
     *
     * Because Windows and Unix use different line endings, we need to maintain a reference file
     * for each platform. This convenience method helps us ensure that these unit tests don't
     * compare an OMEX file that has LF (Unix-style) line endings with one that has CRLF separators
     * (Windows-style).
     */
    private static final String EXAMPLE_ZIP = getOSDependentExampleFile();
    private static final String[] IGNORE_FILE_CONTENT = { "metadata.rdf" };
    private static String EXAMPLE_PATH="example_files/example1_test/example_zip";
    private Path zipPath;
    private ICombineArchive arch;

    @Before
    public void setUp() throws Exception {
        if (System.getProperty("os.name").startsWith("Windows")) {
            zipPath = Files.createTempFile("zipTest", ".zip");
        } else {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-r--r--");
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
            zipPath = Files.createTempFile("zipTest", ".zip", attr);
        }
        CombineArchiveFactory fact = new CombineArchiveFactory();
        String zipPathStr = zipPath.toString();
        Files.delete(zipPath);
        arch = fact.openArchive(zipPathStr, true);
    }


    @After
    public void tearDown() throws Exception{
        if(this.arch.isOpen()){
            this.arch.close();
        }
        this.arch = null;
        Files.deleteIfExists(zipPath);
    }

    @Test
    public void testCreateArtifact() throws Exception {
        Path readMeSrc = FileSystems.getDefault().getPath(EXAMPLE_PATH, "readme.txt");
        String readMeTgt1 = readMeSrc.getFileName().toString();
        String readMeTgt2 = "abc/foo/" + readMeSrc.getFileName();
        ArtifactInfo entry1 = arch.createArtifact(readMeTgt1, "text/plain");
        OutputStream writer1 = arch.writeArtifact(entry1);
        Files.copy(readMeSrc, writer1);
        writer1.close();
        Path psFile = FileSystems.getDefault().getPath(EXAMPLE_PATH, PSFILE_NAME);
        ArtifactInfo entry2 = arch.createArtifact(PSFILE_NAME, "application/postscript");
        OutputStream writer2 = arch.writeArtifact(entry2);
        Files.copy(psFile, writer2);
        writer2.close();
        arch.createArtifact(readMeTgt2, "text/plain", readMeSrc);
        arch.close();
        assertEquals(zipPath.toFile(), new File(EXAMPLE_ZIP), IGNORE_FILE_CONTENT);
    }

    private static String getOSDependentExampleFile() {
        final String prefix = "example_files/example1_test/example";
        final String WIN    = "_win";
        final String suffix = ".zip";
        StringBuilder result = new StringBuilder(prefix);
        if (System.getProperty("os.name").startsWith("Windows")) {
            result.append(WIN);
        }
        result.append(suffix);
        return result.toString();
    }
}
