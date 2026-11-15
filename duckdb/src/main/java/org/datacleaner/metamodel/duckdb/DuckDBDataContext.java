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
package org.datacleaner.metamodel.duckdb;

import java.sql.Connection;

import org.datacleaner.metamodel.UpdateScript;
import org.datacleaner.metamodel.UpdateSummary;
import org.datacleaner.metamodel.jdbc.JdbcDataContext;
import org.datacleaner.metamodel.schema.TableType;

/**
 * A read-only {@link JdbcDataContext} for DuckDB databases. Write operations
 * are not supported by this context.
 *
 * Note that the DuckDB JDBC driver reports its tables with the table type
 * "BASE TABLE" instead of the standard "TABLE". This data context wraps the
 * connection so that table type filtering is aligned and tables of DuckDB are
 * resolved as {@link TableType#TABLE}.
 */
public class DuckDBDataContext extends JdbcDataContext {

    public DuckDBDataContext(Connection connection) {
        this(connection, TableType.DEFAULT_TABLE_TYPES, null);
    }

    public DuckDBDataContext(Connection connection, TableType[] tableTypes) {
        this(connection, tableTypes == null ? TableType.DEFAULT_TABLE_TYPES : tableTypes, null);
    }

    public DuckDBDataContext(Connection connection, TableType[] tableTypes, String catalogName) {
        super(DuckDBConnectionWrapper.wrap(connection),
                tableTypes == null ? TableType.DEFAULT_TABLE_TYPES : tableTypes, catalogName);
        setQueryRewriter(new DuckDBQueryRewriter(this));
    }

    @Override
    public UpdateSummary executeUpdate(UpdateScript update) {
        throw new UnsupportedOperationException("DuckDB data context is read-only");
    }
}
