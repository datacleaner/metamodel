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
package org.datacleaner.metamodel.factory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.datacleaner.metamodel.csv.CsvDataContextFactory;
import org.datacleaner.metamodel.excel.ExcelDataContextFactory;
import org.datacleaner.metamodel.fixedwidth.FixedWidthDataContextFactory;
import org.datacleaner.metamodel.jdbc.JdbcDataContextFactory;
import org.datacleaner.metamodel.json.JsonDataContextFactory;
import org.datacleaner.metamodel.pojo.PojoDataContextFactory;
import org.datacleaner.metamodel.xml.XmlDomDataContextFactory;
import org.datacleaner.metamodel.xml.XmlSaxDataContextFactory;
import org.junit.Assert;
import org.junit.Test;

public class DataContextFactoryRegistryTest {

    @Test
    public void testLoadAllFactories() {
        final DataContextFactoryRegistry registry = DataContextFactoryRegistryImpl.getDefaultInstance();
        final Collection<DataContextFactory> factories = registry.getFactories();

        final List<Class<?>> factoryClasses = factories.stream().map(f -> f.getClass()).collect(Collectors.toList());

        Assert.assertTrue(factoryClasses.contains(CsvDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(ExcelDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(FixedWidthDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(JdbcDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(JsonDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(PojoDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(XmlDomDataContextFactory.class));
        Assert.assertTrue(factoryClasses.contains(XmlSaxDataContextFactory.class));
    }
}
