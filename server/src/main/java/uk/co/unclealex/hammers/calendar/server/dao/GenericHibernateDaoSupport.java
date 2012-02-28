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
 */
package uk.co.unclealex.hammers.calendar.server.dao;

import java.util.Arrays;

import javax.persistence.Entity;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import uk.co.unclealex.hammers.calendar.server.model.HasIdentity;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * An abstract class that can be used for classes that implement {@link CrudDao} and use Hibernate.
 * @author alex
 *
 * @param <M> The type of the model that this data access class is for.
 */
public abstract class GenericHibernateDaoSupport<M extends HasIdentity> implements CrudDao<M>{

	/**
	 * The class of the model objects being persisted.
	 */
	private final Class<M> i_clazz;
	
	/**
	 * The Hibernate entity name of the model objects being persisted.
	 */
	private final String i_entityName;
	
	/**
	 * A Hibernate {@link SessionFactory}.
	 */
	private SessionFactory i_sessionFactory;
	
	/**
	 * Create a new instance of this class.
	 * @param clazz The class of the model objects being persisted.
	 */
	public GenericHibernateDaoSupport(Class<M> clazz) {
		super();
		i_clazz = clazz;
		Entity entity = clazz.getAnnotation(Entity.class);
		if (entity != null) {
			String name = entity.name();
			if (!Strings.isNullOrEmpty(name)) {
				i_entityName = name;
			}
			else {
				i_entityName = clazz.getName();
			}
		}
		else {
			i_entityName = clazz.getName();
		}
	}
	
	@Override
	public Iterable<M> getAll() {
		Criteria c = createCriteria();
		return list(c);
	}

	/**
	 * Create a {@link Criteria} for model objects.
	 * @return A {@link Criteria} for model objects based on the model's class.
	 */
	protected Criteria createCriteria() {
		return getSession().createCriteria(getClazz());
	}
	
	/**
	 * List the results of a {@link Query}.
	 * @param query The {@link Query} to be executed.
	 * @return The results of the {@link Query}.
	 */
	protected Iterable<M> list(Query query) {
		return filter(query.list());
	}

	/**
	 * List the results of a {@link Criteria}.
	 * @param criteria The {@link Criteria} to be executed.
	 * @return The results of the {@link Criteria}.
	 */
	protected Iterable<M> list(Criteria criteria) {
		return filter(criteria.list());
	}
	
	/**
	 * Return a unique result from a {@link Query}.
	 * @param query The {@link Query} to be executed.
	 * @return The unique result of the {@link Query}
	 */
	@SuppressWarnings("unchecked")
	protected M unique(Query query) {
		return (M) query.uniqueResult();
	}

	/**
	 * Return a unique result from a {@link Criteria}.
	 * @param criteria The {@link Criteria} to be executed.
	 * @return The unique result of the {@link Criteria}
	 */
	@SuppressWarnings("unchecked")
	protected M unique(Criteria criteria) {
		return (M) criteria.uniqueResult();
	}
	
	@Override
	public void remove(Integer id) {
		Session session = getSession();
		session.createQuery("delete from " + getEntityName() + " where id = :id").setInteger("id", id).executeUpdate();
		session.flush();
	}
	
	@Override
	public void saveOrUpdate(M... models) {
		saveOrUpdate(Arrays.asList(models));
	}

	@Override
	public void saveOrUpdate(Iterable<M> models) {
		Session session = getSession();
		for (M model : models) {
			session.saveOrUpdate(model);
		}
		session.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public M findById(Integer key) {
		return (M) getSession().get(getClazz(), key);
	}

	/**
	 * Filter an unbounded {@link Iterable} so that it only contains model objects.
	 * @param iterable The unbounded {@link Iterable}
	 * @return A correctly bound {@link Iterable}.
	 */
	protected Iterable<M> filter(Iterable<?> iterable) {
		return Iterables.filter(iterable, getClazz());
	}
	
	/**
	 * Filter an unbounded {@link Iterable} so that it only contains objects with a given class.
	 * @param iterable The unbounded {@link Iterable}
	 * @param <C> The bound for the returned iterable.
	 * @param clazz The class to filter with.
	 * @return A correctly bound {@link Iterable}.
	 */
	protected <C> Iterable<C> filter(Iterable<?> iterable, Class<C> clazz) {
		return Iterables.filter(iterable, clazz);
	}
	
	/**
	 * Get the current Hibernate session.
	 * @return The current Hibernate session.
	 */
	protected Session getSession() {
		return getSessionFactory().getCurrentSession();
	}
	
	public Class<M> getClazz() {
		return i_clazz;
	}

	public String getEntityName() {
		return i_entityName;
	}

	public SessionFactory getSessionFactory() {
		return i_sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		i_sessionFactory = sessionFactory;
	}
}
