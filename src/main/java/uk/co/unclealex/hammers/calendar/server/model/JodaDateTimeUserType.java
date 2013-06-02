/**
 * Copyright 2010-2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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

package uk.co.unclealex.hammers.calendar.server.model;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;

import com.google.common.base.Objects;


/**
 * Store a {@link DateTime} as an ISO8601 formatted (yyyy-MM-ddTHH:mm:ss.SSSZZ)
 * string.
 * 
 * @author alex
 * 
 */
public class JodaDateTimeUserType implements UserType {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<DateTime> returnedClass() {
		return DateTime.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		String xmlRepresentation = rs.getString(names[0]);
		return xmlRepresentation == null ? null : new DateTime(xmlRepresentation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		String xmlRepresentation = value == null ? null : value.toString();
		st.setString(index, xmlRepresentation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMutable() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return target;
	}

}
