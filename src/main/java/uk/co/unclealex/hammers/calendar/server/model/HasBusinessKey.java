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


/**
 * An interface for models that can be uniquely defined by a business key.
 * 
 * @param <K>
 *          The type of the business key.
 * @author alex
 */
public interface HasBusinessKey<K extends Comparable<K> & Serializable> extends HasIdentity {

	/**
	 * Get the model's business key.
	 * @return The model's business key.
	 */
	K getBusinessKey();
	
	/**
	 * Set the model's business key.
	 * @param businessKey The model's new business key.
	 */
	void setBusinessKey(K businessKey);
}
