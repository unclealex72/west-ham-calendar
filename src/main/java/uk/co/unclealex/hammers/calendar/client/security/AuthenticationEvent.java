package uk.co.unclealex.hammers.calendar.client.security;


public class AuthenticationEvent {
  
  private final String i_username;
  
  public AuthenticationEvent(String username) {
		super();
		i_username = username;
	}

	public String getUsername() {
		return i_username;
	}  
}
