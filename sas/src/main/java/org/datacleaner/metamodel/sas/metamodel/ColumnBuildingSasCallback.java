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
package org.datacleaner.metamodel.sas.metamodel;

import java.util.List;

import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.schema.ColumnType;
import org.datacleaner.metamodel.schema.MutableColumn;
import org.datacleaner.metamodel.schema.Table;
import org.datacleaner.metamodel.sas.SasColumnType;
import org.datacleaner.metamodel.sas.SasReaderCallback;

final class ColumnBuildingSasCallback implements SasReaderCallback {

	private final Table _table;
	private final List<Column> _columns;

	public ColumnBuildingSasCallback(Table table, List<Column> columns) {
		_table = table;
		_columns = columns;
	}

	@Override
	public boolean readData() {
		// don't read data
		return false;
	}

	@Override
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength) {
		final ColumnType type;
		if (columnType == SasColumnType.NUMERIC) {
			type = ColumnType.NUMERIC;
		} else if (columnType == SasColumnType.CHARACTER) {
			type = ColumnType.VARCHAR;
		} else {
			type = null;
		}

		MutableColumn column = new MutableColumn(columnName, type, _table,
				columnIndex, true);
		column.setRemarks(columnLabel);
		column.setColumnSize(columnLength);
		_columns.add(column);
	}

	@Override
	public boolean row(int row, Object[] rowData) {
		return false;
	}

}
