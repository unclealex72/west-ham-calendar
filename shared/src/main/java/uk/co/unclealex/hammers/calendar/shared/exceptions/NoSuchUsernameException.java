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
 * An execption that is thrown if a user with the given username does not exist when it needs to.
 * @author aj016368
 *
 */
public class NoSuchUsernameException extends Exception {

  /**
	 * Instantiates a new no such username exception.
	 */
  public NoSuchUsernameException() {
    super();
  }

  /**
	 * Instantiates a new no such username exception.
	 * 
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
  public NoSuchUsernameException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
	 * Instantiates a new no such username exception.
	 * 
	 * @param message
	 *          the message
	 */
  public NoSuchUsernameException(String message) {
    super(message);
  }

  /**
	 * Instantiates a new no such username exception.
	 * 
	 * @param cause
	 *          the cause
	 */
  public NoSuchUsernameException(Throwable cause) {
    super(cause);
  }

}
