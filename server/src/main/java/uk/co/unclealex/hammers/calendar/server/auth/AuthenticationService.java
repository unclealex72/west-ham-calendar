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

import uk.co.unclealex.hammers.calendar.shared.services.SecurityInvalidator;


/**
 * The Interface AuthenticationService.
 * 
 * @author alex
 */
public interface AuthenticationService {

	/**
	 * Get the logged in user's username.
	 * @return The logged in user's username if someone is logged in, or null otherwise.
	 */
	String getUserPrincipal();

	/**
	 * Log out the current user.
	 * @param securityInvalidator The {@link SecurityInvalidator} to use to log out the current user.
	 */
	void logout(SecurityInvalidator securityInvalidator);

	/**
	 * Authenticate a user.
	 * @param username The user's username.
	 * @param password The user's password.
	 * @param securityInvalidator The {@link SecurityInvalidator} to use to log out the current user.
	 * @return True if authentication succeeded, false otherwise.
	 */
	boolean authenticate(String username, String password, SecurityInvalidator securityInvalidator);

	/**
	 * Indicate whether someone is currently logged in.
	 * @return True if someone is logged in, false otherwise.
	 */
	boolean isUserAuthenticated();

}
