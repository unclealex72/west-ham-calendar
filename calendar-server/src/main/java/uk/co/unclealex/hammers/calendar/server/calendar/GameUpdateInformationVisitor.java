/**
 * Copyright 2010-2012 Alex Jones
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

package uk.co.unclealex.hammers.calendar.server.calendar;


/**
 * 
 * A visitor for {@link GameUpdateInformation} beans.
 * 
 * @author alex
 * 
 */
public interface GameUpdateInformationVisitor {

	/**
	 * Visit an instance of a {@link GameUpdateInformation}.
	 * @param gameUpdateInformation The {@link GameUpdateInformation} to visit.
	 */
	void visit(GameUpdateInformation gameUpdateInformation);

	/**
	 * Visit an instance of a {@link GameWasUpdatedInformation}.
	 * @param gameWasUpdatedInformation The {@link GameWasUpdatedInformation} to visit.
	 */
	void visit(GameWasUpdatedInformation gameWasUpdatedInformation);

	/**
	 * Visit an instance of a {@link GameWasCreatedInformation}.
	 * @param gameWasCreatedInformation The {@link GameWasCreatedInformation} to visit.
	 */
	void visit(GameWasCreatedInformation gameWasCreatedInformation);

	/**
	 * The default implementation of a {@link GameUpdateInformationVisitor} that
	 * throws an {@link IllegalArgumentException} if the supplied.
	 * 
	 * {@link GameUpdateInformation} bean is unknown.
	 * 
	 * @author alex
	 */
	abstract class Default implements GameUpdateInformationVisitor {

		/**
		 * Always throw an {@link IllegalArgumentException}.
		 * @param gameUpdateInformation An instance of an unknown type of {@link GameUpdateInformation}
		 * @throws IllegalArgumentException Always thrown.
		 */
		public void visit(GameUpdateInformation gameUpdateInformation) throws IllegalArgumentException {
			throw new IllegalArgumentException("The class " + gameUpdateInformation.getClass() + " is unrecognised.");
		}
	}
}
