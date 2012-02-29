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

package uk.co.unclealex.hammers.calendar.server.auth;

import org.springframework.transaction.annotation.Transactional;

import uk.co.unclealex.hammers.calendar.server.dao.OauthTokenDao;
import uk.co.unclealex.hammers.calendar.server.model.OauthToken;
import uk.co.unclealex.hammers.calendar.server.model.OauthTokenType;


/**
 * The default implementation of {@link TokenService}.
 * @author alex
 *
 */
@Transactional
public class TokenServiceImpl implements TokenService {

	/**
	 * The {@OauthTokenDao} to use for token persistence.
	 */
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
		return oauthToken == null ? null : oauthToken.getToken();
	}
	
	/**
	 * Gets the {@OauthTokenDao} to use for token persistence.
	 * 
	 * @return the {@OauthTokenDao} to use for token persistence
	 */
	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	/**
	 * Sets the {@OauthTokenDao} to use for token persistence.
	 * 
	 * @param oauthTokenDao
	 *          the new {@OauthTokenDao} to use for token
	 *          persistence
	 */
	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}

}
