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
package org.datacleaner.metamodel.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datacleaner.metamodel.AbstractUpdateCallback;
import org.datacleaner.metamodel.MetaModelException;
import org.datacleaner.metamodel.create.AbstractTableCreationBuilder;
import org.datacleaner.metamodel.create.TableCreationBuilder;
import org.datacleaner.metamodel.data.DataSet;
import org.datacleaner.metamodel.data.Row;
import org.datacleaner.metamodel.delete.AbstractRowDeletionBuilder;
import org.datacleaner.metamodel.delete.RowDeletionBuilder;
import org.datacleaner.metamodel.drop.AbstractTableDropBuilder;
import org.datacleaner.metamodel.drop.TableDropBuilder;
import org.datacleaner.metamodel.insert.AbstractRowInsertionBuilder;
import org.datacleaner.metamodel.insert.RowInsertionBuilder;
import org.datacleaner.metamodel.query.FilterItem;
import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.schema.MutableSchema;
import org.datacleaner.metamodel.schema.MutableTable;
import org.datacleaner.metamodel.schema.Schema;
import org.datacleaner.metamodel.schema.Table;
import org.datacleaner.metamodel.util.SimpleTableDef;

/**
 * Update callback for the pojo module.
 */
final class PojoUpdateCallback extends AbstractUpdateCallback {

    private final PojoDataContext _dataContext;

    public PojoUpdateCallback(PojoDataContext dataContext) {
        super(dataContext);
        _dataContext = dataContext;
    }

    @Override
    public TableCreationBuilder createTable(Schema schema, String name) throws IllegalArgumentException,
            IllegalStateException {

        return new AbstractTableCreationBuilder<PojoUpdateCallback>(this, schema, name) {

            @Override
            public Table execute() throws MetaModelException {
                MutableTable table = getTable();
                MutableSchema schema = (MutableSchema) getSchema();
                table.setSchema(schema);
                schema.addTable(table);
                _dataContext.addTableDataProvider(new MapTableDataProvider(new SimpleTableDef(table),
                        new ArrayList<Map<String, ?>>()));
                return table;
            }
        };
    }

    @Override
    public boolean isDropTableSupported() {
        return true;
    }

    @Override
    public TableDropBuilder dropTable(Table table) throws IllegalArgumentException, IllegalStateException,
            UnsupportedOperationException {
        return new AbstractTableDropBuilder(table) {
            @Override
            public void execute() throws MetaModelException {
                MutableTable mutableTable = (MutableTable) getTable();
                MutableSchema schema = (MutableSchema) mutableTable.getSchema();
                schema.removeTable(mutableTable);
                mutableTable.setSchema(null);
            }
        };
    }

    @Override
    public RowInsertionBuilder insertInto(Table table) throws IllegalArgumentException, IllegalStateException,
            UnsupportedOperationException {
        return new AbstractRowInsertionBuilder<PojoUpdateCallback>(this, table) {
            @Override
            public void execute() throws MetaModelException {
                boolean[] explicitNulls = getExplicitNulls();
                Column[] columns = getColumns();
                Object[] values = getValues();
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null || explicitNulls[i]) {
                        map.put(columns[i].getName(), values[i]);
                    }
                }
                _dataContext.insert(getTable().getName(), map);
            }
        };
    }

    @Override
    public boolean isDeleteSupported() {
        return true;
    }

    @Override
    public RowDeletionBuilder deleteFrom(Table table) throws IllegalArgumentException, IllegalStateException,
            UnsupportedOperationException {
        return new AbstractRowDeletionBuilder(table) {

            @Override
            public void execute() throws MetaModelException {
                final DataSet dataSet = _dataContext.query().from(getTable()).select(getTable().getColumns()).execute();
                final PojoDataSet<?> pojoDataSet = (PojoDataSet<?>) dataSet;
                final List<FilterItem> whereItems = getWhereItems();
                while (pojoDataSet.next()) {
                    boolean delete = true;
                    final Row row = pojoDataSet.getRow();
                    for (FilterItem whereItem : whereItems) {
                        if (!whereItem.evaluate(row)) {
                            delete = false;
                            break;
                        }
                    }
                    if (delete) {
                        pojoDataSet.remove();
                    }
                }
            }
        };
    }

}
