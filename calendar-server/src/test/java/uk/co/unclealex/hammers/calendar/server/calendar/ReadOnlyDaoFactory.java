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

package uk.co.unclealex.hammers.calendar.server.calendar;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

import uk.co.unclealex.hammers.calendar.server.dao.BusinessCrudDao;
import uk.co.unclealex.hammers.calendar.server.dao.CrudDao;
import uk.co.unclealex.hammers.calendar.server.model.HasBusinessKey;
import uk.co.unclealex.hammers.calendar.server.model.HasIdentity;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;


/**
 * A factory for creating ReadOnlyDao objects.
 * 
 * @author alex
 */
public class ReadOnlyDaoFactory {

	/**
	 * Creates a new ReadOnlyDao object.
	 * 
	 * @param <M>
	 *          the generic type
	 * @param <D>
	 *          the generic type
	 * @param iface
	 *          the iface
	 * @param elements
	 *          the elements
	 * @return the D
	 */
	public <M extends HasIdentity, D extends CrudDao<M>> D createCrudDao(Class<? extends D> iface, M... elements) {
		return createCrudDao(iface, Arrays.asList(elements));
	}
	
	/**
	 * Creates a new ReadOnlyDao object.
	 * 
	 * @param <M>
	 *          the generic type
	 * @param <D>
	 *          the generic type
	 * @param iface
	 *          the iface
	 * @param elements
	 *          the elements
	 * @return the D
	 */
	@SuppressWarnings("unchecked")
	public <M extends HasIdentity, D extends CrudDao<M>> D createCrudDao(Class<? extends D> iface, final Iterable<M> elements) {
		final CrudDao<M> crudDao = new CrudDao<M>() {
			
			public void saveOrUpdate(Iterable<M> models) {
				throw new UnsupportedOperationException("saveOrUpdate");
			}

			public void saveOrUpdate(M... models) {
				throw new UnsupportedOperationException("saveOrUpdate");
			}

			public Iterable<M> getAll() {
				return elements;
			}

			public M findById(final Integer id) {
				Predicate<M> p = new Predicate<M>() {
					public boolean apply(M element) {
						return id.equals(element.getId());
					}
				};
				return Iterables.find(elements, p, null);
			}

			public void remove(Integer id) {
				throw new UnsupportedOperationException("remove");
			}
		};
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(crudDao, args);
				}
				catch (InvocationTargetException e) {
					throw e;
				}
				catch (Throwable t) {
					throw new UnsupportedOperationException(method.getName());
				}
			}
		};
		return (D) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { iface }, handler);
	}

	/**
	 * Creates a new ReadOnlyDao object.
	 * 
	 * @param <K>
	 *          the key type
	 * @param <M>
	 *          the generic type
	 * @param <D>
	 *          the generic type
	 * @param iface
	 *          the iface
	 * @param elements
	 *          the elements
	 * @return the D
	 */
	public <K extends Serializable & Comparable<K>, M extends HasBusinessKey<K>, D extends BusinessCrudDao<K, M>> D createBusinessCrudDao(
			Class<? extends D> iface, M... elements) {
		return createBusinessCrudDao(iface, Arrays.asList(elements));
	}
	
	/**
	 * Creates a new ReadOnlyDao object.
	 * 
	 * @param <K>
	 *          the key type
	 * @param <M>
	 *          the generic type
	 * @param <D>
	 *          the generic type
	 * @param iface
	 *          the iface
	 * @param elements
	 *          the elements
	 * @return the D
	 */
	@SuppressWarnings("unchecked")
	public <K extends Serializable & Comparable<K>, M extends HasBusinessKey<K>, D extends BusinessCrudDao<K, M>> D createBusinessCrudDao(
			Class<? extends D> iface, final Iterable<M> elements) {
		Function<M, K> keyFunction = new Function<M, K>() {
			public K apply(M model) {
				return model.getBusinessKey();
			}
		};
		final Map<K, M> allByKey = Maps.uniqueIndex(elements, keyFunction);
		final BusinessCrudDao<K, M> crudDao = new BusinessCrudDao<K, M>() {
			public void saveOrUpdate(Iterable<M> models) {
				throw new UnsupportedOperationException("saveOrUpdate");
			}

			public void saveOrUpdate(M... models) {
				throw new UnsupportedOperationException("saveOrUpdate");
			}

			public Iterable<M> getAll() {
				return elements;
			}

			@Override
			public M findByKey(K key) {
				return allByKey.get(key);
			}

			@Override
			public Map<K, M> getAllByKey() {
				return allByKey;
			}

			public M findById(final Integer id) {
				Predicate<M> p = new Predicate<M>() {
					public boolean apply(M element) {
						return id.equals(element.getId());
					}
				};
				return Iterables.find(elements, p, null);
			}

			public void remove(Integer id) {
				throw new UnsupportedOperationException("remove");
			}

			public void remove(K key) {
				throw new UnsupportedOperationException("remove");
			}
		};
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					return method.invoke(crudDao, args);
				}
				catch (InvocationTargetException e) {
					throw e;
				}
				catch (Throwable t) {
					throw new UnsupportedOperationException(method.getName());
				}
			}
		};
		return (D) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { iface }, handler);
	}
}
