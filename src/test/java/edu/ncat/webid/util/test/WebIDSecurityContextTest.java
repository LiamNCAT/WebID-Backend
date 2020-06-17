package edu.ncat.webid.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import edu.ncat.webid.util.WebIDSecurityContext;

@RunWith(MockitoJUnitRunner.class)
public class WebIDSecurityContextTest {
	
	@Mock HttpServletRequest req;
	@Mock Principal prin;
	@InjectMocks WebIDSecurityContext wisc;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void authenticationSchemeTest() {
		Mockito.when(req.getAuthType()).thenReturn("CONFIDENTIAL");
		String authType = wisc.getAuthenticationScheme();
		assertEquals(authType, "CONFIDENTIAL");
	}
	
	@Test
	public void secureTest() {
		Mockito.when(req.isSecure()).thenReturn(true);
		boolean secure = wisc.isSecure();
		assertTrue(secure);
	}

}
