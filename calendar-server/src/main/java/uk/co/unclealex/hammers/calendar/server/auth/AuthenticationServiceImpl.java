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

import java.util.Collection;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import uk.co.unclealex.hammers.calendar.shared.services.SecurityInvalidator;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;


/**
 * The default implementation of {@link AuthenticationService}.
 * 
 * @author alex
 * 
 */
public class AuthenticationServiceImpl implements AuthenticationService {

	/**
	 * The Spring {@link AuthenticationManager} used to authenticate users.
	 */
	private AuthenticationManager i_authenticationManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authenticate(String username, String password, SecurityInvalidator securityInvalidator) {
		try {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
			getAuthenticationManager().authenticate(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return true;
		}
		catch (AuthenticationException e) {
			logout(securityInvalidator);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || isAuthenticatedAnonymously(authentication)) {
			return null;
		}
		return ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
	}

	/**
	 * Check to see if an {@link Authentication} object is authenticated anonymously.
	 * @param authentication The authentication object to check.
	 * @return True if the authentication is anonymous, false otherwise.
	 */
	protected boolean isAuthenticatedAnonymously(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.size() == 1 && "ROLE_ANONYMOUS".equals(authorities.iterator().next().getAuthority());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout(SecurityInvalidator securityInvalidator) {
		securityInvalidator.invalidate();
		SecurityContextHolder.clearContext();
	}

	/**
	 * Find whether the authenticated user has a given role.
	 * @param role The role to check for.
	 * @return True if the user has the given role, false otherwise.
	 */
	protected boolean isUserInRole(String role) {
		Function<GrantedAuthority, String> authorityFunction = new Function<GrantedAuthority, String>() {
			@Override
			public String apply(GrantedAuthority grantedAuthority) {
				return grantedAuthority.getAuthority();
			}
		};
		return Iterables
				.contains(Iterables.transform(SecurityContextHolder.getContext().getAuthentication().getAuthorities(),
						authorityFunction), role);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUserAuthenticated() {
		return isUserInRole("ROLE_USER");
	}

	/**
	 * Gets the Spring {@link AuthenticationManager} used to authenticate users.
	 * 
	 * @return the Spring {@link AuthenticationManager} used to authenticate users
	 */
	public AuthenticationManager getAuthenticationManager() {
		return i_authenticationManager;
	}

	/**
	 * Sets the Spring {@link AuthenticationManager} used to authenticate users.
	 * 
	 * @param authenticationManager
	 *          the new Spring {@link AuthenticationManager} used to authenticate
	 *          users
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		i_authenticationManager = authenticationManager;
	}

}
