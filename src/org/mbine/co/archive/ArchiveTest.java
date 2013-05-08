package org.mbine.co.archive;

import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchiveTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Path zipPath = FileSystems.getDefault().getPath("tst.zip")
					.toAbsolutePath();
			Files.deleteIfExists(zipPath);
			ArchiveFactory fact = new ArchiveFactory();
			try (ICombineArchive arch = fact.createArchive(zipPath.toString(), true)) {
				Path readMePath = FileSystems.getDefault().getPath("readme.txt");
				Entry entry = arch.createResource(readMePath.toString(), "txt");
				OutputStream writer = arch.writeResource(entry);
				Files.copy(readMePath, writer);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
