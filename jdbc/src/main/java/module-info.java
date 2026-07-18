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
module org.apache.metamodel.jdbc {
    requires org.apache.metamodel;
    requires org.slf4j;
    requires java.sql;
    requires commons.pool;
    requires static com.fasterxml.jackson.databind;
    requires static org.postgresql.jdbc;
    requires static java.desktop;

    exports org.apache.metamodel.jdbc;
    exports org.apache.metamodel.jdbc.dialects;

    provides org.apache.metamodel.factory.DataContextFactory
        with org.apache.metamodel.jdbc.JdbcDataContextFactory;
}
