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
module org.datacleaner.metamodel {
    requires org.slf4j;
    requires java.sql;
    requires static java.desktop;

    exports org.datacleaner.metamodel;
    exports org.datacleaner.metamodel.annotations;
    exports org.datacleaner.metamodel.convert;
    exports org.datacleaner.metamodel.create;
    exports org.datacleaner.metamodel.data;
    exports org.datacleaner.metamodel.delete;
    exports org.datacleaner.metamodel.drop;
    exports org.datacleaner.metamodel.factory;
    exports org.datacleaner.metamodel.insert;
    exports org.datacleaner.metamodel.intercept;
    exports org.datacleaner.metamodel.query;
    exports org.datacleaner.metamodel.query.builder;
    exports org.datacleaner.metamodel.query.parser;
    exports org.datacleaner.metamodel.schema;
    exports org.datacleaner.metamodel.schema.builder;
    exports org.datacleaner.metamodel.schema.naming;
    exports org.datacleaner.metamodel.update;
    exports org.datacleaner.metamodel.util;

    uses org.datacleaner.metamodel.factory.DataContextFactory;
    uses org.datacleaner.metamodel.factory.ResourceFactory;

    provides org.datacleaner.metamodel.factory.ResourceFactory
        with org.datacleaner.metamodel.factory.FileResourceFactory,
             org.datacleaner.metamodel.factory.ClasspathResourceFactory,
             org.datacleaner.metamodel.factory.UrlResourceFactory,
             org.datacleaner.metamodel.factory.InMemoryResourceFactory;
}
