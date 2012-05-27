/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with i_work for additional information
 * regarding copyright ownership.  The ASF licenses i_file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use i_file except in compliance
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
 *
 */

package uk.co.unclealex.hammers.calendar.server.testing;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.datasource.DelegatingDataSource;


/**
 * The Class CountingDataSource.
 * 
 * @author alex
 */
public class CountingDataSource extends DelegatingDataSource {

	/** The OPE n_ connectio n_ count. */
	public static AtomicInteger OPEN_CONNECTION_COUNT = new AtomicInteger(0);
	
	/** The TOTA l_ connectio n_ count. */
	public static AtomicInteger TOTAL_CONNECTION_COUNT = new AtomicInteger(0);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection() throws SQLException {
		return wrap(super.getConnection());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return wrap(super.getConnection(username, password));
	}
	
	/**
	 * Wrap.
	 * 
	 * @param conn
	 *          the conn
	 * @return the connection
	 */
	protected Connection wrap(Connection conn) {
		OPEN_CONNECTION_COUNT.incrementAndGet();
		TOTAL_CONNECTION_COUNT.incrementAndGet();
		return new CountingConnection(conn);
	}
	
	/**
	 * The Class CountingConnection.
	 */
	class CountingConnection implements Connection {
		
		/** The delegate. */
		private Connection delegate;

		/**
		 * Instantiates a new counting connection.
		 * 
		 * @param delegate
		 *          the delegate
		 */
		public CountingConnection(Connection delegate) {
			super();
			this.delegate = delegate;
		}

		/**
		 * {@inheritDoc}
		 */
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return delegate.unwrap(iface);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return delegate.isWrapperFor(iface);
		}

		/**
		 * {@inheritDoc}
		 */
		public Statement createStatement() throws SQLException {
			return delegate.createStatement();
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return delegate.prepareStatement(sql);
		}

		/**
		 * {@inheritDoc}
		 */
		public CallableStatement prepareCall(String sql) throws SQLException {
			return delegate.prepareCall(sql);
		}

		/**
		 * {@inheritDoc}
		 */
		public String nativeSQL(String sql) throws SQLException {
			return delegate.nativeSQL(sql);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			delegate.setAutoCommit(autoCommit);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getAutoCommit() throws SQLException {
			return delegate.getAutoCommit();
		}

		/**
		 * {@inheritDoc}
		 */
		public void commit() throws SQLException {
			delegate.commit();
		}

		/**
		 * {@inheritDoc}
		 */
		public void rollback() throws SQLException {
			delegate.rollback();
		}

		/**
		 * {@inheritDoc}
		 */
		public void close() throws SQLException {
			OPEN_CONNECTION_COUNT.decrementAndGet();
			delegate.close();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isClosed() throws SQLException {
			return delegate.isClosed();
		}

		/**
		 * {@inheritDoc}
		 */
		public DatabaseMetaData getMetaData() throws SQLException {
			return delegate.getMetaData();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setReadOnly(boolean readOnly) throws SQLException {
			delegate.setReadOnly(readOnly);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isReadOnly() throws SQLException {
			return delegate.isReadOnly();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setCatalog(String catalog) throws SQLException {
			delegate.setCatalog(catalog);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getCatalog() throws SQLException {
			return delegate.getCatalog();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setTransactionIsolation(int level) throws SQLException {
			delegate.setTransactionIsolation(level);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getTransactionIsolation() throws SQLException {
			return delegate.getTransactionIsolation();
		}

		/**
		 * {@inheritDoc}
		 */
		public SQLWarning getWarnings() throws SQLException {
			return delegate.getWarnings();
		}

		/**
		 * {@inheritDoc}
		 */
		public void clearWarnings() throws SQLException {
			delegate.clearWarnings();
		}

		/**
		 * {@inheritDoc}
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return delegate.createStatement(resultSetType, resultSetConcurrency);
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		/**
		 * {@inheritDoc}
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		/**
		 * {@inheritDoc}
		 */
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return delegate.getTypeMap();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			delegate.setTypeMap(map);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setHoldability(int holdability) throws SQLException {
			delegate.setHoldability(holdability);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getHoldability() throws SQLException {
			return delegate.getHoldability();
		}

		/**
		 * {@inheritDoc}
		 */
		public Savepoint setSavepoint() throws SQLException {
			return delegate.setSavepoint();
		}

		/**
		 * {@inheritDoc}
		 */
		public Savepoint setSavepoint(String name) throws SQLException {
			return delegate.setSavepoint(name);
		}

		/**
		 * {@inheritDoc}
		 */
		public void rollback(Savepoint savepoint) throws SQLException {
			delegate.rollback(savepoint);
		}

		/**
		 * {@inheritDoc}
		 */
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			delegate.releaseSavepoint(savepoint);
		}

		/**
		 * {@inheritDoc}
		 */
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		/**
		 * {@inheritDoc}
		 */
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return delegate.prepareStatement(sql, autoGeneratedKeys);
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return delegate.prepareStatement(sql, columnIndexes);
		}

		/**
		 * {@inheritDoc}
		 */
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return delegate.prepareStatement(sql, columnNames);
		}

		/**
		 * {@inheritDoc}
		 */
		public Clob createClob() throws SQLException {
			return delegate.createClob();
		}

		/**
		 * {@inheritDoc}
		 */
		public Blob createBlob() throws SQLException {
			return delegate.createBlob();
		}

		/**
		 * {@inheritDoc}
		 */
		public NClob createNClob() throws SQLException {
			return delegate.createNClob();
		}

		/**
		 * {@inheritDoc}
		 */
		public SQLXML createSQLXML() throws SQLException {
			return delegate.createSQLXML();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isValid(int timeout) throws SQLException {
			return delegate.isValid(timeout);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			delegate.setClientInfo(name, value);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			delegate.setClientInfo(properties);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getClientInfo(String name) throws SQLException {
			return delegate.getClientInfo(name);
		}

		/**
		 * {@inheritDoc}
		 */
		public Properties getClientInfo() throws SQLException {
			return delegate.getClientInfo();
		}

		/**
		 * {@inheritDoc}
		 */
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return delegate.createArrayOf(typeName, elements);
		}

		/**
		 * {@inheritDoc}
		 */
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return delegate.createStruct(typeName, attributes);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setSchema(String schema) throws SQLException {
			delegate.setSchema(schema);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getSchema() throws SQLException {
			return delegate.getSchema();
		}

		/**
		 * {@inheritDoc}
		 */
		public void abort(Executor executor) throws SQLException {
			delegate.abort(executor);
		}

		/**
		 * {@inheritDoc}
		 */
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			delegate.setNetworkTimeout(executor, milliseconds);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getNetworkTimeout() throws SQLException {
			return delegate.getNetworkTimeout();
		}
		
		
		
	}
}
