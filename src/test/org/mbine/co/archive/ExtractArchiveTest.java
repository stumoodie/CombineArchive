package org.mbine.co.archive;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class ExtractArchiveTest {
   private static final String MODEL2012220003_OMEX = "omex_files/MODEL2012220003.omex";
   private static final String BIOMD0000001000_OMEX = "omex_files/BIOMD0000001000.omex";
   private static final String iAB_AMO1410_SARS_CoV2_OMEX = "omex_files/iAB_AMO1410_SARS-CoV-2.omex";
   private ICombineArchive archive;
   private Path tmpFile;
   private Path zipPath;

   public void setUp(final String omexTestFilePath) throws Exception {
      File file = pickTestOmexFile(omexTestFilePath);
      zipPath = Paths.get(file.toURI());
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString("r--r--r--");
      FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
      tmpFile = Files.createTempFile("tmp", ".zip", attr);
      Files.copy(zipPath, tmpFile, StandardCopyOption.REPLACE_EXISTING);
      CombineArchiveFactory factory = new CombineArchiveFactory();
      archive = factory.openArchive(tmpFile.toString(), false);
   }

   @After
   public void tearDown() throws Exception {
      if (archive != null && archive.isOpen()) {
         archive.close();
      }
      Files.deleteIfExists(tmpFile);
      tmpFile = null;
      zipPath = null;
   }

   @Test
   @Parameters({
           BIOMD0000001000_OMEX,
           MODEL2012220003_OMEX,
           iAB_AMO1410_SARS_CoV2_OMEX
   })
   public void testListAllFilesInArchive(String omexTestFilePath) throws Exception {
      setUp(omexTestFilePath);
      boolean res = archive.isOpen();
      assertEquals(true, res);
      System.out.println(tmpFile.toAbsolutePath());
      System.out.println(tmpFile.getParent().toString());
      Iterator<ArtifactInfo> iterator = archive.artifactIterator();
      while (iterator.hasNext()) {
         ArtifactInfo entry = iterator.next();
         String path = entry.getPath();
         System.out.println(entry);
         assertNotNull(path);
         String format = entry.getFormat();
         boolean master = entry.isMaster();
         if (format.contains("sbml.level-") && master) {
            InputStream stream = archive.readArtifact(entry);
            String result = convertInputStreamToString(stream);
            System.out.println(result);
         }
      }
   }

   @Test
   @Parameters(method = "paramsToTestExtractMasterFile")
   public void testExtractMasterFile(String omexTestFilePath, boolean hasMasterFile, String format,
                                     boolean masterFile, String origFilePath) throws Exception {
      setUp(omexTestFilePath);
      boolean res = archive.isOpen();
      assertEquals(true, res);
      boolean hasMaster = archive.hasMasterFile();
      assertEquals(hasMasterFile, hasMaster);
      Map.Entry<String, InputStream> detectedMasterFile = archive.getMasterFile();
      String detectedFormat = detectedMasterFile.getKey();
      assertEquals(format, detectedFormat);
      InputStream stream = detectedMasterFile.getValue();
      assertEquals(masterFile, stream != null);
      File file = archive.writeMasterFile();
      if (file != null) {
         File origFile = pickTestOmexFile(origFilePath);
         boolean areTwoFilesEqual = FileUtils.contentEquals(origFile, file);
         assertTrue(areTwoFilesEqual);
         // clean
         try {
            Files.delete(file.toPath());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   @Test
   @Parameters(method = "paramsToTestFindMasterFile")
   public void testFindMasterFile(String omexTestFilePath, boolean expected) throws Exception {
      setUp(omexTestFilePath);
      boolean res = archive.isOpen();
      assertEquals(true, res);
      boolean hasMaster = archive.hasMasterFile();
      assertEquals(hasMaster, expected);
   }

   private Object[] paramsToTestExtractMasterFile() {
      return new Object[] {
         /* the OMEX file below does not declare a master attribute */
         new Object[] {BIOMD0000001000_OMEX, false, "", false, "omex_files/BIOMD0000001000/MODEL1712050009.xml"},
         new Object[] {MODEL2012220003_OMEX, true, "https://identifiers.org/combine.specifications/sbml.level-3" +
                 ".version-1", true, "omex_files/MODEL2012220003/iDPM21RW.xml"},
         new Object[] {iAB_AMO1410_SARS_CoV2_OMEX, true, "https://identifiers.org/combine.specifications/sbml.level-3" +
                 ".version-1", true, "omex_files/iAB_AMO1410_SARS-CoV-2/iAB_AMO1410_SARS-CoV-2.xml"},
      };
   }
   private Object[] paramsToTestFindMasterFile() {
      return new Object[] {
         new Object[] {BIOMD0000001000_OMEX, false},
         new Object[] {MODEL2012220003_OMEX, true},
         new Object[] {iAB_AMO1410_SARS_CoV2_OMEX, true},
      };
   }
   private static String convertInputStreamToString(InputStream is) throws IOException {
      if (is == null) {
         return "";
      }
      String newLine = System.getProperty("line.separator");
      String result;
      try (Stream<String> lines = new BufferedReader(new InputStreamReader(is)).lines()) {
         result = lines.collect(Collectors.joining(newLine));
      }

      return result;
   }

   private static File pickTestOmexFile(final String path) {
      ClassLoader classLoader = ExtractArchiveTest.class.getClassLoader();
      return new File(classLoader.getResource(path).getFile());
   }
}
