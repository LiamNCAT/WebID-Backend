package edu.ncat.webid.util;

import java.math.BigInteger;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

public class WebIDAuthentication {
	Model m;
	QueryExecution qexec;
	
	public WebIDAuthentication(){
		m = ModelFactory.createDefaultModel();
	}
	
	
	public boolean authenticate(Subject sub, HttpServletRequest req) throws CertificateParsingException {    
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
		
		
		m.read((String) sanInfo.get(1));
		
		ResultSet response = query(m);
		
		RDFNode mod = null;
		RDFNode exp = null;
		
		while(response.hasNext()) {
			QuerySolution sol = response.nextSolution();
			mod = sol.get("?mod");
			exp = sol.get("?exp");
		}
		
		BigInteger modulus = new BigInteger(mod.toString());
		BigInteger exponent = new BigInteger(exp.toString());
		
		if(modulus.compareTo(pub.getModulus()) == 0 && exponent.compareTo(pub.getPublicExponent())==0) {
			return true;
		}
		
		else {
			return false;
		}
	}
	
	public ResultSet query(Model m) {
		StringBuffer queryStr = new StringBuffer();
		ResultSet response;
		
		queryStr.append("PREFIX cert: <http://www.w3.org/ns/auth/cert#>");
		queryStr.append(" PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
		queryStr.append(" SELECT ?mod ?exp");
		queryStr.append(" WHERE { ?x a foaf:Person.");
		
		queryStr.append(" ?x cert:key ?k.");
		queryStr.append(" ?k cert:modulus ?mod.");
		queryStr.append(" ?k cert:exponent ?exp. }");
		
		Query query = QueryFactory.create(queryStr.toString());
		qexec = QueryExecutionFactory.create(query, m);
		
		try {
			   response = qexec.execSelect();
			   
		}
		catch(Exception e) {
			return null;
		}
		return response;
		
	}

}
