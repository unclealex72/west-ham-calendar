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
package uk.co.unclealex.hammers.calendar.html.builder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringGameBuilderActionFactory implements
		GameBuilderActionFactory, ApplicationContextAware {

	private ApplicationContext i_applicationContext;
	
	@Override
	public GameBuilderAction createMonthBuilderAction(GameBuilderInformation gameBuilderInformation) {
		return createGameBuilderAction("monthBuilderAction", gameBuilderInformation);
	}

	@Override
	public GameBuilderAction createSingleGameBuilderAction(GameBuilderInformation gameBuilderInformation) {
		return createGameBuilderAction("singleGameBuilderAction", gameBuilderInformation);
	}

	protected GameBuilderAction createGameBuilderAction(
			String beanName, GameBuilderInformation gameBuilderInformation) {
		GameBuilderAction gameBuilderAction = (GameBuilderAction) getApplicationContext().getBean(beanName, GameBuilderAction.class);
		gameBuilderAction.setGameBuilderInformation(gameBuilderInformation);
		return gameBuilderAction;
	}
	
	public ApplicationContext getApplicationContext() {
		return i_applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		i_applicationContext = applicationContext;
	}

}
