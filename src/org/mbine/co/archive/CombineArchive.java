package org.mbine.co.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;

public class CombineArchive implements ICombineArchive {
	private final FileSystem fs;
	private final ManifestManager manifest;
	
	CombineArchive(FileSystem fs, ManifestManager manMan) {
		this.fs = fs;
		this.manifest = manMan;
	}

	@Override
	public void close() throws Exception {
		fs.close();
	}

	@Override
	public Entry createResource(String fileLocation, String fileType) {
		try{
			Path newResPath = this.fs.getPath(fileLocation);
			this.manifest.load();
			Files.createFile(newResPath);
			this.manifest.addEntry(newResPath.toString(), fileType);
			Entry retVal = new Entry(fileLocation, fileType);
			this.manifest.save();
			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void removeResource(Entry entry) {
		Path entryPath = fs.getPath(entry.getPath());
		try {
			this.manifest.load();
			Files.deleteIfExists(entryPath);
			this.manifest.removeEntry(entryPath.toString());
			this.manifest.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream readResource(Entry resource) {
		Path entryPath = this.fs.getPath(resource.getPath());
		InputStream strm = null;
		try {
			strm = Files.newInputStream(entryPath, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return strm;
	}

	@Override
	public OutputStream writeResource(Entry resource) {
		Path entryPath = this.fs.getPath(resource.getPath());
		OutputStream strm = null;
		try {
			strm = Files.newOutputStream(entryPath, StandardOpenOption.WRITE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return strm;
	}

	@Override
	public Entry getEntry(String path) {
		return null;
	}

	@Override
	public boolean exists(Entry resource) {
		Path rPath = this.fs.getPath(resource.getPath());
		return Files.exists(rPath);
	}

	@Override
	public boolean isValidPath(String fileLocation) {
		Path path = this.fs.getPath(fileLocation);
		return Files.isWritable(path);
	}

	@Override
	public Entry createResource(String fileLocation, String fileType, Path srcFile) {
		try{
			Path newResPath = this.fs.getPath(fileLocation);
			this.manifest.load();
			Files.copy(srcFile, newResPath);
			this.manifest.addEntry(newResPath.toString(), fileType);
			Entry retVal = new Entry(fileLocation, fileType);
			this.manifest.save();
			return retVal;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterator<Entry> entryIterator() {
		final Iterator<String> pathIter = this.manifest.filePathIterator();
		return new Iterator<Entry>(){

			@Override
			public boolean hasNext() {
				return pathIter.hasNext();
			}

			@Override
			public Entry next() {
				String path = pathIter.next();
				return new Entry(path, manifest.getFileType(path));
			}

			@Override
			public void remove() {
				new UnsupportedOperationException("Removal not supported by this iterator.");
			}
			
		};
	}

}
