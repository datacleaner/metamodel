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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.datacleaner.metamodel.UpdateScript;
import org.datacleaner.metamodel.data.DataSet;
import org.datacleaner.metamodel.data.Row;
import org.datacleaner.metamodel.query.CountAggregateFunction;
import org.datacleaner.metamodel.query.FilterItem;
import org.datacleaner.metamodel.query.LogicalOperator;
import org.datacleaner.metamodel.query.OperatorType;
import org.datacleaner.metamodel.query.OrderByItem;
import org.datacleaner.metamodel.query.Query;
import org.datacleaner.metamodel.query.SelectItem;
import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.schema.Schema;
import org.datacleaner.metamodel.schema.Table;
import org.datacleaner.metamodel.schema.TableType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DuckDBDataContextTest {

    private Connection _connection;
    private DuckDBDataContext _dataContext;

    @Before
    public void setUp() throws Exception {
        _connection = DriverManager.getConnection("jdbc:duckdb:");
        try (Statement st = _connection.createStatement()) {
            st.execute("CREATE TABLE persons (id INTEGER, name VARCHAR)");
            st.execute("INSERT INTO persons VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')");
            st.execute("CREATE TABLE orders (id INTEGER, person_id INTEGER, amount DECIMAL(10,2))");
            st.execute("INSERT INTO orders VALUES (1, 1, 10.50), (2, 1, 20.00), (3, 2, 5.00), "
                    + "(4, 3, 7.25), (5, 3, 7.25), (6, 4, 1.00)");
        }
        _dataContext = new DuckDBDataContext(_connection);
    }

    @After
    public void tearDown() throws Exception {
        _connection.close();
    }

    @Test
    public void testSchemaAndQuery() throws Exception {
        final Schema schema = _dataContext.getDefaultSchema();
        assertNotNull(schema);
        final Table table = schema.getTableByName("persons");
        assertNotNull(table);
        assertEquals(TableType.TABLE, table.getType());
        assertEquals(2, table.getColumnCount());
        final Column nameColumn = table.getColumnByName("name");
        assertNotNull(nameColumn);

        final Query query = new Query().from(table).select(nameColumn)
                .where(nameColumn, OperatorType.EQUALS_TO, "Bob");
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            assertTrue(dataSet.next());
            final Row row = dataSet.getRow();
            assertEquals("Bob", row.getValue(0));
            assertFalse(dataSet.next());
        }
    }

    @Test
    public void testMaxRowsWithLimitRewriter() throws Exception {
        final Table table = _dataContext.getDefaultSchema().getTableByName("persons");
        final Query query = new Query().from(table).select(table.getColumns()).setFirstRow(2).setMaxRows(1);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            assertTrue(dataSet.next());
            assertEquals("Bob", dataSet.getRow().getValue(1));
            assertFalse(dataSet.next());
        }
    }

    @Test
    public void testSelectDistinct() throws Exception {
        final Table orders = getTable("orders");
        final Column amount = orders.getColumnByName("amount");

        final Query query = new Query().from(orders).selectDistinct().select(amount);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> amounts = getSingleColumnValues(dataSet);
            assertEquals(5, amounts.size());
            assertTrue(amounts.contains(new BigDecimal("7.25")));
            assertTrue(amounts.contains(new BigDecimal("1.00")));
        }
    }

    @Test
    public void testSelectCount() throws Exception {
        final Table persons = getTable("persons");
        final Query query = new Query().from(persons).selectCount();
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            assertTrue(dataSet.next());
            assertEquals(3L, dataSet.getRow().getValue(0));
            assertFalse(dataSet.next());
        }
    }

    @Test
    public void testSelectCountWithWhere() throws Exception {
        final Table orders = getTable("orders");
        final Column personId = orders.getColumnByName("person_id");
        final Query query = new Query().from(orders).selectCount()
                .where(personId, OperatorType.EQUALS_TO, 1);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            assertTrue(dataSet.next());
            assertEquals(2L, dataSet.getRow().getValue(0));
            assertFalse(dataSet.next());
        }
    }

    @Test
    public void testGroupByWithCount() throws Exception {
        final Table orders = getTable("orders");
        final Column personId = orders.getColumnByName("person_id");
        final Query query = new Query().from(orders).select(personId).select(new CountAggregateFunction(), personId)
                .groupBy(personId);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Row> rows = getRows(dataSet);
            assertEquals(4, rows.size());
            // order of groups is not guaranteed, so verify as a map-like set of entries
            final List<String> entries = new ArrayList<>();
            for (Row row : rows) {
                entries.add(row.getValue(0) + "=" + row.getValue(1));
            }
            assertTrue(entries.contains("1=2"));
            assertTrue(entries.contains("2=1"));
            assertTrue(entries.contains("3=2"));
            assertTrue(entries.contains("4=1"));
        }
    }

    @Test
    public void testGroupByWithHaving() throws Exception {
        final Table orders = getTable("orders");
        final Column personId = orders.getColumnByName("person_id");
        final Query query = new Query().from(orders).select(personId).select(new CountAggregateFunction(), personId)
                .groupBy(personId).having(new CountAggregateFunction(), personId, OperatorType.GREATER_THAN, 1);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Row> rows = getRows(dataSet);
            assertEquals(2, rows.size());
            final List<Object> personIds = new ArrayList<>();
            for (Row row : rows) {
                assertEquals(2L, row.getValue(1));
                personIds.add(row.getValue(0));
            }
            assertTrue(personIds.contains(1));
            assertTrue(personIds.contains(3));
        }
    }

    @Test
    public void testOrderByAscending() throws Exception {
        final Table persons = getTable("persons");
        final Column name = persons.getColumnByName("name");
        final Query query = new Query().from(persons).select(name).orderBy(name);
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> names = getSingleColumnValues(dataSet);
            assertEquals(Arrays.asList("Alice", "Bob", "Charlie"), names);
        }
    }

    @Test
    public void testOrderByDescending() throws Exception {
        final Table orders = getTable("orders");
        final Column amount = orders.getColumnByName("amount");
        final Query query = new Query().from(orders).select(amount).orderBy(new OrderByItem(new SelectItem(amount),
                OrderByItem.Direction.DESC));
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> amounts = getSingleColumnValues(dataSet);
            assertEquals(6, amounts.size());
            assertEquals(new BigDecimal("20.00"), amounts.get(0));
            assertEquals(new BigDecimal("1.00"), amounts.get(5));
        }
    }

    @Test
    public void testWhereWithInOperator() throws Exception {
        final Table persons = getTable("persons");
        final Column name = persons.getColumnByName("name");
        final Query query = new Query().from(persons).select(name)
                .where(name, OperatorType.IN, Arrays.asList("Bob", "Charlie"));
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> names = getSingleColumnValues(dataSet);
            assertEquals(2, names.size());
            assertTrue(names.contains("Bob"));
            assertTrue(names.contains("Charlie"));
        }
    }

    @Test
    public void testWhereWithLikeOperator() throws Exception {
        final Table persons = getTable("persons");
        final Column name = persons.getColumnByName("name");
        final Query query = new Query().from(persons).select(name).where(name, OperatorType.LIKE, "Ch%");
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> names = getSingleColumnValues(dataSet);
            assertEquals(Arrays.asList("Charlie"), names);
        }
    }

    @Test
    public void testWhereWithCombinedFilters() throws Exception {
        final Table orders = getTable("orders");
        final Column amount = orders.getColumnByName("amount");
        final Query query = new Query().from(orders).select(orders.getColumnByName("id"))
                .where(new FilterItem(LogicalOperator.OR, new FilterItem(new SelectItem(amount), OperatorType.GREATER_THAN,
                        new BigDecimal("15.00")), new FilterItem(new SelectItem(amount), OperatorType.LESS_THAN,
                                new BigDecimal("2.00"))));
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Object> ids = getSingleColumnValues(dataSet);
            assertEquals(Arrays.asList(2, 6), ids);
        }
    }

    @Test
    public void testInnerJoin() throws Exception {
        final Table persons = getTable("persons");
        final Table orders = getTable("orders");
        final Query query = new Query().from(persons, orders, org.datacleaner.metamodel.query.JoinType.INNER,
                persons.getColumnByName("id"), orders.getColumnByName("person_id"))
                .select(persons.getColumnByName("name")).select(orders.getColumnByName("amount"));
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Row> rows = getRows(dataSet);
            assertEquals(5, rows.size());
            for (Row row : rows) {
                assertNotNull(row.getValue(0));
                assertNotNull(row.getValue(1));
            }
        }
    }

    @Test
    public void testLeftJoin() throws Exception {
        final Table persons = getTable("persons");
        final Table orders = getTable("orders");
        final Query query = new Query().from(persons, orders, org.datacleaner.metamodel.query.JoinType.LEFT,
                persons.getColumnByName("id"), orders.getColumnByName("person_id"))
                .select(persons.getColumnByName("name")).select(orders.getColumnByName("id"));
        try (DataSet dataSet = _dataContext.executeQuery(query)) {
            final List<Row> rows = getRows(dataSet);
            // person 1 and 3 match 2 orders each, person 2 matches 1 order, order 6 (person 4) has no match
            assertEquals(5, rows.size());
            int ordersForPerson2 = 0;
            for (Row row : rows) {
                if ("Bob".equals(row.getValue(0))) {
                    ordersForPerson2++;
                    assertEquals(3, row.getValue(1));
                }
            }
            assertEquals(1, ordersForPerson2);
        }
    }

    @Test
    public void testReadOnly() throws Exception {
        final Table table = _dataContext.getDefaultSchema().getTableByName("persons");
        try {
            _dataContext.executeUpdate((UpdateScript) callback -> {
                throw new IllegalStateException("should not be reached");
            });
            fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            assertEquals("DuckDB data context is read-only", e.getMessage());
        }
    }

    private Table getTable(String name) {
        final Table table = _dataContext.getDefaultSchema().getTableByName(name);
        assertNotNull("Table not found: " + name, table);
        return table;
    }

    private List<Row> getRows(DataSet dataSet) {
        final List<Row> rows = new ArrayList<>();
        while (dataSet.next()) {
            rows.add(dataSet.getRow());
        }
        return rows;
    }

    private List<Object> getSingleColumnValues(DataSet dataSet) {
        final List<Object> values = new ArrayList<>();
        for (Row row : getRows(dataSet)) {
            values.add(row.getValue(0));
        }
        return values;
    }

}
