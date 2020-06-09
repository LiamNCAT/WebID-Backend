package edu.ncat.webid.services;

import javax.security.auth.Subject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.wymiwyg.wrhapi.Request;

import edu.ncat.webid.jaxb.User;
import edu.ncat.webid.util.WebIDSecurityContext;

import org.apache.clerezza.foafssl.auth.FoafSslAuthentication;

/**
 * 
 * @author William Nick
 *
 */

@Path("/api/authentication")
public class AuthenticationService {
	
	@Path("/login")
	@POST
	public boolean login(@Context WebIDSecurityContext sec, @Context Request request) {
		Subject subject = new Subject();
		subject.getPrincipals().add(sec.getUserPrincipal());
		FoafSslAuthentication webid = new FoafSslAuthentication();
		return webid.authenticate(request, subject);
	}
	@Path("/register")
	@POST
	public Model register(User user) {
		
	}
	
	public void cert() {
		
	}
	
}
