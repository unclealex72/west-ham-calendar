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
package uk.co.unclealex.hammers.calendar.server.dao;

import java.io.Serializable;
import java.util.Map;

import uk.co.unclealex.hammers.calendar.server.model.HasBusinessKey;


/**
 * An interface for data access objects for models that can be uniquely
 * identified by a business key.
 * 
 * @param <K>
 *          The type of the business key.
 * @param <M>
 *          The type of the model.
 * @author alex
 */
public interface BusinessCrudDao<K extends Serializable & Comparable<K>, M extends HasBusinessKey<K>> extends
		CrudDao<M> {

	/**
	 * Find a model by its business key.
	 * @param key The key to search for.
	 * @return The model with the given key or null if no such model could be found.
	 */
	M findByKey(K key);

	/**
	 * Get all available models by their business key.
	 * @return A map of all models by their business key.
	 */
	Map<K, M> getAllByKey();

	/**
	 * Remove a model.
	 * @param key The business key of the model to remove.
	 */
	void remove(K key);
}
