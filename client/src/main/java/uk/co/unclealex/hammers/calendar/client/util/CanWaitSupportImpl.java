/**
 * 
 */
package uk.co.unclealex.hammers.calendar.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

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
public class CanWaitSupportImpl implements CanWaitSupport {

	private final Map<HasEnabled, Boolean> i_statesByHasEnabled = new HashMap<HasEnabled, Boolean>();
	private final List<HasEnabled> i_hasEnableds = new ArrayList<HasEnabled>();
	
	@Override
	public void startWaiting() {
		getStatesByHasEnabled().clear();
		for (HasEnabled hasEnabled : getHasEnableds()) {
			getStatesByHasEnabled().put(hasEnabled, hasEnabled.isEnabled());
			hasEnabled.setEnabled(false);
		}
	}

	@Override
	public void stopWaiting() {
		for (Entry<HasEnabled, Boolean> entry : getStatesByHasEnabled().entrySet()) {
			entry.getKey().setEnabled(entry.getValue());
		}
	}

	@Override
	public CanWaitSupport wrap(HasEnabled... newHasEnableds) {
		List<HasEnabled> hasEnableds = getHasEnableds();
		hasEnableds.clear();
		hasEnableds.addAll(Arrays.asList(newHasEnableds));
		return this;
	}
	
	@Override
	public CanWaitSupport wrap(HasWidgets hasWidgets) {
		List<HasEnabled> hasEnableds = getHasEnableds();
		hasEnableds.clear();
		for (Iterator<Widget> iter = hasWidgets.iterator(); iter.hasNext(); ) {
			Widget widget = iter.next();
			if (widget instanceof HasEnabled) {
				hasEnableds.add((HasEnabled) widget);
			}
		}
		return this;
	}
	
	public Map<HasEnabled, Boolean> getStatesByHasEnabled() {
		return i_statesByHasEnabled;
	}

	public List<HasEnabled> getHasEnableds() {
		return i_hasEnableds;
	}

}
