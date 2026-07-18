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
package org.datacleaner.metamodel.csv;

import org.datacleaner.metamodel.DataContext;
import org.datacleaner.metamodel.factory.AbstractDataContextFactory;
import org.datacleaner.metamodel.factory.DataContextProperties;
import org.datacleaner.metamodel.factory.ResourceFactoryRegistry;
import org.datacleaner.metamodel.schema.naming.ColumnNamingStrategy;
import org.datacleaner.metamodel.schema.naming.CustomColumnNamingStrategy;
import org.datacleaner.metamodel.util.FileHelper;
import org.datacleaner.metamodel.util.Resource;
import org.datacleaner.metamodel.util.SimpleTableDef;

public class CsvDataContextFactory extends AbstractDataContextFactory {

    @Override
    protected String getType() {
        return "csv";
    }

    @Override
    public DataContext create(DataContextProperties properties, ResourceFactoryRegistry resourceFactoryRegistry) {
        assert accepts(properties, resourceFactoryRegistry);

        final Resource resource = resourceFactoryRegistry.createResource(properties.getResourceProperties());

        final int columnNameLineNumber =
                getInt(properties.getColumnNameLineNumber(), CsvConfiguration.DEFAULT_COLUMN_NAME_LINE);
        final String encoding = getString(properties.getEncoding(), FileHelper.DEFAULT_ENCODING);
        final char separatorChar = getChar(properties.getSeparatorChar(), CsvConfiguration.DEFAULT_SEPARATOR_CHAR);
        final char quoteChar = getChar(properties.getQuoteChar(), CsvConfiguration.DEFAULT_QUOTE_CHAR);
        final char escapeChar = getChar(properties.getEscapeChar(), CsvConfiguration.DEFAULT_ESCAPE_CHAR);
        final boolean failOnInconsistentRowLength = getBoolean(properties.isFailOnInconsistentRowLength(), false);
        final boolean multilineValuesEnabled = getBoolean(properties.isMultilineValuesEnabled(), true);

        final ColumnNamingStrategy columnNamingStrategy;
        if (properties.getTableDefs() == null) {
            columnNamingStrategy = null;
        } else {
            final SimpleTableDef firstTable = properties.getTableDefs()[0];
            final String[] columnNames = firstTable.getColumnNames();
            columnNamingStrategy = new CustomColumnNamingStrategy(columnNames);
        }

        final CsvConfiguration configuration = new CsvConfiguration(columnNameLineNumber, columnNamingStrategy,
                encoding, separatorChar, quoteChar, escapeChar, failOnInconsistentRowLength, multilineValuesEnabled);
        return new CsvDataContext(resource, configuration);
    }
}
