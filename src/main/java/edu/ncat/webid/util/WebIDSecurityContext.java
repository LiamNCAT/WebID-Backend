package edu.ncat.webid.util;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

public class WebIDSecurityContext implements SecurityContext{

	private Principal principal;
	
	public WebIDSecurityContext(Principal prin) {
		principal = prin;
	}
	
	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSecure() {
		
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		// TODO Auto-generated method stub
		return null;
	}

}
