package uk.co.unclealex.hammers.calendar.client.security;


public class AuthenticationEvent {
  
  private final String username;
  
  public AuthenticationEvent(String username) {
		super();
		this.username = username;
	}

	public String getUsername() {
		return username;
	}  
}
