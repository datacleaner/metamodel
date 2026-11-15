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
package org.datacleaner.metamodel.sas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.datacleaner.metamodel.data.DataSet;
import org.datacleaner.metamodel.query.SelectItem;
import org.datacleaner.metamodel.sas.SasColumnType;
import org.datacleaner.metamodel.sas.SasReaderCallback;
import org.junit.Assert;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
class CountingSasReaderCallback implements SasReaderCallback {

    private static final Logger logger = LoggerFactory.getLogger(CountingSasReaderCallback.class);

    public static final int SAMPLE_LIMIT = 100;

    private final List<Object[]> sampleRows = new ArrayList<Object[]>();
    private final boolean readData;
    private final DataSet compareToDataSet;
    private int columnCount = 0;
    private int rowCount = 0;

    public CountingSasReaderCallback(boolean readData, DataSet compareToDataSet) {
        this.readData = readData;
        this.compareToDataSet = compareToDataSet;
    }

    @Override
    public boolean readData() {
        return readData;
    }

    @Override
    public void column(int columnIndex, String columnName, String columnLabel, SasColumnType columnType,
            int columnLength) {
        columnCount++;
        if (compareToDataSet != null) {
            SelectItem selectItem = compareToDataSet.getSelectItems().get(columnIndex);
            Assert.assertEquals(selectItem.getColumn().getName(), columnName);
        }
    }

    @Override
    public boolean row(int row, Object[] rowData) {
        logger.info("row no. {}: {}", row, rowData);
        rowCount++;

        if (rowCount < SAMPLE_LIMIT) {
            sampleRows.add(rowData);
        }

        Assert.assertEquals(columnCount, rowData.length);

        if (compareToDataSet != null) {
            Assert.assertTrue(compareToDataSet.next());
            Object[] benchValues = compareToDataSet.getRow().getValues();
            Assert.assertEquals(benchValues.length, rowData.length);
            for (int i = 0; i < benchValues.length; i++) {
                Object benchValue = benchValues[i];
                if (".".equals(benchValue)) {
                    // TODO: Should we accept NaN or instead return null?
                    benchValue = Double.NaN;
                }
                Object actualValue = rowData[i];
                if (actualValue instanceof Number) {
                    try {
                        benchValue = Double.parseDouble(benchValues[i].toString());
                    } catch (NumberFormatException e) {
                        logger.error("Could not parse {} as number", benchValues[i]);
                    }
                }

                if (!benchValue.equals(actualValue)) {
                    logger.error("Bench row:  \"{}\"", Arrays.toString(benchValues));
                    logger.error("Actual row: \"{}\"", Arrays.toString(rowData));
                    logger.error("Failing column: {}, {}", i, compareToDataSet.getSelectItems().get(i).getColumn()
                            .toString());
                    Assert.assertEquals(
                            "Bench and actual values does not match: " + benchValue + " vs. " + actualValue,
                            benchValue.toString(), actualValue.toString());
                }

                benchValues[i] = benchValue;
            }
        }
        return true;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<Object[]> getSampleRows() {
        return sampleRows;
    }
}
