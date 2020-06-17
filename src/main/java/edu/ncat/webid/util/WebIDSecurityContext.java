package edu.ncat.webid.util;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class WebIDSecurityContext implements SecurityContext{

	private Principal principal;

	@Context
	HttpServletRequest req;
	
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
		return req.isSecure();
		
	}

	@Override
	public String getAuthenticationScheme() {
		return req.getAuthType();
	}

}
