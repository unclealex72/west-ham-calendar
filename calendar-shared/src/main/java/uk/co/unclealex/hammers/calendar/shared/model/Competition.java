/**
 * Copyright 2012 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * The different competitions that West Ham have taken part in.
 * 
 * @author alex
 * 
 */
public enum Competition {
	/**
	 * The FA Premiership.
	 */
	PREM("Premiership", true, "PREM"),

	/**
	 * The League Cup.
	 */
	LGCP("League Cup", false, "LGCP"),

	/**
	 * The FA Cup.
	 */
	FACP("FA Cup", false, "FACP"),

	/**
	 * The Championship.
	 */
	FLC("Championship", true, "FLC", "FLD1"),

	/**
	 * The Championship play-offs.
	 */
	FLCPO("Play-Offs", false, "FLD1 P/O", "FLC P/O");

	/**
	 * Map all competitions by their token.
	 */
	private static SortedMap<String, Competition> COMPETITIONS_BY_TOKEN;
	static {
		Comparator<String> longestFirstComparator = new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int cmp = s2.length() - s1.length();
				return cmp == 0 ? s1.compareTo(s2) : cmp;
			}
		};
		COMPETITIONS_BY_TOKEN = new TreeMap<String, Competition>(longestFirstComparator);
		for (Competition competition : Competition.values()) {
			for (String token : competition.getTokens()) {
				COMPETITIONS_BY_TOKEN.put(token, competition);
			}
		}
	}

	/**
	 * Get a competition by its token.
	 * @param token The token to look for.
	 * @return The competition for the token.
	 * @throws IllegalArgumentException Thrown if no such competition exists.
	 */
	public static Competition findByToken(String token) throws IllegalArgumentException {
		for (Map.Entry<String, Competition> entry : COMPETITIONS_BY_TOKEN.entrySet()) {
			if (token.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		throw new IllegalArgumentException(token + " is not a valid competition token.");
	}

	/**
	 * An array of tokens that can identify competition on the West Ham website.
	 */
	private String[] tokens;
	
	/**
	 * True if competition is a league competition, false otherwise.
	 */
	private boolean league;
	
	/**
	 * The name of the competition.
	 */
	private String name;

	/**
	 * Instantiates a new competition.
	 * 
	 * @param name
	 *          the name
	 * @param league
	 *          the league
	 * @param tokens
	 *          the tokens
	 */
	private Competition(String name, boolean league, String... tokens) {
		this.name = name;
		this.league = league;
		this.tokens = tokens;
	}

	/**
	 * Gets the name of the competition.
	 * 
	 * @return the name of the competition
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the an array of tokens that can identify competition on the West
	 * Ham website.
	 * 
	 * @return the an array of tokens that can identify competition on the
	 *         West Ham website
	 */
	public String[] getTokens() {
		return tokens;
	}

	/**
	 * Checks if is true if competition is a league competition, false
	 * otherwise.
	 * 
	 * @return the true if competition is a league competition, false
	 *         otherwise
	 */
	public boolean isLeague() {
		return league;
	}
}
