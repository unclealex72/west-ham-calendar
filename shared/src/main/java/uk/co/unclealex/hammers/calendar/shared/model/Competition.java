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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public enum Competition {
	PREM("Premiership", true, "PREM"),
	LGCP("League Cup", false, "LGCP"),
	FACP("FA Cup", false, "FACP"),
	FLC("Championship", true, "FLC", "FLD1"),
	FLCPO("Play-Offs", false, "FLD1 P/O");
	
	private static SortedMap<String, Competition> COMPETITIONS_BY_TOKEN;
	static {
		Comparator<String> longestFirstComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int cmp = s2.length() - s1.length();
				return cmp == 0?s1.compareTo(s2):cmp;
			}
		};
		COMPETITIONS_BY_TOKEN = new TreeMap<String, Competition>(longestFirstComparator);
		for (Competition competition : Competition.values()) {
			for (String token : competition.getTokens()) {
				COMPETITIONS_BY_TOKEN.put(token, competition);
			}
		}
	}
	
	public static Competition findByToken(String token) throws IllegalArgumentException {
		for (Map.Entry<String, Competition> entry : COMPETITIONS_BY_TOKEN.entrySet()) {
			if (token.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		throw new IllegalArgumentException(token + " is not a valid competition token.");
	}
	
	private String[] i_tokens;
	private boolean i_league;
	private String i_name;
	
	private Competition(String name, boolean league, String... tokens) {
		i_name = name;
		i_league = league;
		i_tokens = tokens;
	}

	public String getName() {
		return i_name;
	}
	
	public String[] getTokens() {
		return i_tokens;
	}

	/**
	 * @return True if this is a league competition, false otherwise.
	 */
	public boolean isLeague() {
		return i_league;
	}
}
