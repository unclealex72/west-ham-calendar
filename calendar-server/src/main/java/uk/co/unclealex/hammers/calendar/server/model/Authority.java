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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import uk.co.unclealex.hammers.calendar.shared.model.Role;


/**
 * A model of a granted {@link Role} for a user.
 * 
 * @author alex
 * 
 */
@Entity
@Table(name = "authorities")
public class Authority implements HasIdentity {

	/**
	 * The primary key of authority.
	 */
	private Integer id;
	
	/**
	 * The {@link Role} granted to the user.
	 */
	private Role role;

	/**
	 * {@inheritDoc}
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the primary key of authority.
	 * 
	 * @param id
	 *          the new primary key of authority
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the {@link Role} granted to the user.
	 * 
	 * @return the {@link Role} granted to the user
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "authority", nullable = false)
	public Role getRole() {
		return role;
	}

	/**
	 * Sets the {@link Role} granted to the user.
	 * 
	 * @param role
	 *          the new {@link Role} granted to the user
	 */
	public void setRole(Role role) {
		this.role = role;
	}

}
