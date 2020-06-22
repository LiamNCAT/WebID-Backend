package edu.ncat.webid.util;

import java.security.PublicKey;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jena.rdf.model.Model;

public class WebIDAuthentication {
	
	
	
	public boolean authenticate(Subject sub, HttpServletRequest req) throws CertificateParsingException {
		
		X509Certificate cert = null;
		
		Collection<List<?>> san = cert.getSubjectAlternativeNames();
		
		if(san == null) {
			return false;
		}
		
		
		
		PublicKey pk = cert.getPublicKey();
		
		
		
		
		return false;
		
	}
	
	public ResultSet query(Model m) {
		return null;
		
	}

}
