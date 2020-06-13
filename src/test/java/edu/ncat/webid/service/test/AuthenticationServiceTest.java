package edu.ncat.webid.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.security.Principal;

import javax.security.auth.x500.X500Principal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.wymiwyg.wrhapi.Request;

import com.hp.hpl.jena.rdf.model.Model;

import edu.ncat.webid.jaxb.User;
import edu.ncat.webid.services.AuthenticationService;
import edu.ncat.webid.util.WebIDSecurityContext;

public class AuthenticationServiceTest {

	AuthenticationService as;
	
	@Mock WebIDSecurityContext sec;
	@Mock Request req;
	
	Principal prin;
	
	User user;
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		as = new AuthenticationService();
		//InputStream is = this.getClass().getResourceAsStream("/edu/ncat/webid/resource/cert.pem");
		//prin = new X500Principal(is);
		
		user = new User();
		user.setFirstName("William");
		user.setLastName("Nick");
		user.setEmail("williamnick44@gmail.com");
		user.setUri("http://www.dropbox.com/wnick");
		
	}
	
	@Test
	public void canAutheticateWithValidWebID() {
		Mockito.when(sec.getUserPrincipal()).thenReturn(prin);
		//boolean auth = as.login(sec, req);
		//assertEquals(auth, true);
		
	}
	
	@Test
	public void canRegisterANewWebID() {
		Model m = as.register(user);
		assertNotNull(m);
	}
}
