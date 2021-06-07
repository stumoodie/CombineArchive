/*
 * Copyright 2013 EMBL - European Bioinformatics Institute
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

package org.mbine.co.archive;


/**
 * A template for an artifact included in an archive.
 * Please see the specification at
 * <a href="http://co.mbine.org/standards/omex">COMBINE archive format</a>
 *
 * @author Stuart Moodie
 * @author <a href="mailto:mglont@pm.me">Mihai Glont</a>
 * @author <a href="mailto:nvntung@gmail.com">Tung Nguyen</a>
 */
public final class ArtifactInfo {
	// The format attribute
	private String format;
	// The location attribute
	private String path;

	// this attribute indicates whether the artifact is a master file or not.
	// if it is set true, this file is used first
	private boolean master;
	
	
	ArtifactInfo(String path, String format, boolean master){
		this.path = path;
		this.format = format;
		this.master = master;
	}
	
	public String getFormat(){
		return format;
	}
	
	
	public String getPath(){
		return path;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArtifactInfo other = (ArtifactInfo) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (format == null) {
			if (other.format != null)
				return false;
		} else if (!format.equals(other.format))
			return false;
		if (!master) {
			if (other.master) {
				return false;
			}
		} else if (!other.master) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArtifactInfo [format=" + format + ", path=" + path + ", master=" + master + "]";
	}
}
