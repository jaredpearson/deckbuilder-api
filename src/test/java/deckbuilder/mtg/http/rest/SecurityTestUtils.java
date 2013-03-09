package deckbuilder.mtg.http.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.SecurityContext;

import deckbuilder.mtg.entities.User;
import deckbuilder.mtg.http.Principal;

public class SecurityTestUtils {

	private SecurityTestUtils() {
	}
	
	public static SecurityContext mockSecurityContext(User user) {
		return mockSecurityContext(user, false);
	}
	
	public static SecurityContext mockSecurityContext(User user, boolean admin) {
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.isUserInRole("administrator")).thenReturn(admin);
		when(securityContext.getUserPrincipal()).thenReturn(new Principal(user));
		return securityContext;
	}
}
