/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.datacleaner.metamodel.sas;

/**
 * Represents the header metadata in the sas7bdat file format.
 * 
 * @author Kasper Sørensen
 */
final class SasHeader {

	private final String sasRelease;
	private final String sasHost;
	private final int pageSize;
	private final int pageCount;

	public SasHeader(String sasRelease, String sasHost, int pageSize,
			int pageCount) {
		this.sasRelease = sasRelease;
		this.sasHost = sasHost;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
	}

	public String getSasRelease() {
		return sasRelease;
	}

	public String getSasHost() {
		return sasHost;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	@Override
	public String toString() {
		return "SasHeader [sasRelease=" + sasRelease + ", sasHost=" + sasHost
				+ ", pageSize=" + pageSize + ", pageCount=" + pageCount + "]";
	}
}
