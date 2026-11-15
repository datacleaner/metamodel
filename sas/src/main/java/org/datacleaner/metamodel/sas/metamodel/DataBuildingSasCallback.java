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

import org.datacleaner.metamodel.data.RowPublisher;
import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.sas.SasColumnType;
import org.datacleaner.metamodel.sas.SasReaderCallback;

final class DataBuildingSasCallback implements SasReaderCallback {

	private final List<Column> _columns;
	private final int _maxRows;
	private RowPublisher _publisher;

	public DataBuildingSasCallback(RowPublisher publisher, List<Column> columns,
			int maxRows) {
		_publisher = publisher;
		_columns = columns;
		_maxRows = maxRows;
	}

	@Override
	public boolean readData() {
		return true;
	}

	@Override
	public void column(int columnIndex, String columnName, String columnLabel,
			SasColumnType columnType, int columnLength) {
		// do nothing
	}

	@Override
	public boolean row(int row, Object[] rowData) {
		if (_maxRows > 0 && row > _maxRows) {
			// don't read any more rows
			return false;
		}

		Object[] result = new Object[_columns.size()];
		for (int i = 0; i < result.length; i++) {
			int columnNumber = _columns.get(i).getColumnNumber();
			result[i] = rowData[columnNumber];
		}

		_publisher.publish(result);

		return true;
	}

}
