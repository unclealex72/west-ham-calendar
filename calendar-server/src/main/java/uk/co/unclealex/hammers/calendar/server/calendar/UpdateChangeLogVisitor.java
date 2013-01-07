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
 * A visitor for {@link UpdateChangeLog}s.
 * @author alex
 *
 */
public interface UpdateChangeLogVisitor {

	/**
	 * Visit an {@link UpdateChangeLog}.
	 * @param updateChangeLog The update change log to visit.
	 */
	void visit(UpdateChangeLog updateChangeLog);
	
	/**
	 * Visit an {@link AddedChangeLog}.
	 * @param addedChangeLog The added change log to visit.
	 */
	void visit(AddedChangeLog addedChangeLog);
	
	/**
	 * Visit an {@link UpdatedChangeLog}.
	 * @param updatedChangeLog The updated change log to visit.
	 */
	void visit(UpdatedChangeLog updatedChangeLog);

	/**
	 * Visit a {@link RemovedChangeLog}.
	 * @param removedChangeLog The removed change log to visit.
	 */
	void visit(RemovedChangeLog removedChangeLog);
	
	/**
	 * An implementation of the visitor that throws an {@link IllegalArgumentException} if
	 * an unknown type of chage log is visited.
	 * @author alex
	 *
	 */
	abstract class Default implements UpdateChangeLogVisitor {
		
		/**
		 * Throw an {@link IllegalArgumentException}.
		 * @param updateChangeLog The update change log to visit.
		 * @throws IllegalArgumentException This is always thrown.
		 */
		public void visit(UpdateChangeLog updateChangeLog) throws IllegalArgumentException {
			throw new IllegalArgumentException(updateChangeLog.getClass() + " is not a valid update change log class.");
		}
	}
}
