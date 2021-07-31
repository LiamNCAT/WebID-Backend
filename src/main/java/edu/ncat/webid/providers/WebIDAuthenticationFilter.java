package edu.ncat.webid.providers;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import edu.ncat.webid.util.WebIDSecurityContext;

@Provider
@Priority(Priorities.AUTHENTICATION)

/**
 * 
 * @author Bianca
 *
 */
public class WebIDAuthenticationFilter implements ContainerRequestFilter{

	@Context
	private ResourceInfo resourceInfo;
	
	private WebIDSecurityContext wisc;
		
	private static final ResponseBuilder ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED).entity("User not authenticated");
	//private static final ResponseBuilder ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN).entity("Access denied");
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Principal principal = getPrincipal(requestContext);
		

	    if (principal == null) {
	    	requestContext.abortWith(ACCESS_DENIED.build());
	      return;
	    }
	    
	    wisc = new WebIDSecurityContext(principal);
	    
	    requestContext.setSecurityContext(wisc);
		
	}

	private Principal getPrincipal(ContainerRequestContext requestContext) {
		X509Certificate[] certificates = (X509Certificate[]) requestContext.getProperty("javax.servlet.request.X509Certificate");

	    if (certificates != null && certificates.length > 0) {
	      return certificates[0].getSubjectX500Principal();
	    } 
	    else {
	      return null;
	    }
	}
	
	public WebIDSecurityContext getWisc() {
		return wisc;
	}
	  
}
