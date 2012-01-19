/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class HtmlDocumentServiceImpl implements HtmlDocumentService {

	@Override
	public Map<URL, String> load(Collection<URL> urls) throws IOException {
		Map<URL, String> result = new HashMap<URL, String>();
		for (URL url : urls) {
			result.put(url, load(url));
		}
		return result;
	}

	@Override
	public String load(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer);
			return writer.toString();
		}
		finally {
			IOUtils.closeQuietly(is);
		}
	}

}
