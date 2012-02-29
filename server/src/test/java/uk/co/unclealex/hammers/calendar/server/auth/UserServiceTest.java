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
 * The Class UserServiceTest.
 * 
 * @author alex
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/application-contexts/dao/context.xml", "/application-contexts/dao/test-db.xml",
		"/application-contexts/auth/context.xml" })
@SuppressWarnings("deprecation")
public class UserServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	/** The Constant AUTHORITY_TABLE_NAME. */
	private static final String AUTHORITY_TABLE_NAME = Authority.class.getAnnotation(Table.class).name();
	
	/** The Constant USERS_TABLE_NAME. */
	private static final String USERS_TABLE_NAME = User.class.getAnnotation(Table.class).name();

	/** The simple jdbc template. */
	@Autowired
	private SimpleJdbcTemplate simpleJdbcTemplate;

	/** The user service. */
	@Autowired
	private UserService userService;
	
	/** The user dao. */
	@Autowired
	private UserDao userDao;
	
	/** The authentication manager. */
	@Autowired
	private AuthenticationManager authenticationManager;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate, USERS_TABLE_NAME, AUTHORITY_TABLE_NAME);
	}

	/**
	 * Cannot alter nonexisting user.
	 */
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
	
	/**
	 * Cannot remove nonexisting user.
	 */
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

	/**
	 * Cannot alter nonexisting user password.
	 */
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

	/**
	 * Test add user.
	 * 
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
	@Test
	public void testAddUser() throws UsernameAlreadyExistsException {
		userService.addUser("user", "user password", Role.ROLE_USER);
		userService.addUser("admin", "admin password", Role.ROLE_ADMIN);
		checkUser("admin", "admin password", Role.ROLE_USER, Role.ROLE_ADMIN);
		checkUser("user", "user password", Role.ROLE_USER);
		Assert.assertEquals("The wrong number of users were found.", 2, SimpleJdbcTestUtils.countRowsInTable(simpleJdbcTemplate, USERS_TABLE_NAME));
	}

	/**
	 * Test add same user fails.
	 * 
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
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
	
	/**
	 * Test alter user.
	 * 
	 * @throws NoSuchUsernameException
	 *           the no such username exception
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
	@Test
	public void testAlterUser() throws NoSuchUsernameException, UsernameAlreadyExistsException {
		userService.addUser("me", "beef", Role.ROLE_ADMIN);
		userService.alterUser("me", "stew", Role.ROLE_USER);
		checkUser("me", "stew", Role.ROLE_USER);
		userService.alterUser("me", "cake", Role.ROLE_ADMIN);
		checkUser("me", "cake", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	/**
	 * Test alter user password.
	 * 
	 * @throws NoSuchUsernameException
	 *           the no such username exception
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
	@Test
	public void testAlterUserPassword() throws NoSuchUsernameException, UsernameAlreadyExistsException {
		userService.addUser("me", "beef", Role.ROLE_ADMIN);
		userService.alterPassword("me", "stew");
		checkUser("me", "stew", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	/**
	 * Test nonexistent user cannot login.
	 */
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

	/**
	 * Test inccorect password cannot login.
	 * 
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
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
	
	/**
	 * Test ensure default user exists.
	 */
	@Test
	public void testEnsureDefaultUserExists() {
		userService.ensureDefaultUsersExists("username", "password");
		checkUser("username", "password", Role.ROLE_USER, Role.ROLE_ADMIN);
		userService.ensureDefaultUsersExists("username", "password");
		checkUser("username", "password", Role.ROLE_USER, Role.ROLE_ADMIN);
	}

	/**
	 * Test remove user.
	 * 
	 * @throws NoSuchUsernameException
	 *           the no such username exception
	 * @throws UsernameAlreadyExistsException
	 *           the username already exists exception
	 */
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
	 * Check user.
	 * 
	 * @param username
	 *          the username
	 * @param password
	 *          the password
	 * @param expectedRoles
	 *          the expected roles
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

	/**
	 * Authenticate.
	 * 
	 * @param username
	 *          the username
	 * @param password
	 *          the password
	 * @return the authentication
	 */
	protected Authentication authenticate(String username, String password) {
		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}
	
}
