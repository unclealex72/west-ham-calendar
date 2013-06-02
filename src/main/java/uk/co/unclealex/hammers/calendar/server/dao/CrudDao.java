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
package uk.co.unclealex.hammers.calendar.server.dao;

import uk.co.unclealex.hammers.calendar.server.model.HasIdentity;


/**
 * An interface for data access objects that takes care of create, read, update
 * and delete operations.
 * 
 * @param <M>
 *          The type of model that data access object is for.
 * @author alex
 */
public interface CrudDao<M extends HasIdentity> {

	/**
	 * Create or update a list of model beans.
	 * @param models The list of model beans.
	 */
	void saveOrUpdate(M... models);

	/**
	 * Create or update a list of model beans.
	 * @param models The list of model beans.
	 */
	void saveOrUpdate(Iterable<M> models);
	
	/**
	 * Get all the persisted models.
	 * @return A list of all available model beans.
	 */
	Iterable<M> getAll();
	
	/**
	 * Find a model by its primary key.
	 * @param id The primary key to search for.
	 * @return The model with the given primary key or null if no such model exists.
	 */
	M findById(Integer id);
	
	/**
	 * Remove a model.
	 * @param id The id of the model to remove.
	 */
	void remove(Integer id);
}
