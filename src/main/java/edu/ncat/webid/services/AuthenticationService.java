package edu.ncat.webid.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;


import edu.ncat.webid.jaxb.User;
import edu.ncat.webid.util.Biometrics;
import edu.ncat.webid.util.WebIDAuthentication;
import edu.ncat.webid.util.WebIDSecurityContext;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
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

/**
 * 
 * 
 * @author William Nick
 *
 */

@Path("/api/authentication")
public class AuthenticationService {
	@Context WebIDSecurityContext sec;
	@Context HttpServletRequest req;
	
	WebIDAuthentication webidAuth;
	
	/**
	 * Authenticates a user with a valid WebID
	 * @return true-if the WebID is valid
	 * @return false-if the WebID is invalid
	 * @throws CertificateParsingException
	 */
	
	@Path("/login")
	@POST
	public boolean login() throws CertificateParsingException {
		Subject subject = new Subject();
		subject.getPrincipals().add(sec.getUserPrincipal());
		
		webidAuth = new WebIDAuthentication();
		webidAuth.authenticate(subject, req);
		
		return true;
	}
	/**
	 * Registers the user of the WebID protocol and creates a FOAF profile
	 * @param user
	 * @return the FOAF profile for that particular user
	 */
	
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
	/**
	 * Creates a self-signed certificate for a WebID user
	 * @param URI the users server URI for the WebID user
	 * @return a X509 certificate that has the user's server as the Subject Alternative Name
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws OperatorCreationException
	 */
	
	
	@Path("/cert")
	@POST
	public X509Certificate cert(String URI) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, CertificateException, OperatorCreationException {
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
		
		GeneralNames subjectAltName = new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, URI));
		
		generator.addExtension(X509Extensions.SubjectAlternativeName, false, subjectAltName);
		
		byte[] serverChain = generator.build(signer).getEncoded();
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(serverChain));
	}
	@Path("/bio")
	@POST
	public boolean bioAuth(ArrayList<Double> fv, int id) throws CertificateParsingException {
		X509Certificate cert = ((X509Certificate[])req.getAttribute("javax.servlet.request.X509Certificate"))[0];
		Collection<List<?>> san = cert.getSubjectAlternativeNames();
		
		if(san == null) {
			return false;
		}
		
		
		RSAPublicKey pub = (RSAPublicKey) cert.getPublicKey();
		
		Iterator<List<?>> iter = san.iterator();
		List<?> sanInfo = null;
		
		if(iter.hasNext()) {
			sanInfo = iter.next();
		}
		Biometrics bio = new Biometrics(cert, id);
		
		double compare = bio.compareProbeToGallery(fv);
		
		if(compare >= 0.7 && compare <= 0.95) {
			return true;
		}
		
		return false;
	}
	
}
