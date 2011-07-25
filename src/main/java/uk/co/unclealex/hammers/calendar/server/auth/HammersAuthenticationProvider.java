package uk.co.unclealex.hammers.calendar.server.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class HammersAuthenticationProvider implements AuthenticationProvider {
	
	private static Map<String, String> users = new HashMap<String, String>();
	
	static {
		users.put("fabrizio", "javacodegeeks");
		users.put("justin", "javacodegeeks");
		users.put("alex", "alex");
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = (String) authentication.getPrincipal();
		String password = (String)authentication.getCredentials();
		
		if (users.get(username)==null)
			throw new UsernameNotFoundException("User not found");
		
		String storedPass = users.get(username);
		
		if (!storedPass.equals(password))
			throw new BadCredentialsException("Invalid password");
		
		Authentication customAuthentication = new HammersUserAuthentication("ROLE_USER", authentication);
		customAuthentication.setAuthenticated(true);
		
		return customAuthentication;
		
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
