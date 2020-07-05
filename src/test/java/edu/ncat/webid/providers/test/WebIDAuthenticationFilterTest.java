package edu.ncat.webid.providers.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.ncat.webid.providers.WebIDAuthenticationFilter;

public class WebIDAuthenticationFilterTest {
	@Mock ContainerRequestContext requestContext;
	
	X509Certificate [] certs;
	
	@InjectMocks WebIDAuthenticationFilter wiaf;
	
	@Before
	public void setup() throws NoSuchAlgorithmException, OperatorCreationException, IOException, CertificateException {
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
		
		byte[] serverChain = generator.build(signer).getEncoded();
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		
		certs[0] = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(serverChain));
	}
	
	@Test
	public void filterWorksWithCorrectWebID() throws IOException {
		Mockito.when(requestContext.getProperty("javax.servlet.request.X509Certificate")).thenReturn(certs);
		wiaf.filter(requestContext);
		assertNotNull(wiaf.getWisc());
	}
	
	@Test
	public void filterNullsTheSecurityContextWithoutACert() throws IOException {
		wiaf.filter(requestContext);
		assertNull(wiaf.getWisc());
	}
}
