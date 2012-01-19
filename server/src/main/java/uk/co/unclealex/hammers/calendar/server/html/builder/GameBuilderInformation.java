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
package uk.co.unclealex.hammers.calendar.server.html.builder;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import uk.co.unclealex.hammers.calendar.server.model.Game;
import uk.co.unclealex.hammers.calendar.shared.model.Month;

public class GameBuilderInformation {

	private List<Game> i_games = new LinkedList<Game>();
	private Month i_month;
	private int i_year;
	private URL i_url;
	
	public GameBuilderInformation(int year, URL url) {
		i_year = year;
		i_url = url;
	}
	
	public List<Game> getGames() {
		return i_games;
	}
	
	public void setGames(List<Game> games) {
		i_games = games;
	}
	
	public Month getMonth() {
		return i_month;
	}
	
	public void setMonth(Month month) {
		i_month = month;
	}
	
	public int getYear() {
		return i_year;
	}
	
	public void setYear(int year) {
		i_year = year;
	}

	public URL getUrl() {
		return i_url;
	}

	public void setUrl(URL url) {
		i_url = url;
	}

}
