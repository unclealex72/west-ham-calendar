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
package uk.co.unclealex.hammers.calendar.shared.exceptions;


/**
 * An exception that is thrown if a user already exists.
 * @author alex
 *
 */
public class UsernameAlreadyExistsException extends Exception {

  /**
	 * Instantiates a new username already exists exception.
	 */
  public UsernameAlreadyExistsException() {
    super();
  }

  /**
	 * Instantiates a new username already exists exception.
	 * 
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
  public UsernameAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
	 * Instantiates a new username already exists exception.
	 * 
	 * @param message
	 *          the message
	 */
  public UsernameAlreadyExistsException(String message) {
    super(message);
  }

  /**
	 * Instantiates a new username already exists exception.
	 * 
	 * @param cause
	 *          the cause
	 */
  public UsernameAlreadyExistsException(Throwable cause) {
    super(cause);
  }

	
}
