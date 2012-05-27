/**
 * Copyright 2012 Alex Jones
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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;


/**
 * A user is someone who can log into i_application.
 * @author aj016368
 *
 */
public class User implements Serializable {

	/**
	 * The user's username.
	 */
  private String i_username;
  
  /**
   * True if the user is logged in, false otherwise.
   */
  private boolean i_loggedIn;
  
  /**
   * The user's most senior role.
   */
  private Role i_role;
  
  /**
	 * Instantiates a new user.
	 */
  protected User() {
    super();
  }

  /**
	 * Instantiates a new user.
	 * 
	 * @param username
	 *          the username
	 * @param loggedIn
	 *          the logged in
	 * @param role
	 *          the role
	 */
  public User(String username, boolean loggedIn, Role role) {
    super();
    i_username = username;
    i_loggedIn = loggedIn;
    i_role = role;
  }

  /**
	 * Gets the user's username.
	 * 
	 * @return the user's username
	 */
  public String getUsername() {
    return i_username;
  }

  /**
	 * Gets the user's most senior role.
	 * 
	 * @return the user's most senior role
	 */
  public Role getRole() {
    return i_role;
  }

  /**
	 * Checks if is true if the user is logged in, false otherwise.
	 * 
	 * @return the true if the user is logged in, false otherwise
	 */
  public boolean isLoggedIn() {
    return i_loggedIn;
  }
  
  
}
