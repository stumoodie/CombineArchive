package org.mbine.co.archive;

public interface IArchiveFactory {
	boolean canCreateArchive(String path, boolean createFlag);

	ICombineArchive createArchive(String path, boolean createFlag);
}
