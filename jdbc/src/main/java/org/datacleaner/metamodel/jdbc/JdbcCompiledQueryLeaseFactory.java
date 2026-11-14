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
package org.datacleaner.metamodel.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.datacleaner.metamodel.jdbc.JdbcUtils.JdbcActionType;
import org.datacleaner.metamodel.util.FileHelper;

/**
 * Factory for the object pool of {@link JdbcCompiledQueryLease}s.
 */
final class JdbcCompiledQueryLeaseFactory implements PooledObjectFactory<JdbcCompiledQueryLease> {

    private final JdbcDataContext _dataContext;
    private final String _sql;

    public JdbcCompiledQueryLeaseFactory(JdbcDataContext dataContext, String sql) {
        _dataContext = dataContext;
        _sql = sql;
    }
    

    @Override
    public PooledObject<JdbcCompiledQueryLease> makeObject() throws Exception {
        try {
            final Connection connection = _dataContext.getConnection();
            final PreparedStatement statement = connection.prepareStatement(_sql);
            final JdbcCompiledQueryLease lease = new JdbcCompiledQueryLease(connection, statement);
            return new DefaultPooledObject<>(lease);
        } catch (SQLException e) {
            throw JdbcUtils.wrapException(e, "preparing statement", JdbcActionType.OTHER);
        }
    }

    @Override
    public void destroyObject(PooledObject<JdbcCompiledQueryLease> pooledObject) throws Exception {
        final JdbcCompiledQueryLease lease = pooledObject.getObject();
        FileHelper.safeClose(lease.getStatement());
        final Connection connection = lease.getConnection();
        _dataContext.close(connection);
    }

    @Override
    public boolean validateObject(PooledObject<JdbcCompiledQueryLease> pooledObject) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<JdbcCompiledQueryLease> pooledObject) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<JdbcCompiledQueryLease> pooledObject) throws Exception {
    }
}