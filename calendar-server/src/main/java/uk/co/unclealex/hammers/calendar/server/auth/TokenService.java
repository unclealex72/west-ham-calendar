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

package uk.co.unclealex.hammers.calendar.server.auth;


/**
 * A service to manage access and refresh tokens.
 * @author alex
 *
 */
public interface TokenService {

	/**
	 * Install the two tokens taken from a Google access token response.
	 * @param accessToken The access token.
	 * @param refreshToken The refresh token.
	 */
	void installTokens(String accessToken, String refreshToken);
	
	/**
	 * Store a new access token (in the case that an old one has expired).
	 * @param refreshedAccessToken The new token returned from Google.
	 */
	void updateAccessToken(String refreshedAccessToken);
	
	/**
	 * Retrieve the currently stored tokens.
	 * @return The currently stored tokens.
	 */
	CurrentTokens getCurrentTokens();
	
	/**
	 * A simple bean to hold the currently stored tokens.
	 * @author alex
	 *
	 */
	interface CurrentTokens {
		
		/**
		 * Get the current access token.
		 * @return The current access token or null if one does not exist.
		 */
		String getAccessToken();
		
		/**
		 * Get the current refresh token.
		 * @return The current refresh token or null if one does not exist.
		 */
		String getRefreshToken();
	}
}
