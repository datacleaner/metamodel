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

/**
 * A {@link SasReaderCallback} for getting the count of rows in a dataset.
 */
public class CountReaderCallback implements SasReaderCallback {

    private int count;

    /**
     * Constructs a {@link CountReaderCallback}
     */
    public CountReaderCallback() {
        count = 0;
    }

    @Override
    public void column(int columnIndex, String columnName, String columnLabel, SasColumnType columnType,
            int columnLength) {
        // do nothing
    }

    @Override
    public boolean readData() {
        return true;
    }

    @Override
    public boolean row(int rowNumber, Object[] rowData) {
        count++;
        return true;
    }

    /**
     * Gets the count of rows in the read table
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

}
