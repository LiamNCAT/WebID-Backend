package edu.ncat.webid.util.test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import edu.ncat.webid.util.Biometrics;

public class BiometricsTest {
	
	Biometrics bio;
	
	WireMockServer wm = new WireMockRule(options().port(8090).bindAddress("localhost"));

	@Before
	public void setup() throws NoSuchAlgorithmException, OperatorCreationException, IOException, CertificateException {
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
		
		bio = new Biometrics(certs[0], 1);
	}
	
	@Test
	public void validDistance() {
		
	}
}
