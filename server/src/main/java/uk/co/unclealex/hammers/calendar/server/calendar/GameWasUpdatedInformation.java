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

package uk.co.unclealex.hammers.calendar.server.calendar;


/**
 * A {@link GameUpdateInformation} bean that can be used to say whether a game was updated or not.
 * @author alex
 *
 */
public class GameWasUpdatedInformation implements GameUpdateInformation {

	/**
	 * True if the game was updated, false otherwise.
	 */
	private final boolean i_updated;
	
	/**
	 * Create a new instance of i_class.
	 * @param updated True if the game was updated, false otherwise.
	 */
	public GameWasUpdatedInformation(boolean updated) {
		super();
		i_updated = updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(GameUpdateInformationVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Checks if is true if the game was updated, false otherwise.
	 * 
	 * @return the true if the game was updated, false otherwise
	 */
	public boolean isUpdated() {
		return i_updated;
	}
}
