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

package uk.co.unclealex.hammers.calendar.server.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.unclealex.hammers.calendar.server.model.OauthToken;
import uk.co.unclealex.hammers.calendar.server.model.OauthTokenType;

/**
 * @author alex
 *
 */
public class HibernateOauthTokenDaoTest extends DaoTest {

	
	public HibernateOauthTokenDaoTest() {
		super(OauthToken.class);
	}

	@Autowired OauthTokenDao oauthTokenDao;

	@Override
	protected void doSetup() throws Exception {
		// do nothing special.
	}

	@Test
	public void testFindByKey() {
		OauthToken accessOauthToken = new OauthToken(OauthTokenType.ACCESS, "1234");
		OauthToken refreshOauthToken = new OauthToken(OauthTokenType.REFRESH, "4321");
		oauthTokenDao.saveOrUpdate(accessOauthToken, refreshOauthToken);
		OauthToken actualOauthToken = oauthTokenDao.findByKey(OauthTokenType.REFRESH);
		Assert.assertNotNull("Could not find the refresh token.", actualOauthToken);
		Assert.assertEquals("The refresh token was incorrect.", refreshOauthToken, actualOauthToken);
	}

}
