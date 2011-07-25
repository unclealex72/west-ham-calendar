/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.google.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

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
public class CalendarService extends com.google.gdata.client.calendar.CalendarService {

	private final String i_oauthToken;
	
	public CalendarService(String applicationName, String oauthToken) {
		super(applicationName);
		i_oauthToken = oauthToken;
	}

	@Override
	public GDataRequest createRequest(RequestType type, URL requestUrl, ContentType contentType) throws IOException,
			ServiceException {
		return super.createRequest(type, authorise(requestUrl), contentType);
	}
	
	protected URL authorise(URL url) throws MalformedURLException, UnsupportedEncodingException {
		String unauthorisedUrl = url.toString();
		String bareUrl;
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		int paramStart = unauthorisedUrl.indexOf('?');
		if (paramStart < 0) {
			bareUrl = unauthorisedUrl;
		}
		else {
			bareUrl = unauthorisedUrl.substring(0, paramStart);
			Iterable<String> existingParameters = Splitter.on('&').split(unauthorisedUrl.substring(paramStart + 1));
			for (String existingParameter : existingParameters) {
				int equalPos = existingParameter.indexOf('=');
				String key;
				String value;
				if (equalPos < 0) {
					key = existingParameter;
					value = "";
				}
				else {
					key = existingParameter.substring(0, equalPos);
					value = existingParameter.substring(equalPos + 1);
				}
				parameters.put(key, value);
			}
		}
		parameters.put("oauth_token", getOauthToken());
		parameters.put("max-results", Integer.toString(Integer.MAX_VALUE));
		URL authorisedUrl = new URL(String.format("%s?%s", bareUrl, Joiner.on('&').withKeyValueSeparator("=").join(parameters)));
		return authorisedUrl;
	}

	public String getOauthToken() {
		return i_oauthToken;
	}
}
