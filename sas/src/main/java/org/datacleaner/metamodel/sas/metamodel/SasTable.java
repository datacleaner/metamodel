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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.schema.MutableTable;
import org.datacleaner.metamodel.schema.Schema;
import org.datacleaner.metamodel.schema.Table;
import org.datacleaner.metamodel.schema.TableType;
import org.datacleaner.metamodel.sas.SasReader;

/**
 * {@link Table} implementation for SAS data.
 * 
 * @author Kasper Sørensen
 */
final class SasTable extends MutableTable implements Serializable {

	private static final long serialVersionUID = 1L;

	private final File _file;
	private final List<Column> _columns;
	private boolean _columnsLoaded;

	public SasTable(File file, Schema schema) {
		super(createName(file), TableType.TABLE, schema);
		_file = file;
		_columns = new ArrayList<Column>();
		_columnsLoaded = false;
	}

	private static String createName(File file) {
		String name = file.getName();
		if (name.endsWith(".sas7bdat")) {
			name = name.substring(0, name.length() - 9);
		}
		return name;
	}

	public File getFile() {
		return _file;
	}

	@Override
	protected List<Column> getColumnsInternal() {
		if (!_columnsLoaded) {
			_columnsLoaded = true;
			SasReader sasReader = new SasReader(_file);
			sasReader.read(new ColumnBuildingSasCallback(this, _columns));
		}
		return _columns;
	}
}
