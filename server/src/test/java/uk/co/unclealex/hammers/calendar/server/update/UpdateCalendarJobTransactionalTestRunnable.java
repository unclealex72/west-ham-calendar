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

package uk.co.unclealex.hammers.calendar.server.update;

import uk.co.unclealex.hammers.calendar.server.dao.OauthTokenDao;
import uk.co.unclealex.hammers.calendar.server.model.OauthToken;
import uk.co.unclealex.hammers.calendar.server.model.OauthTokenType;

/**
 * @author alex
 *
 */
public class UpdateCalendarJobTransactionalTestRunnable implements Runnable {

	private OauthTokenDao i_oauthTokenDao;
	
	@Override
	public void run() {
		clear();
		OauthToken oauthToken = new OauthToken(OauthTokenType.ACCESS, "token");
		getOauthTokenDao().saveOrUpdate(oauthToken);
		clear();
	}

	protected void clear() {
		OauthTokenDao oauthTokenDao = getOauthTokenDao();
		for (OauthToken oauthToken : oauthTokenDao.getAll()) {
			oauthTokenDao.remove(oauthToken.getId());
		}
	}
	
	public OauthTokenDao getOauthTokenDao() {
		return i_oauthTokenDao;
	}

	public void setOauthTokenDao(OauthTokenDao oauthTokenDao) {
		i_oauthTokenDao = oauthTokenDao;
	}

}
