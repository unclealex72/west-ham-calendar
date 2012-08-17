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

package uk.co.unclealex.hammers.calendar.server.update;

import org.springframework.transaction.annotation.Transactional;


/**
 * A {@link Runnable} that wraps and delegates to another {@link Runnable} but makes
 * sure it is run in a {@link Transactional} context. This class is required to make sure
 * that jobs not run in a servlet based thread are still transactional.
 * @author alex
 *
 */
@Transactional
public class TransactionalRunnableDelegate implements Runnable {

	/**
	 * The {@link Runnable} to wrap and delegate to.
	 */
	private Runnable i_runnable;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		getRunnable().run();
	}

	/**
	 * Gets the {@link Runnable} to wrap and delegate to.
	 * 
	 * @return the {@link Runnable} to wrap and delegate to
	 */
	public Runnable getRunnable() {
		return i_runnable;
	}

	/**
	 * Sets the {@link Runnable} to wrap and delegate to.
	 * 
	 * @param runnable
	 *          the new {@link Runnable} to wrap and delegate to
	 */
	public void setRunnable(Runnable runnable) {
		i_runnable = runnable;
	}

	
}
