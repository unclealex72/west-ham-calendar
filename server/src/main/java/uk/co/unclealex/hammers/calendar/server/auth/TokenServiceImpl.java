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

package uk.co.unclealex.hammers.calendar.server.auth;

import uk.co.unclealex.hammers.calendar.server.dao.OauthTokenDao;
import uk.co.unclealex.hammers.calendar.server.model.OauthToken;
import uk.co.unclealex.hammers.calendar.server.model.OauthTokenType;

/**
 * @author alex
 *
 */
public class TokenServiceImpl implements TokenService {

	private OauthTokenDao i_oauthTokenDao;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void installTokens(String accessToken, String refreshToken) {
		createOrUpdateToken(accessToken, OauthTokenType.ACCESS);
		createOrUpdateToken(refreshToken, OauthTokenType.REFRESH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAccessToken(String refreshedAccessToken) {
		createOrUpdateToken(refreshedAccessToken, OauthTokenType.ACCESS);
	}

	/**
	 * Create or update a token.
	 * @param token The token to store.
	 * @param tokenType The type of the token.
	 */
	protected void createOrUpdateToken(String token, OauthTokenType tokenType) {
		OauthTokenDao oauthTokenDao = getOauthTokenDao();
		OauthToken oauthToken = oauthTokenDao.findByKey(tokenType);
		if (oauthToken == null) {
			oauthToken = new OauthToken(tokenType, token);
		}
		else {
			oauthToken.setToken(token);
		}
		oauthTokenDao.saveOrUpdate(oauthToken);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CurrentTokens getCurrentTokens() {
		return new CurrentTokens() {
			
			@Override
			public String getRefreshToken() {
				return findTokenByType(OauthTokenType.REFRESH);
			}
			
			@Override
			public String getAccessToken() {
				return findTokenByType(OauthTokenType.ACCESS);
			}
		};
	}

	/**
	 * Find a token by its type.
	 * @param oauthTokenType The type of token to search for.
	 * @return The stored token value or null if one could not be found.
	 */
	protected String findTokenByType(OauthTokenType oauthTokenType) {
		OauthToken oauthToken = getOauthTokenDao().findByKey(oauthTokenType);
		return oauthToken == null?null:oauthToken.getToken();
	}
	
	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}

}
