/**
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
 * @author Stuart Moodie
 *
 */
public class CombineArchiveException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * Exception with simple message.
	 * @param message
	 */
	public CombineArchiveException(String message) {
		super(message);
	}

	/**
	 * Exception the rethrows another (possible typed) exception.
	 * @param cause
	 */
	public CombineArchiveException(Throwable cause) {
		super(cause);
	}

	/**
	 * Exception that provides a message to a rethrown exception.
	 * @param message
	 * @param cause
	 */
	public CombineArchiveException(String message, Throwable cause) {
		super(message, cause);
	}

}
