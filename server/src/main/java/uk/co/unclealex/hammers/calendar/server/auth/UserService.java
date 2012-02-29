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
package uk.co.unclealex.hammers.calendar.server.auth;

import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.Role;


/**
 * A service for adding, removing and altering users and their roles.
 * @author alex
 *
 */
public interface UserService {

	/**
	 * Make sure that a default user exists.
	 * @param username The username of the default user.
	 * @param password The password of the default user.
	 */
	void ensureDefaultUsersExists(String username, String password);
	
	/**
	 * Add a new user.
	 * @param username The new user's username.
	 * @param password The new user's password.
	 * @param role The user's most authoritative role.
	 * @throws UsernameAlreadyExistsException Thrown if a user with the given username already exists.
	 */
	void addUser(String username, String password, Role role) throws UsernameAlreadyExistsException;
	
	/**
	 * Remove a user.
	 * @param username The username of the user to remove.
	 * @throws NoSuchUsernameException Thrown if no such user exists.
	 */
	void removeUser(String username) throws NoSuchUsernameException;
	
	/**
	 * Change a user.
	 * @param username The username of the user to change.
	 * @param newPassword The user's new password.
	 * @param newRole The user's new most authoritative role.
	 * @throws NoSuchUsernameException Thrown if no such user exists.
	 */
  void alterUser(String username, String newPassword, Role newRole) throws NoSuchUsernameException;

  /**
   * Alter a user's password.
	 * @param username The username of the user to change.
	 * @param newPassword The user's new password.
	 * @throws NoSuchUsernameException Thrown if no such user exists.
   */
  void alterPassword(String username, String newPassword) throws NoSuchUsernameException;
}
