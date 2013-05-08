package org.mbine.co.archive;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArchiveFactory implements IArchiveFactory {
	private static final String URI_PREFIX = "jar:file://";
	private static final String MANIFEST_FILE_NAME = "manifest.xml";
	
	
	@Override
	public ICombineArchive createArchive(String path, boolean createFlag) {
		try{
			Map<String, String> env = new HashMap<>();
			env.put("create", Boolean.toString(createFlag));
			Path zipLocn = Paths.get(path).toAbsolutePath();
			StringBuilder buf = new StringBuilder(URI_PREFIX);
			buf.append(zipLocn.toString());
			URI zipUri = URI.create(buf.toString());
			ICombineArchive retVal = null;
			FileSystem zipFs = FileSystems.newFileSystem(zipUri, env);
			Path maniPath = zipFs.getPath(MANIFEST_FILE_NAME);
			ManifestManager man = new ManifestManager(maniPath);
			if(!Files.exists(maniPath)){
				Files.createFile(maniPath);
				man.save();
			}
			retVal = new CombineArchive(zipFs, man);

			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public boolean canCreateArchive(String path, boolean createFlag) {
		Path zipLocn = Paths.get(path);
		return Files.isRegularFile(zipLocn) && Files.isReadable(zipLocn) && Files.isWritable(zipLocn);
	}

}
