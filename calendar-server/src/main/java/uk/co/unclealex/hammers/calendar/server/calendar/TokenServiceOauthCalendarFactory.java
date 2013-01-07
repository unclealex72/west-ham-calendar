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

package uk.co.unclealex.hammers.calendar.server.calendar;

import uk.co.unclealex.hammers.calendar.server.auth.TokenService;


/**
 * An {@link AbstractOauthCalendarFactory} that uses a {@link TokenService}.
 * @author alex
 *
 */
public class TokenServiceOauthCalendarFactory extends AbstractOauthCalendarFactory {

	/**
	 * The {@link TokenService} used to persist and get Google tokens.
	 */
	private TokenService tokenService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void installTokens(String accessToken, String refreshToken) {
		getTokenService().installTokens(accessToken, refreshToken);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRefreshToken() {
		return getTokenService().getCurrentTokens().getRefreshToken();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAccessToken() {
		return getTokenService().getCurrentTokens().getAccessToken();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAccessToken(String accessToken) {
		getTokenService().updateAccessToken(accessToken);
	}

	/**
	 * Gets the {@link TokenService} used to persist and get Google tokens.
	 * 
	 * @return the {@link TokenService} used to persist and get Google tokens
	 */
	public TokenService getTokenService() {
		return tokenService;
	}

	/**
	 * Sets the {@link TokenService} used to persist and get Google tokens.
	 * 
	 * @param tokenService
	 *          the new {@link TokenService} used to persist and get Google tokens
	 */
	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	
}
