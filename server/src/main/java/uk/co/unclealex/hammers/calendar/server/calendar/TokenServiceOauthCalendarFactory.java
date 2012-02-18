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

package uk.co.unclealex.hammers.calendar.server.calendar;

import uk.co.unclealex.hammers.calendar.server.auth.TokenService;

/**
 * An {@link AbstractOauthCalendarFactory} that uses a {@link TokenService}.
 * @author alex
 *
 */
public class TokenServiceOauthCalendarFactory extends AbstractOauthCalendarFactory {

	private TokenService i_tokenService;
	
	public String getRefreshToken() {
		return getTokenService().getCurrentTokens().getRefreshToken();
	}

	public String getAccessToken() {
		return getTokenService().getCurrentTokens().getAccessToken();
	}

	public void setAccessToken(String accessToken) {
		getTokenService().updateAccessToken(accessToken);
	}

	public TokenService getTokenService() {
		return i_tokenService;
	}

	public void setTokenService(TokenService tokenService) {
		i_tokenService = tokenService;
	}

	
}
