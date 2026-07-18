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
package org.datacleaner.metamodel;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Collection;

import javax.sql.DataSource;

import org.datacleaner.metamodel.csv.CsvConfiguration;
import org.datacleaner.metamodel.csv.CsvDataContext;
import org.datacleaner.metamodel.excel.ExcelConfiguration;
import org.datacleaner.metamodel.excel.ExcelDataContext;
import org.datacleaner.metamodel.fixedwidth.FixedWidthConfiguration;
import org.datacleaner.metamodel.fixedwidth.FixedWidthDataContext;
import org.datacleaner.metamodel.jdbc.JdbcDataContext;
import org.datacleaner.metamodel.json.JsonDataContext;
import org.datacleaner.metamodel.pojo.PojoDataContext;
import org.datacleaner.metamodel.pojo.TableDataProvider;
import org.datacleaner.metamodel.schema.TableType;
import org.datacleaner.metamodel.util.FileHelper;
import org.datacleaner.metamodel.util.Resource;
import org.datacleaner.metamodel.xml.XmlDomDataContext;
import org.xml.sax.InputSource;

/**
 * A factory for DataContext objects. This class substantially easens the task
 * of creating and initializing DataContext objects and/or their strategies for
 * reading datastores.
 *
 * @see DataContext
 */
public class DataContextFactory {

    public static final char DEFAULT_CSV_SEPARATOR_CHAR = CsvConfiguration.DEFAULT_SEPARATOR_CHAR;
    public static final char DEFAULT_CSV_QUOTE_CHAR = CsvConfiguration.DEFAULT_QUOTE_CHAR;

    private DataContextFactory() {
        // Prevent instantiation
    }

    public static DataContext createCompositeDataContext(DataContext... delegates) {
        return new CompositeDataContext(delegates);
    }

    public static DataContext createCompositeDataContext(Collection<DataContext> delegates) {
        return new CompositeDataContext(delegates);
    }

    public static UpdateableDataContext createCsvDataContext(File file) {
        return createCsvDataContext(file, DEFAULT_CSV_SEPARATOR_CHAR, DEFAULT_CSV_QUOTE_CHAR);
    }

    public static DataContext createJsonDataContext(File file) {
        return new JsonDataContext(file);
    }

    public static UpdateableDataContext createCsvDataContext(File file, char separatorChar, char quoteChar) {
        return createCsvDataContext(file, separatorChar, quoteChar, FileHelper.DEFAULT_ENCODING);
    }

    public static UpdateableDataContext createCsvDataContext(File file, char separatorChar, char quoteChar,
            String encoding) {
        final CsvConfiguration configuration = new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, encoding,
                separatorChar, quoteChar, CsvConfiguration.DEFAULT_ESCAPE_CHAR);
        final CsvDataContext dc = new CsvDataContext(file, configuration);
        return dc;
    }

    public static UpdateableDataContext createCsvDataContext(File file, CsvConfiguration configuration) {
        final CsvDataContext dc = new CsvDataContext(file, configuration);
        return dc;
    }

    public static DataContext createCsvDataContext(InputStream inputStream, char separatorChar, char quoteChar) {
        return createCsvDataContext(inputStream, separatorChar, quoteChar, FileHelper.DEFAULT_ENCODING);
    }

    public static DataContext createCsvDataContext(InputStream inputStream, char separatorChar, char quoteChar,
            String encoding) {
        final CsvConfiguration configuration = new CsvConfiguration(CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, encoding,
                separatorChar, quoteChar, CsvConfiguration.DEFAULT_ESCAPE_CHAR);
        final CsvDataContext dc = new CsvDataContext(inputStream, configuration);
        return dc;
    }

    public static DataContext createCsvDataContext(InputStream inputStream, CsvConfiguration configuration) {
        final CsvDataContext dc = new CsvDataContext(inputStream, configuration);
        return dc;
    }

    public static DataContext createFixedWidthDataContext(File file, String fileEncoding, int fixedValueWidth) {
        return createFixedWidthDataContext(file, new FixedWidthConfiguration(
                FixedWidthConfiguration.DEFAULT_COLUMN_NAME_LINE, fileEncoding, fixedValueWidth));
    }

    public static DataContext createFixedWidthDataContext(File file, FixedWidthConfiguration configuration) {
        final FixedWidthDataContext dc = new FixedWidthDataContext(file, configuration);
        return dc;
    }

    public static DataContext createFixedWidthDataContext(Resource resource, FixedWidthConfiguration configuration) {
        final FixedWidthDataContext dc = new FixedWidthDataContext(resource, configuration);
        return dc;
    }

    public static DataContext createFixedWidthDataContext(File file, String fileEncoding, int fixedValueWidth,
            int headerLineNumber) {
        return createFixedWidthDataContext(file, new FixedWidthConfiguration(
                FixedWidthConfiguration.DEFAULT_COLUMN_NAME_LINE, fileEncoding, fixedValueWidth));
    }

    public static UpdateableDataContext createExcelDataContext(File file, ExcelConfiguration configuration) {
        return new ExcelDataContext(file, configuration);
    }

    public static UpdateableDataContext createExcelDataContext(File file) {
        return createExcelDataContext(file, new ExcelConfiguration());
    }

    public static DataContext createXmlDataContext(InputSource inputSource, String schemaName,
            boolean autoFlattenTables) {
        XmlDomDataContext dc = new XmlDomDataContext(inputSource, schemaName, autoFlattenTables);
        return dc;
    }

    public static DataContext createXmlDataContext(File file, boolean autoFlattenTables) {
        XmlDomDataContext dc = new XmlDomDataContext(file, autoFlattenTables);
        return dc;
    }

    public static DataContext createXmlDataContext(URL url, boolean autoFlattenTables) {
        XmlDomDataContext dc = new XmlDomDataContext(url, autoFlattenTables);
        return dc;
    }

    public static UpdateableDataContext createJdbcDataContext(Connection connection) {
        return new JdbcDataContext(connection);
    }

    public static UpdateableDataContext createJdbcDataContext(DataSource ds) {
        return new JdbcDataContext(ds);
    }

    public static UpdateableDataContext createJdbcDataContext(Connection connection, String catalogName) {
        return new JdbcDataContext(connection, TableType.DEFAULT_TABLE_TYPES, catalogName);
    }

    public static UpdateableDataContext createJdbcDataContext(Connection connection, TableType... tableTypes) {
        return new JdbcDataContext(connection, tableTypes, null);
    }

    public static UpdateableDataContext createJdbcDataContext(Connection connection, String catalogName,
            TableType[] tableTypes) {
        return new JdbcDataContext(connection, tableTypes, catalogName);
    }

    public static UpdateableDataContext createJdbcDataContext(DataSource ds, TableType... tableTypes) {
        return new JdbcDataContext(ds, tableTypes, null);
    }

    public static UpdateableDataContext createJdbcDataContext(DataSource ds, String catalogName,
            TableType[] tableTypes) {
        return new JdbcDataContext(ds, tableTypes, catalogName);
    }

    public static UpdateableDataContext createJdbcDataContext(DataSource ds, String catalogName) {
        return new JdbcDataContext(ds, TableType.DEFAULT_TABLE_TYPES, catalogName);
    }

    public static DataContext createPojoDataContext() {
        return new PojoDataContext();
    }

    public static DataContext createPojoDataContext(java.util.List<TableDataProvider<?>> tables) {
        return new PojoDataContext(tables);
    }

    public static DataContext createPojoDataContext(String schemaName, TableDataProvider<?>[] tables) {
        return new PojoDataContext(schemaName, tables);
    }

    public static DataContext createPojoDataContext(String schemaName, java.util.List<TableDataProvider<?>> tables) {
        return new PojoDataContext(schemaName, tables);
    }
}
