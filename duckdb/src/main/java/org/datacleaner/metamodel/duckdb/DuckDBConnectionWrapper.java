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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Utility for wrapping a DuckDB {@link Connection} in order to align its JDBC
 * metadata behaviour with the standard. Since DuckDB 2.x, a null catalog in
 * {@link DatabaseMetaData#getTables} makes the driver return tables from all
 * attached databases (including the "system" catalog), which shadow's the
 * user's schemas. This wrapper defaults the catalog to the connection's
 * current catalog.
 */
final class DuckDBConnectionWrapper {

    private DuckDBConnectionWrapper() {
    }

    static Connection wrap(Connection connection) {
        return (Connection) Proxy.newProxyInstance(DuckDBConnectionWrapper.class.getClassLoader(),
                new Class<?>[] { Connection.class }, new DelegateInvocationHandler(connection) {
                    @Override
                    Object invoke(Method method, Object[] args) throws Throwable {
                        if ("getMetaData".equals(method.getName()) && method.getParameterCount() == 0) {
                            return wrapDatabaseMetaData((DatabaseMetaData) method.invoke(target, args));
                        }
                        return super.invoke(method, args);
                    }
                });
    }

    private static DatabaseMetaData wrapDatabaseMetaData(DatabaseMetaData metaData) {
        return (DatabaseMetaData) Proxy.newProxyInstance(DuckDBConnectionWrapper.class.getClassLoader(),
                new Class<?>[] { DatabaseMetaData.class }, new DelegateInvocationHandler(metaData) {
                    @Override
                    Object invoke(Method method, Object[] args) throws Throwable {
                        if ("getTables".equals(method.getName()) && method.getParameterCount() == 4) {
                            // Since DuckDB 2.x, a null catalog makes getTables
                            // return tables from all attached databases
                            // (including the "system" catalog), shadowing the
                            // user's schema. Default to the current catalog.
                            String catalog = args[0] != null ? (String) args[0] : ((DatabaseMetaData) target).getConnection().getCatalog();
                            return method.invoke(target, catalog, args[1], args[2], args[3]);
                        }
                        return super.invoke(method, args);
                    }
                });
    }

    private abstract static class DelegateInvocationHandler implements InvocationHandler {

        protected final Object target;

        DelegateInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                final String name = method.getName();
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("toString".equals(name)) {
                    return "DuckDB" + target.toString();
                }
                return method.invoke(this, args);
            }
            try {
                return invoke(method, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        Object invoke(Method method, Object[] args) throws Throwable {
            return method.invoke(target, args);
        }
    }
}
