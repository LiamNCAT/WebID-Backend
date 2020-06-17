package edu.ncat.webid.util;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

public class WebIDAuthentication {
	
	public boolean authenticate(X509Certificate cert) throws CertificateParsingException {
		Collection<List<?>> san = cert.getSubjectAlternativeNames();
		
		if(san == null) {
			return false;
		}
		
		return false;
		
	}

}
