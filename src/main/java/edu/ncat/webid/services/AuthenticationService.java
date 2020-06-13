package edu.ncat.webid.services;

import javax.security.auth.Subject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.wymiwyg.wrhapi.Request;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

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
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	public Model register(User user) {
		String uri = user.getUri()+"profile.rdf";
		String foafns = "http://xmlns.com/foaf/0.1/";
		Model m = ModelFactory.createDefaultModel();
		
		m.add(m.createResource(uri+"#me"), m.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), foafns+"Person");
		m.add(m.createResource(uri+"#me"), m.createProperty(foafns+"givenName"), user.getFirstName());
		m.add(m.createResource(uri+"#me"), m.createProperty(foafns+"familyName"), user.getLastName());
		m.add(m.createResource(uri+"#me"), m.createProperty(foafns+"email"), user.getEmail());
		m.add(m.createResource(uri+"#me"), m.createProperty(foafns+"homepage"), user.getUri());
		
		return m;
	}
	
	public void cert() {
		
	}
	
}
