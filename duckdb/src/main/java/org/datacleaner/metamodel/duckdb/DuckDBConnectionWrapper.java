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
 * metadata behaviour with the standard. DuckDB reports tables with the table
 * type "BASE TABLE" instead of the standard "TABLE", and its driver only
 * matches this exact literal when filtering on table types in
 * {@link DatabaseMetaData#getTables}. This wrapper rewrites the "TABLE" type
 * to "BASE TABLE" on the way in. Mapping of the returned table type back to
 * {@link org.datacleaner.metamodel.schema.TableType#TABLE} is handled by
 * {@link org.datacleaner.metamodel.schema.TableType#getTableType(String)}.
 */
final class DuckDBConnectionWrapper {

    private static final String DUCKDB_TABLE_TYPE = "BASE TABLE";
    private static final String STANDARD_TABLE_TYPE = "TABLE";

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
                            return method.invoke(target, args[0], args[1], args[2],
                                    rewriteTableTypes((String[]) args[3]));
                        }
                        return super.invoke(method, args);
                    }
                });
    }

    private static String[] rewriteTableTypes(String[] types) {
        if (types == null) {
            return null;
        }
        final String[] result = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            result[i] = STANDARD_TABLE_TYPE.equals(types[i]) ? DUCKDB_TABLE_TYPE : types[i];
        }
        return result;
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
