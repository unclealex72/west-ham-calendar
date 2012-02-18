/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.dao;

import javax.persistence.Entity;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import uk.co.unclealex.hammers.calendar.server.model.HasIdentity;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

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
public class GenericHibernateDaoSupport<M extends HasIdentity> extends HibernateDaoSupport implements CrudDao<M>{

	//private static final Logger log = LoggerFactory.getLogger(GenericHibernateDaoSupport.class);
	
	private final Class<M> i_clazz;
	private final String i_entityName;
	
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
	
	public Iterable<M> getAll() {
		Criteria c = createCriteria();
		return list(c);
	}

	protected Criteria createCriteria() {
		return getSession().createCriteria(getClazz());
	}
	
	protected Iterable<M> list(Query query) {
		return filter(query.list());
	}

	protected Iterable<M> list(Criteria criteria) {
		return filter(criteria.list());
	}
	
	@SuppressWarnings("unchecked")
	protected M unique(Query query) {
		return (M) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	protected M unique(Criteria criteria) {
		return (M) criteria.uniqueResult();
	}
	
	public void remove(Integer id) {
		Session session = getSession();
		session.createQuery("delete from " + getEntityName() + " where id = :id").setInteger("id", id).executeUpdate();
		session.flush();
	}
	
	@Override
	public void saveOrUpdate(M... models) {
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

	protected Iterable<M> filter(Iterable<?> iterable) {
		return Iterables.filter(iterable, getClazz());
	}
	
	protected <C> Iterable<C> filter(Iterable<?> iterable, Class<C> clazz) {
		return Iterables.filter(iterable, clazz);
	}
	
	public Class<M> getClazz() {
		return i_clazz;
	}

	public String getEntityName() {
		return i_entityName;
	}
}
