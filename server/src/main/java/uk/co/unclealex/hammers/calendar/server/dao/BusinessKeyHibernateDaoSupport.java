/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.dao;

import java.io.Serializable;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import uk.co.unclealex.hammers.calendar.server.model.HasBusinessKey;

/**
 * Copyright 2011 Alex Jones
 *
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
 *
 * @author unclealex72
 *
 */
public abstract class BusinessKeyHibernateDaoSupport<K extends Serializable & Comparable<K>, M extends HasBusinessKey<K>> extends GenericHibernateDaoSupport<M> implements BusinessCrudDao<K, M>, Function<M, K> {

	private final String i_businessKeyProperty;
	
	/**
	 * @param clazz
	 */
	public BusinessKeyHibernateDaoSupport(Class<M> clazz, String businessKeyProperty) {
		super(clazz);
		i_businessKeyProperty = businessKeyProperty;
	}

	@Override
	public M findByKey(K key) {
		Query query = 
				getSession().createQuery("from " + getEntityName() + " where " + getBusinessKeyProperty() + " = :businessKey").setParameter("businessKey", key);
		return unique(query);
	}

	@Override
	public void remove(K key) {
		Session session = getSession();
		Query query = session.createQuery(
				"delete from " + getEntityName() + " where " + getBusinessKeyProperty() + " = :businessKey").
				setParameter("businessKey", key);
		query.executeUpdate();
		session.flush();
	}

	@Override
	public Map<K, M> getAllByKey() {
		return Maps.uniqueIndex(getAll(), this);
	}
	
	public K apply(M model) {
		return model.getBusinessKey();
	}
	
	public String getBusinessKeyProperty() {
		return i_businessKeyProperty;
	}

}
