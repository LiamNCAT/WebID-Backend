package edu.ncat.webid.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import org.apache.jena.rdf.model.Model;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import edu.ncat.webid.jaxb.User;
import edu.ncat.webid.services.AuthenticationService;
import edu.ncat.webid.util.WebIDSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

	@InjectMocks AuthenticationService as;
	
	@Mock WebIDSecurityContext sec;
	@Mock Principal prin;
	
	User user;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		user = new User();
		user.setFirstName("William");
		user.setLastName("Nick");
		user.setEmail("williamnick44@gmail.com");
		user.setUri("http://www.dropbox.com/wnick");
		
	}
	
	@Test
	public void canAutheticateWithValidWebID() {
		//Mockito.when(sec.getUserPrincipal()).thenReturn(prin);
		//boolean auth = as.login(sec, req);
		//assertEquals(auth, true);
		
	}
	
	@Test
	public void canRegisterANewWebID() {
		Model m = as.register(user);
		assertNotNull(m);
	}
	
	@Test
	public void canCreateCerts() throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, OperatorCreationException, IOException {
		String URL = user.getUri()+"/profile.rdf";
		X509Certificate cert = as.cert(URL);
		assertNotNull(cert);
		cert.getSubjectAlternativeNames();
	}
	
}
