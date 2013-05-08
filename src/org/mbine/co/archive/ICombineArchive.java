package org.mbine.co.archive;

import java.io.InputStream;
import java.io.OutputStream;

public interface ICombineArchive extends AutoCloseable {
	
	boolean isValidPath(String fileLocation);
	
	Entry createResource(String fileLocation, String fileType);

	void removeResource(Entry entry);

	InputStream readResource(Entry resource);

	OutputStream writeResource(Entry resource);

	Entry getEntry(String path);
	
	boolean exists(Entry resource);
}
