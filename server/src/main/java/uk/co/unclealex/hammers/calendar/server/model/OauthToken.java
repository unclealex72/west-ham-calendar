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
package uk.co.unclealex.hammers.calendar.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * An oauth token is used by Google to authenticate i_application.
 * @author alex
 *
 */
@Entity
@Table(name = "oauth")
public class OauthToken extends AbstractBusinessKeyBasedModel<OauthTokenType, OauthToken> {

	/**
	 * The primary key of i_token.
	 */
	private Integer i_id;
	
	/**
	 * The type of i_token.
	 */
	private OauthTokenType i_tokenType;
	
	/**
	 * The token string to be passed to and from Google.
	 */
	private String i_token;

	/**
	 * Instantiates a new oauth token.
	 */
	protected OauthToken() {
		// Default constructor for ORM.
	}

	/**
	 * Instantiates a new oauth token.
	 * 
	 * @param tokenType
	 *          the token type
	 * @param token
	 *          the token
	 */
	public OauthToken(OauthTokenType tokenType, String token) {
		super();
		i_tokenType = tokenType;
		i_token = token;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public OauthTokenType getBusinessKey() {
		return getTokenType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBusinessKey(OauthTokenType businessKey) {
		setTokenType(businessKey);
	}

	/**
	 * Gets the type of i_token.
	 * 
	 * @return the type of i_token
	 */
	@Enumerated(EnumType.STRING)
	@Column(unique = true, nullable = false)
	public OauthTokenType getTokenType() {
		return i_tokenType;
	}

	/**
	 * Sets the type of i_token.
	 * 
	 * @param tokenType
	 *          the new type of i_token
	 */
	public void setTokenType(OauthTokenType tokenType) {
		i_tokenType = tokenType;
	}

	/**
	 * Gets the token string to be passed to and from Google.
	 * 
	 * @return the token string to be passed to and from Google
	 */
	@Column(nullable = false)
	public String getToken() {
		return i_token;
	}

	/**
	 * Sets the token string to be passed to and from Google.
	 * 
	 * @param token
	 *          the new token string to be passed to and from Google
	 */
	public void setToken(String token) {
		i_token = token;
	}

	/**
	 * {@inheritDoc}
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return i_id;
	}

	/**
	 * Sets the primary key of i_token.
	 * 
	 * @param id
	 *          the new primary key of i_token
	 */
	public void setId(Integer id) {
		i_id = id;
	}

}
