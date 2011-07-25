/**
 * 
 */
package uk.co.unclealex.hammers.calendar.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.co.unclealex.hammers.calendar.shared.model.CalendarColour;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

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
public class CalendarColoursCssServlet extends HttpServlet {

	private String i_content;
	private int i_length;
	
	@Override
	public void init() {
		Function<CalendarColour, String> f = new Function<CalendarColour, String>() {
			@Override
			public String apply(CalendarColour calendarColour) {
				return String.format(".%s { background-color: %s }", calendarColour.asStyle(), calendarColour.getRgb());
			}
		};
		String content = Joiner.on('\n').join(Iterables.transform(Arrays.asList(CalendarColour.values()), f));
		setLength(content.length());
		setContent(content);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		showContent(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		showContent(req, resp);
	}

	
	protected void showContent(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/css");
		resp.setContentLength(getLength());
		PrintWriter writer = resp.getWriter();
		writer.print(getContent());
		writer.close();
	}

	public String getContent() {
		return i_content;
	}

	public void setContent(String content) {
		i_content = content;
	}

	public int getLength() {
		return i_length;
	}

	public void setLength(int length) {
		i_length = length;
	}
}
