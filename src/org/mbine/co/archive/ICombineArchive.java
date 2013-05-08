package org.mbine.co.archive;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Iterator;

public interface ICombineArchive extends AutoCloseable {
	
	boolean isValidPath(String fileLocation);
	
	Entry createResource(String fileLocation, String fileType);

	Entry createResource(String fileLocation, String fileType, Path srcFile);

	void removeResource(Entry entry);

	InputStream readResource(Entry resource);

	OutputStream writeResource(Entry resource);

	Entry getEntry(String path);
	
	boolean exists(Entry resource);
	
	Iterator<Entry> entryIterator();
}
