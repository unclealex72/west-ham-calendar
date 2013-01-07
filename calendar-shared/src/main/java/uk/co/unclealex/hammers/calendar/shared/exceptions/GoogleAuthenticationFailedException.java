/**
 * Copyright 2012 Alex Jones
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

package uk.co.unclealex.hammers.calendar.shared.exceptions;


/**
 * An exception that is thrown if the application cannot authenticate itself to
 * the Google servers.
 * 
 * @author alex
 */
public class GoogleAuthenticationFailedException extends Exception {

	/**
	 * Instantiates a new google authentication failed exception.
	 */
	public GoogleAuthenticationFailedException() {
		super();
	}

	/**
	 * Instantiates a new google authentication failed exception.
	 * 
	 * @param message
	 *          the message
	 * @param cause
	 *          the cause
	 */
	public GoogleAuthenticationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new google authentication failed exception.
	 * 
	 * @param message
	 *          the message
	 */
	public GoogleAuthenticationFailedException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new google authentication failed exception.
	 * 
	 * @param cause
	 *          the cause
	 */
	public GoogleAuthenticationFailedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
