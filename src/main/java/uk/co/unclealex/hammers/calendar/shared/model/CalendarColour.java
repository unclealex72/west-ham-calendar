/**
 * 
 */
package uk.co.unclealex.hammers.calendar.shared.model;


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
public enum CalendarColour {
	MEXICAN_RED("Mexican Red", "#A32929"),  
	NIGHT_SHADZ("Night Shadz", "#B1365F"),  
	PLUM("Plum", "#7A367A"),  
	DAISY_BUSH("Daisy Bush", "#5229A3"),  
	ASTRONAUT("Astronaut", "#29527A"),  
	ST_TROPAZ("St Tropaz", "#2952A3"),  
	ELM("Elm", "#1B887A"),  
	SEA_GREEN("Sea Green", "#28754E"),  
	SAN_FELIX("San Felix", "#0D7813"),  
	LIMEADE("Limeade", "#528800"),  
	CORN_HARVEST("Corn Harvest", "#88880E"),  
	PIRATE_GOLD("Pirate Gold", "#AB8B00"),  
	INDOCHINE("Indochine", "#BE6D00"),  
	RUST("Rust", "#B1440E"),  
	AU_CHICO("Au Chico", "#865A5A"),  
	OLD_LAVENDER("Old Lavender", "#705770"),  
	BLUE_BAYOUX("Blue Bayoux", "#4E5D6C"),  
	WAIKAWA_GRAY("Waikawa Gray", "#5A6986"),  
	CUTTY_SARK("Cutty Sark", "#4A716C"),  
	HEMLOCK("Hemlock", "#6E6E41"),  
	SHADOW("Shadow", "#8D6F47"),  
	PERU_TAN("Peru Tan", "#853104"),  
	CHERRYWOOD("Cherrywood", "#691426"),  
	BORDEAUX("Loulou", "#5C1158"),  
	VALHALLA("Valhalla", "#23164E"),  
	BISCAY("Biscay", "#182C57"),  
	GULF_BLUE("Gulf Blue", "#060D5E"),  
	PARSLEY("Parsley", "#125A12"),  
	DELL("Dell", "#2F6213"),  
	GREEN_LEAF("Green Leaf", "#2F6309"),  
	VERDUN_GREEN("Verdun Green", "#5F6B02"),  
	RUSTY_NAIL("Rusty Nail", "#875509"),  
	RUSTIER_NAIL("Rustier Nail", "#8C500B"),  
	SEPIA("Sepia", "#754916"),  
	BROWN_BRAMBLE("Brown Bramble", "#6B3304"),  
	LOULOU("Loulou", "#5B123B"),  
	VALENTINO("Valentino", "#42104A"),  
	ELEPHANT("Elephant", "#113F47"),  
	MINE_SHAFT("Mine Shaft", "#333333"),  
	EDEN("Eden", "#0F4B38"),  
	YUKON_GOLD("Yukon Gold", "#856508"),  
	MOCCACCINO("Moccaccino", "#711616");
	
	private final String i_name;
	private final String i_rgb;
	
	private CalendarColour(String name, String rgb) {
		i_name = name;
		i_rgb = rgb;
	}
	
	public String asStyle() {
		return "calendar_colour_" + toString().toLowerCase();
	}

	public String getName() {
		return i_name;
	}
	
	public String getRgb() {
		return i_rgb;
	}

}
