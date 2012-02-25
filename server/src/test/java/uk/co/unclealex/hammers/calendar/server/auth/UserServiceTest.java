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

package uk.co.unclealex.hammers.calendar.server.auth;

import java.util.Arrays;
import java.util.SortedSet;

import javax.persistence.Table;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import uk.co.unclealex.hammers.calendar.server.dao.UserDao;
import uk.co.unclealex.hammers.calendar.server.model.Authority;
import uk.co.unclealex.hammers.calendar.server.model.User;
import uk.co.unclealex.hammers.calendar.shared.exceptions.NoSuchUsernameException;
import uk.co.unclealex.hammers.calendar.shared.exceptions.UsernameAlreadyExistsException;
import uk.co.unclealex.hammers.calendar.shared.model.Role;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * @author alex
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/application-contexts/dao/context.xml", "/application-contexts/dao/test-db.xml",
		"/application-contexts/auth/context.xml" })
@SuppressWarnings("deprecation")
public class UserServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	private static final String AUTHORITY_TABLE_NAME = Authority.class.getAnnotation(Table.class).name();
	private static final String USERS_TABLE_NAME = User.class.getAnnotation(Table.class).name();

	@Autowired
	private SimpleJdbcTemplate simpleJdbcTemplate;

	@Autowired
	private UserService userService;
	@Autowired
	private UserDao userDao;
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Before
	public void setup() {
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate, USERS_TABLE_NAME, AUTHORITY_TABLE_NAME);
	}

	@Test
	public void cannotAlterNonexistingUser() {
		try {
			userService.alterUser("frank", "smith", Role.ROLE_ADMIN);
			Assert.fail("Altering a non existing user succeeded.");
		}
		catch (NoSuchUsernameException e) {
			// Expected.
		}
	}
	
	@Test
	public void cannotRemoveNonexistingUser() {
		try {
			userService.removeUser("frank");
			Assert.fail("Altering a non existing user succeeded.");
		}
		catch (NoSuchUsernameException e) {
			// Expected.
		}
	}

	@Test
	public void cannotAlterNonexistingUserPassword() {
		try {
			userService.alterPassword("frank", "smith");
			Assert.fail("Altering a non existing user's password succeeded.");
		}
		catch (NoSuchUsernameException e) {
			// Expected.
		}
	}

	@Test
	public void testAddUser() throws UsernameAlreadyExistsException {
		userService.addUser("user", "user password", Role.ROLE_USER);
		userService.addUser("admin", "admin password", Role.ROLE_ADMIN);
		checkUser("admin", "admin password", Role.ROLE_USER, Role.ROLE_ADMIN);
		checkUser("user", "user password", Role.ROLE_USER);
		Assert.assertEquals("The wrong number of users were found.", 2, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, USERS_TABLE_NAME));
	}

	@Test
	public void testAddSameUserFails() throws UsernameAlreadyExistsException {
		userService.addUser("user", "user password", Role.ROLE_USER);
		try {
			userService.addUser("user", "user password", Role.ROLE_USER);
			Assert.fail("Adding the same user twice succeeded.");
		}
		catch (UsernameAlreadyExistsException e) {
			// This is expected.
		}
	}
	
	@Test
	public void testAlterUser() throws NoSuchUsernameException, UsernameAlreadyExistsException {
		userService.addUser("me", "beef", Role.ROLE_ADMIN);
		userService.alterUser("me", "stew", Role.ROLE_USER);
		checkUser("me", "stew", Role.ROLE_USER);
		userService.alterUser("me", "cake", Role.ROLE_ADMIN);
		checkUser("me", "cake", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	@Test
	public void testAlterUserPassword() throws NoSuchUsernameException, UsernameAlreadyExistsException {
		userService.addUser("me", "beef", Role.ROLE_ADMIN);
		userService.alterPassword("me", "stew");
		checkUser("me", "stew", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	@Test
	public void testNonexistentUserCannotLogin() {
		try {
			authenticate("frank", "boing");
			Assert.fail("A non exisitent user got authenticated.");
		}
		catch (BadCredentialsException e) {
			// Expected.
		}
	}

	@Test
	public void testInccorectPasswordCannotLogin() throws UsernameAlreadyExistsException {
		userService.addUser("me", "you", Role.ROLE_USER);
		try {
			authenticate("me", "boing");
			Assert.fail("A non exisitent user got authenticated.");
		}
		catch (BadCredentialsException e) {
			// Expected.
		}
	}
	
	@Test
	public void testEnsureDefaultUserExists() {
		userService.ensureDefaultUsersExists("username", "password");
		checkUser("username", "password", Role.ROLE_USER, Role.ROLE_ADMIN);
		userService.ensureDefaultUsersExists("username", "password");
		checkUser("username", "password", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	@Test
	public void testRemoveUser() throws NoSuchUsernameException, UsernameAlreadyExistsException {
		userService.addUser("username", "password", Role.ROLE_ADMIN);
		userService.removeUser("username");
		Assert.assertEquals("The wrong number of users were found.", 0, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, USERS_TABLE_NAME));
		Assert.assertEquals("The wrong number of authorities were found.", 0, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, AUTHORITY_TABLE_NAME));
		try {
			authenticate("username", "password");
			Assert.fail("A removed user could still log in.");
		}
		catch (BadCredentialsException e) {
			// Expected.
		}
	}
	
	/**
	 * @param username
	 * @param password
	 * @param expectedRoles
	 */
	protected void checkUser(String username, String password, Role... expectedRoles) {
		User user = userDao.findByKey(username);
		Assert.assertNotNull("Cannot find user " + username, user);
		Authentication authentication = authenticate(username, password);
		Assert.assertTrue("User " + username + " was not authenticated.", authentication.isAuthenticated());
		String actualUsername = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
		Assert.assertEquals("The returned security token has the wrong username.", username, actualUsername);
		Arrays.sort(expectedRoles);
		Function<GrantedAuthority, Role> f = new Function<GrantedAuthority, Role>() {
			@Override
			public Role apply(GrantedAuthority grantedAuthority) {
				return Role.valueOf(grantedAuthority.getAuthority());
			}
		};
		SortedSet<Role> actualRoles = Sets.newTreeSet(Iterables.transform(authentication.getAuthorities(), f));
		Assert.assertArrayEquals("The wrong roles were found.", expectedRoles, Iterables.toArray(actualRoles, Role.class));
	}

	protected Authentication authenticate(String username, String password) {
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
}
