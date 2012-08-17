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

package com.google.api.client.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;


/**
 * A factory class to create a {@link GoogleJsonResponseException} for forbidden responses.
 * @author alex
 *
 */
public final class GoogleJsonResponseExceptionFactory {

	/**
	 * Creates a new GoogleJsonResponseException object.
	 * 
	 * @return A forbidden response.
	 */
	public static GoogleJsonResponseException createGoogleJsonResponseException() {
		JsonFactory jsonFactory = new JacksonFactory();
		HttpRequest httpRequest = new HttpRequest(new NetHttpTransport(), HttpMethod.POST);
		httpRequest.setUrl(new GenericUrl("http://myhouse.com"));
		LowLevelHttpResponse lowLevelHttpResponse = new LowLevelHttpResponse() {
			
			@Override
			public String getStatusLine() {
				return "Yuk";
			}
			@Override
			public int getStatusCode() {
				return HttpStatusCodes.STATUS_CODE_FORBIDDEN;
			}
			@Override
			public String getReasonPhrase() {
				return "Yuk";
			}
			
			@Override
			public String getHeaderValue(int index) {
				return "7";
			}
			
			@Override
			public String getHeaderName(int index) {
				return "7";
			}
			
			@Override
			public int getHeaderCount() {
				return 0;
			}
			
			@Override
			public String getContentType() {
				return "text/plain";
			}
			
			@Override
			public long getContentLength() {
				return 0;
			}
			
			@Override
			public String getContentEncoding() {
				return "utf-8";
			}
			
			@Override
			public InputStream getContent() throws IOException {
				return new ByteArrayInputStream(new byte[0]);
			}
		};
		HttpResponse httpResponse = new HttpResponse(httpRequest, lowLevelHttpResponse);
		return GoogleJsonResponseException.from(jsonFactory, httpResponse);
	}
}
