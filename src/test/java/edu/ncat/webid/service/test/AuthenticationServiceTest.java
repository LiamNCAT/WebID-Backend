package edu.ncat.webid.service.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import edu.ncat.webid.jaxb.User;
import edu.ncat.webid.services.AuthenticationService;
import edu.ncat.webid.util.RDFTypes;
import edu.ncat.webid.util.WebIDAuthentication;
import edu.ncat.webid.util.WebIDSecurityContext;
import edu.ncat.webid.vocabulary.CERT;



public class AuthenticationServiceTest {

	@Mock WebIDSecurityContext sec;
	@Mock Principal prin;
	@Mock HttpServletRequest req;
	@InjectMocks AuthenticationService as;
		
	WireMockServer wm = new WireMockRule(options().port(8090).bindAddress("localhost"));
	
	User user;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		user = new User();
		user.setFirstName("William");
		user.setLastName("Nick");
		user.setEmail("williamnick44@gmail.com");
		user.setUri("http://www.dropbox.com/wnick");
		
		wm.start();
		
	}
	
	@Test
	public void canAutheticateWithValidWebID() throws CertificateException, IOException, OperatorCreationException, NoSuchAlgorithmException {
		
		X509Certificate [] certs = new X509Certificate[1];
		
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048, new SecureRandom()); // going beyond 2048 requires crypto extension
        KeyPair keys = gen.generateKeyPair();
		
		PrivateKey serverPrivateKey = keys.getPrivate();
		PublicKey serverPublicKey = keys.getPublic();
		
		ContentSigner signer = new JcaContentSignerBuilder("SHA1WithRSA").setProvider(new BouncyCastleProvider()).build(serverPrivateKey);
		
		X500Name serverSubjectName = new X500Name("CN=DamnDirtyApes");
		
		Date from = new Date();
		Date to = new Date();
		
		SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(serverPublicKey.getEncoded());
		BigInteger sn = new BigInteger(2048, new SecureRandom());
		
		X509v3CertificateBuilder generator = new X509v3CertificateBuilder(serverSubjectName, sn, from, to, serverSubjectName, subPubKeyInfo);
		GeneralNames subjectAltName = new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, "http://localhost:8090/wnick/profile.rdf#me"));
		
		generator.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAltName);
		
		byte[] serverChain = generator.build(signer).getEncoded();
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		
		certs[0] = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(serverChain));
		
		Model realData = ModelFactory.createDefaultModel();
		
		RSAPublicKey pub = (RSAPublicKey) certs[0].getPublicKey();
		
		String personURI = "http://localhost:8090/wnick/profile.rdf#me" ;
		
		realData.createResource(personURI)
				.addProperty(RDF.type, FOAF.Person) 
				.addProperty(CERT.key, 
					realData.createResource() 
						.addProperty(RDF.type, CERT.RSAPublicKey)
						.addProperty(CERT.modulus, pub.getModulus().toString())
						.addProperty(CERT.exponent, pub.getPublicExponent().toString()));
		
		Mockito.when(sec.getUserPrincipal()).thenReturn(prin);
		Mockito.when(req.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certs);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		realData.write(stream);
		
		String rdfData = new String(stream.toByteArray());
		
		wm.stubFor(get(urlEqualTo("/wnick/profile.rdf"))
				.willReturn(aResponse()
						.withHeader("Content-Type", RDFTypes.RDFXML)
						.withBody(rdfData)));
		
		boolean authed = as.login();
		assertTrue(authed);
		
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
	}
	
	
	@After
	public void shutdown() {
		wm.stop();
	}
	
}
