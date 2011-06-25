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
package uk.co.unclealex.hammers.calendar.model;

public enum Month {
	AUGUST("August", 0),
	SEPTEMBER("September", 0),
	OCTOBER("October", 0),
	NOVEMBER("November", 0),
	DECEMBER("December", 0),
	JANUARY("January", 1),
	FEBRUARY("February", 1),
	MARCH("March", 1),
	APRIL("April", 1),
	MAY("May", 1);
	
	public static Month findByName(String name) {
		for (Month month : Month.values()) {
			if (month.getName().equals(name)) {
				return month;
			}
		}
		return null;
	}
	
	private String i_name;
	private int i_yearOffset;
	
	private Month(String name, int yearOffset) {
		i_name = name;
		i_yearOffset = yearOffset;
	}

	public int getYearOffset() {
		return i_yearOffset;
	}
	
	public String getName() {
		return i_name;
	}
	
}
