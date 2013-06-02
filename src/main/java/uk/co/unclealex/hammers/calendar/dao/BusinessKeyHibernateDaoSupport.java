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

import java.io.Serializable;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import uk.co.unclealex.hammers.calendar.model.HasBusinessKey;


/**
 * An implementation of {@link BusinessCrudDao} using Hibernate.
 * 
 * @param <K>
 *          the key type
 * @param <M>
 *          the generic type
 * @author alex
 */
public abstract class BusinessKeyHibernateDaoSupport<K extends Serializable & Comparable<K>, M extends HasBusinessKey<K>>
		extends GenericHibernateDaoSupport<M> implements BusinessCrudDao<K, M> {

	/**
	 * The name of the property that is the model's business key.
	 */
	private final String businessKeyProperty;

	/**
	 * Instantiates a new business key hibernate dao support.
	 * 
	 * @param clazz
	 *          The class of the model.
	 * @param businessKeyProperty
	 *          The name of the property that is the model's business key.
	 */
	public BusinessKeyHibernateDaoSupport(Class<M> clazz, String businessKeyProperty) {
		super(clazz);
		this.businessKeyProperty = businessKeyProperty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M findByKey(K key) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " where " + getBusinessKeyProperty() + " = :businessKey").setParameter(
				"businessKey", key);
		return unique(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(K key) {
		Session session = getSession();
		Query query = session.createQuery(
				"delete from " + getEntityName() + " where " + getBusinessKeyProperty() + " = :businessKey").setParameter(
				"businessKey", key);
		query.executeUpdate();
		session.flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<K, M> getAllByKey() {
		return Maps.uniqueIndex(getAll(), createBusinessKeyFunction());
	}

	/**
	 * Create a {@link Function} that gets a business key from a model.
	 * @return A {@link Function} that gets a business key from a model.
	 */
	protected Function<M, K> createBusinessKeyFunction() {
		return new Function<M, K>() {
			public K apply(M model) {
				return model.getBusinessKey();
			}
		};
	}

	/**
	 * Gets the name of the property that is the model's business key.
	 * 
	 * @return the name of the property that is the model's business key
	 */
	public String getBusinessKeyProperty() {
		return businessKeyProperty;
	}

}
