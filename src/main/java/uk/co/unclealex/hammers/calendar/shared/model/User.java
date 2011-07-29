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
package uk.co.unclealex.hammers.calendar.shared.model;

import java.io.Serializable;

/**
 * @author aj016368
 *
 */
public class User implements Serializable {

  private String i_username;
  private boolean i_loggedIn;
  private Role i_role;
  
  protected User() {
    super();
  }

  public User(String username, boolean loggedIn, Role role) {
    super();
    i_username = username;
    i_loggedIn = loggedIn;
    i_role = role;
  }

  public String getUsername() {
    return i_username;
  }

  public Role getRole() {
    return i_role;
  }

  public boolean isLoggedIn() {
    return i_loggedIn;
  }
  
  
}
