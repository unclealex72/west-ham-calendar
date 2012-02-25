/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name="users")
public class User extends AbstractBusinessKeyBasedModel<String, User> {

	private Integer i_id;
	private String i_username;
	private String i_password;
	private boolean i_enabled;
	private Set<Authority> i_authorities;

	@Override
	public String toString() {
		return getUsername();
	}
	
	@Override @Transient
	public String getBusinessKey() {
		return getUsername();
	}
	
	@Override
	public void setBusinessKey(String businessKey) {
		setUsername(businessKey);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof User && isEqual((User) obj);
	}
	
	@Column(nullable=false, name="username")
	public String getUsername() {
		return i_username;
	}

	public void setUsername(String username) {
		i_username = username;
	}

	@Column(nullable=false, name="password")
	public String getPassword() {
		return i_password;
	}

	public void setPassword(String password) {
		i_password = password;
	}

	@Column(name="enabled")
	public boolean isEnabled() {
		return i_enabled;
	}

	public void setEnabled(boolean enabled) {
		i_enabled = enabled;
	}

	@OneToMany(fetch=FetchType.EAGER, orphanRemoval=true)
	@Cascade(CascadeType.ALL)
	@JoinColumn(name="username", referencedColumnName="username")
	public Set<Authority> getAuthorities() {
		return i_authorities;
	}

	public void setAuthorities(Set<Authority> authorities) {
		i_authorities = authorities;
	}

	@Id @GeneratedValue
	public Integer getId() {
		return i_id;
	}

	public void setId(Integer id) {
		i_id = id;
	}
}
