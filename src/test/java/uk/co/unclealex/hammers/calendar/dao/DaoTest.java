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

package uk.co.unclealex.hammers.calendar.dao;

import java.sql.Types;
import java.util.Map;

import javax.persistence.Table;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import com.google.common.collect.Maps;


/**
 * A base class for DAO tests.
 * @author alex
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/application-contexts/dao/context.xml", "/application-contexts/dao/test-db.xml" })
@SuppressWarnings("deprecation")
public abstract class DaoTest extends AbstractTransactionalJUnit4SpringContextTests {

	/** The logger for this class. */
	private static final Logger log = LoggerFactory.getLogger(DaoTest.class);
	
	/** The simple jdbc template. */
	@Autowired SimpleJdbcTemplate simpleJdbcTemplate;
	
	/** The session factory. */
	@Autowired SessionFactory sessionFactory;
	
	/** The entity classes. */
	@SuppressWarnings("rawtypes")
	private final Class[] entityClasses;
	
	/** The blacklist. */
	private final Map<Integer, String> blacklist = Maps.newHashMap();
	
	/**
	 * Instantiates a new dao test.
	 * 
	 * @param entityClasses
	 *          the entity classes
	 */
	@SuppressWarnings("rawtypes")
	public DaoTest(Class... entityClasses) {
		super();
		this.entityClasses = entityClasses;
		blacklist.put(Types.DATE, "DATE");
		blacklist.put(Types.TIME, "TIME");
		blacklist.put(Types.TIMESTAMP, "TIMESTAMP");
		blacklist.put(Types.BINARY, "BINARY");
		blacklist.put(Types.VARBINARY, "VARBINARY");
		blacklist.put(Types.LONGVARBINARY, "LONGVARBINARY");
		blacklist.put(Types.OTHER, "OTHER");
		blacklist.put(Types.JAVA_OBJECT, "JAVA_OBJECT");
		blacklist.put(Types.STRUCT, "STRUCT");
		blacklist.put(Types.ARRAY, "ARRAY");
		blacklist.put(Types.BLOB, "BLOB");
		blacklist.put(Types.DATALINK, "DATALINK");
		blacklist.put(Types.ROWID, "ROWID");
		blacklist.put(Types.NCLOB, "NCLOB");

	}
	
	/**
	 * Setup.
	 * 
	 * @throws Exception
	 *           the exception
	 */
	@Before
	public final void setup() throws Exception {
		@SuppressWarnings("rawtypes")
		Class[] entityClasses = getEntityClasses();
		String[] tableNames = new String[entityClasses.length];
		for (int idx = 0; idx < entityClasses.length; idx++) {
			tableNames[idx] = ((Class<?>) entityClasses[idx]).getAnnotation(Table.class).name();
		}
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate, tableNames);
		doSetup();
	}
	
	/**
	 * Do setup.
	 * 
	 * @throws Exception
	 *           the exception
	 */
	protected abstract void doSetup() throws Exception;
	
	/**
	 * Test mappings.
	 */
	@Test
	public void testMappings() {
		for (Class<?> entityClass : getEntityClasses()) {
			testMappings(entityClass);
		}
	}
	
	/**
	 * Test mappings.
	 * 
	 * @param entityClass
	 *          the entity class
	 */
	public void testMappings(Class<?> entityClass) {
		log.info("Testing mappings for class " + entityClass);
		ClassMetadata classMetadata = sessionFactory.getClassMetadata(entityClass);
		String[] propertyNames = classMetadata.getPropertyNames();
		for (int idx = 0; idx < propertyNames.length; idx++) {
			Type propertyType = classMetadata.getPropertyType(propertyNames[idx]);
			int[] sqlTypes = propertyType.sqlTypes((Mapping) sessionFactory);
			for (int sqlType : sqlTypes) {
				String typeName = blacklist.get(sqlType);
				Assert.assertNull("Property " + propertyNames[idx] + " for entity class " + entityClass + " has invalid sql type " + typeName, typeName);
			}
		}
	}
	
	/**
	 * Gets the entity classes.
	 * 
	 * @return the entityClass
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getEntityClasses() {
		return entityClasses;
	}
}
