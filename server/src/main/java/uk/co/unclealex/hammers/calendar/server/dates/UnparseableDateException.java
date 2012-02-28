/**
 * Copyright 2010 Alex Jones
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
package uk.co.unclealex.hammers.calendar.server.dates;

/**
 * An exception that is thrown for date strings that are not parseable.
 * @author alex
 *
 */
public class UnparseableDateException extends Exception {

	/**
	 * @see Exception
	 */
	public UnparseableDateException() {
	}

	/**
	 * @see Exception
	 * @param message The exception's message.
	 */
	public UnparseableDateException(String message) {
		super(message);
	}

	/**
	 * @see Exception
	 * @param cause The underlying cause of this exception.
	 */
	public UnparseableDateException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception
	 * @param cause The underlying cause of this exception.
	 * @param message The exception's message.
	 */
	public UnparseableDateException(String message, Throwable cause) {
		super(message, cause);
	}

}
