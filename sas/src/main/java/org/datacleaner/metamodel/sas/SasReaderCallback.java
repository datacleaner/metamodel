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
 * A callback interface for the {@link SasReader} that allows a data "target" to
 * receive data and give instructions as to the further reading of a sas7bdat
 * file.
 * 
 * @author Kasper Sørensen
 */
public interface SasReaderCallback {

	/**
	 * Callback method that accepts a column discovered by the {@link SasReader}
	 * .
	 * 
	 * @param columnIndex
	 *            the index (0-based) of the column
	 * @param columnName
	 *            the physical name of the column
	 * @param columnLabel
	 *            the logical label of the column (often more user-friendly than
	 *            name)
	 * @param columnType
	 *            the type of the column
	 * @param columnLength
	 *            the length of the column
	 */
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength);

	/**
	 * Should the reader read the data/rows (or only columns?)
	 * 
	 * @return true if data/rows should be read.
	 */
	public boolean readData();

	/**
	 * Callback method that accepts an array of row data.
	 * 
	 * @param rowNumber
	 *            the row number (1 = first row)
	 * @param rowData
	 *            the row data
	 * @return true if more rows should be read.
	 */
	public boolean row(int rowNumber, Object[] rowData);

}
