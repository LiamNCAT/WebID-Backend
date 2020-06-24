package edu.ncat.webid.util.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import edu.ncat.webid.util.WebIDAuthentication;
import edu.ncat.webid.vocabulary.CERT;

@RunWith(MockitoJUnitRunner.class)
public class WebIDAuthenticationTest {
	@Mock Subject sub;
	@Mock HttpServletRequest req;
	
	@InjectMocks WebIDAuthentication webidAuth;
	X509Certificate [] certs;
	
	@Before
	public void setup() throws OperatorCreationException, NoSuchAlgorithmException, IOException, CertificateException {
		MockitoAnnotations.initMocks(this);
		certs = new X509Certificate[1];
		
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
		
		generator.addExtension(Extension.subjectAlternativeName, false, new DERBMPString(""));
		
		byte[] serverChain = generator.build(signer).getEncoded();
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		
		certs[0] = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(serverChain));
	}
	
	@Test
	public void WebIDAuthenticatesProperly() throws CertificateParsingException {
		Mockito.when(req.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certs);
		boolean authed = webidAuth.authenticate(sub, req);
	}
	
	@Test
	public void WebIDDoesNotAuthenticateWhenSubjectDoesNotImply() throws CertificateParsingException{
		Mockito.when(req.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(certs);
		//Mockito.when(certs[0].getSubjectAlternativeNames()).thenReturn(null);
		
		boolean authed = webidAuth.authenticate(sub, req);
		
		assertFalse(authed);
	}
	
	@Test
	public void WebIDQueryIsSuccessful() {
		RSAPublicKey pub = (RSAPublicKey) certs[0].getPublicKey();
		
		Model m = ModelFactory.createDefaultModel();
		
		String personURI = "http://www.dropbox.com/wnick/profile.rdf#me" ;
		
		m.createResource(personURI)
				.addProperty(RDF.type, FOAF.Person) 
				.addProperty(CERT.key, 
					m.createResource() 
						.addProperty(RDF.type, CERT.RSAPublicKey)
						.addProperty(CERT.modulus, pub.getModulus().toString())
						.addProperty(CERT.exponent, pub.getPublicExponent().toString()));
		
		ResultSet response = webidAuth.query(m);
		
		assertNotNull(response);

	}
	
}
