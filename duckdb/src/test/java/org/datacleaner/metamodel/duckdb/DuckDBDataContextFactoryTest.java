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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.datacleaner.metamodel.DataContext;
import org.datacleaner.metamodel.data.DataSet;
import org.datacleaner.metamodel.factory.DataContextFactoryRegistry;
import org.datacleaner.metamodel.factory.DataContextFactoryRegistryImpl;
import org.datacleaner.metamodel.factory.DataContextPropertiesImpl;
import org.datacleaner.metamodel.schema.Table;
import org.junit.Test;

public class DuckDBDataContextFactoryTest {

    @Test
    public void testCreateFromClasspathRegistry() throws Exception {
        final DataContextFactoryRegistry registry = DataContextFactoryRegistryImpl.createFromClasspath();

        final Map<String, Object> map = new HashMap<>();
        map.put("type", "duckdb");
        map.put("url", "jdbc:duckdb:");
        final DataContext dataContext = registry.createDataContext(new DataContextPropertiesImpl(map));
        assertNotNull(dataContext);
        assertTrue(dataContext instanceof DuckDBDataContext);

        try (java.sql.Statement st = ((DuckDBDataContext) dataContext).getConnection().createStatement()) {
            st.execute("CREATE TABLE t (foo VARCHAR)");
        }
        final Table table = dataContext.getDefaultSchema().getTableByName("t");
        assertNotNull(table);
        try (DataSet dataSet = dataContext
                .executeQuery(new org.datacleaner.metamodel.query.Query().from(table).select(table.getColumns()))) {
            assertTrue(!dataSet.next());
        }
    }
}
