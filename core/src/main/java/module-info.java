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
module org.apache.metamodel {
    requires org.slf4j;
    requires java.sql;
    requires static java.desktop;

    exports org.apache.metamodel;
    exports org.apache.metamodel.annotations;
    exports org.apache.metamodel.convert;
    exports org.apache.metamodel.create;
    exports org.apache.metamodel.data;
    exports org.apache.metamodel.delete;
    exports org.apache.metamodel.drop;
    exports org.apache.metamodel.factory;
    exports org.apache.metamodel.insert;
    exports org.apache.metamodel.intercept;
    exports org.apache.metamodel.query;
    exports org.apache.metamodel.query.builder;
    exports org.apache.metamodel.query.parser;
    exports org.apache.metamodel.schema;
    exports org.apache.metamodel.schema.builder;
    exports org.apache.metamodel.schema.naming;
    exports org.apache.metamodel.update;
    exports org.apache.metamodel.util;

    uses org.apache.metamodel.factory.DataContextFactory;
    uses org.apache.metamodel.factory.ResourceFactory;

    provides org.apache.metamodel.factory.ResourceFactory
        with org.apache.metamodel.factory.FileResourceFactory,
             org.apache.metamodel.factory.ClasspathResourceFactory,
             org.apache.metamodel.factory.UrlResourceFactory,
             org.apache.metamodel.factory.InMemoryResourceFactory;
}
