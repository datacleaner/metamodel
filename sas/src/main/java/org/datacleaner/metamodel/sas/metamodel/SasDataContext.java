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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.datacleaner.metamodel.DataContext;
import org.datacleaner.metamodel.MetaModelException;
import org.datacleaner.metamodel.QueryPostprocessDataContext;
import org.datacleaner.metamodel.data.DataSet;
import org.datacleaner.metamodel.data.RowPublisherDataSet;
import org.datacleaner.metamodel.query.FilterItem;
import org.datacleaner.metamodel.query.SelectItem;
import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.schema.MutableSchema;
import org.datacleaner.metamodel.schema.Schema;
import org.datacleaner.metamodel.schema.Table;
import org.datacleaner.metamodel.sas.CountReaderCallback;
import org.datacleaner.metamodel.sas.SasFilenameFilter;
import org.datacleaner.metamodel.sas.SasReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DataContext} for a directory of SAS tables (aka. a "libref" in
 * SAS-lingo).
 */
public final class SasDataContext extends QueryPostprocessDataContext {

	private static final Logger logger = LoggerFactory
			.getLogger(SasDataContext.class);

	private final File _directory;

	public SasDataContext(String directoryPath) {
		this(new File(directoryPath));
	}

	public SasDataContext(File directory) {
	    super(false);
		if (directory == null) {
			throw new IllegalArgumentException("Directory cannot be null");
		}
		if (!directory.exists()) {
			throw new IllegalArgumentException("Directory does not exist");
		}
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directory
					+ " is not a directory");
		}
		_directory = directory;
	}

	@Override
	protected Schema getMainSchema() throws MetaModelException {
		MutableSchema schema = new MutableSchema(getMainSchemaName());
		File[] tableFiles = _directory.listFiles(new SasFilenameFilter());

		if (tableFiles.length == 0) {
			logger.warn("Directory did not contain any SAS tables: {}",
					_directory);
		}

		Arrays.sort(tableFiles);

		for (File file : tableFiles) {
			SasTable table = new SasTable(file, schema);
			schema.addTable(table);
		}

		return schema;
	}

	@Override
	protected String getMainSchemaName() throws MetaModelException {
		return _directory.getName();
	}
	
	@Override
	protected Number executeCountQuery(Table table, List<FilterItem> whereItems, boolean functionApproximationAllowed) {
	    if (whereItems.isEmpty()) {
	        SasTable sasTable = (SasTable) table;
	        File file = sasTable.getFile();
	        
	        SasReader sasReader = new SasReader(file);
	        CountReaderCallback callback = new CountReaderCallback();
            sasReader.read(callback);
            int count = callback.getCount();
            return count;
	    }
	    return super.executeCountQuery(table, whereItems, functionApproximationAllowed);
	}

	@Override
	protected DataSet materializeMainSchemaTable(Table table, List<Column> columns,
			int maxRows) {
		SasTable sasTable = (SasTable) table;
		File file = sasTable.getFile();

		List<SelectItem> selectItems = columns.stream().map(c -> new SelectItem(c)).collect(Collectors.toList());

		SasReader sasReader = new SasReader(file);
		return new RowPublisherDataSet(
				selectItems.toArray(new SelectItem[selectItems.size()]),
				maxRows, new SasRowPublisherAction(sasReader, columns, maxRows));
	}
}
