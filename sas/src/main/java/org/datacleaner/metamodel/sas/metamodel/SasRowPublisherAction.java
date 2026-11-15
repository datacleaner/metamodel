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

import java.util.List;

import org.datacleaner.metamodel.data.RowPublisher;
import org.datacleaner.metamodel.schema.Column;
import org.datacleaner.metamodel.util.Action;
import org.datacleaner.metamodel.sas.SasReader;

public final  class SasRowPublisherAction implements Action<RowPublisher> {

	private final SasReader _sasReader;
	private final List<Column> _columns;
	private final int _maxRows;

	public SasRowPublisherAction(SasReader sasReader, List<Column> columns,
			int maxRows) {
		_sasReader = sasReader;
		_columns = columns;
		_maxRows = maxRows;
	}

	@Override
	public void run(RowPublisher publisher) throws Exception {
		_sasReader.read(new DataBuildingSasCallback(publisher, _columns,
				_maxRows));
		publisher.finished();
	}

}
